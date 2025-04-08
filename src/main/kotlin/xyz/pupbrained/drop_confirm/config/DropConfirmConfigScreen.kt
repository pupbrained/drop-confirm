//? if >=1.20.1 {
package xyz.pupbrained.drop_confirm.config

import dev.isxander.yacl3.api.*
import dev.isxander.yacl3.gui.controllers.BooleanController
import dev.isxander.yacl3.gui.controllers.dropdown.ItemController
import dev.isxander.yacl3.gui.controllers.slider.DoubleSliderController
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items
import xyz.pupbrained.drop_confirm.config.DropConfirmConfig.Companion.GSON

object DropConfirmConfigScreen {
  @JvmStatic
  fun createScreen(parent: Screen): Screen = YetAnotherConfigLib.create(GSON) { defaults, config, builder ->
    fun String.t() = Component.translatable(this)
    fun String.desc() = OptionDescription.createBuilder().text(this.t()).build()

    fun <T : Any> option(
      key: String,
      default: T,
      get: () -> T,
      set: (T) -> Unit,
      controller: (Option<T>) -> Controller<T>,
    ): Option<T> = Option.createBuilder<T>()
      .name("option.drop_confirm.$key".t())
      .description("option.drop_confirm.$key.description".desc())
      .binding(default, get, set)
      .customController(controller)
      .build()

    val options = listOf(
      option(
        key = "enabled",
        default = defaults.enabled,
        get = { config.enabled },
        set = { config.enabled = it },
        controller = { BooleanController(it, true) }
      ),

      option(
        key = "play_sounds",
        default = defaults.playSounds,
        get = { config.playSounds },
        set = { config.playSounds = it },
        controller = { BooleanController(it, true) }
      ),

      option(
        key = "confirmation_reset_delay",
        default = defaults.confirmationResetDelay,
        get = { config.confirmationResetDelay },
        set = { config.confirmationResetDelay = it },
        controller = { DoubleSliderController(it, 1.0, 5.0, 0.05) }
      ),

      option(
        key = "treat_as_whitelist",
        default = defaults.treatAsWhitelist,
        get = { config.treatAsWhitelist },
        set = { value ->
          config.treatAsWhitelist = value
          Minecraft.getInstance().apply { execute { setScreen(createScreen(parent)) } }
        },
        controller = { BooleanController(it, true) }
      )
    )

    val listKey = if (config.treatAsWhitelist) "whitelisted" else "blacklisted"
    val itemList = ListOption.createBuilder<Item>().apply {
      name("option.drop_confirm.${listKey}_items".t())
      description("option.drop_confirm.${listKey}_items.description".desc())
      binding(defaults.blacklistedItems, { config.blacklistedItems }, { config.blacklistedItems = it })
      initial(Items.AIR)
      insertEntriesAtEnd(true)
      customController { ItemController(it) }
    }.build()

    builder.apply {
      title("config.drop_confirm.title".t())
      category(
        ConfigCategory.createBuilder().apply {
          name("category.drop_confirm.general".t())
          options(options)
          group(itemList)
        }.build()
      )
    }
  }.generateScreen(parent)
}
//?} else {
/*package xyz.pupbrained.drop_confirm.config

import com.gitlab.cdagaming.unilib.utils.gui.controls.CheckBoxControl
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl
import com.gitlab.cdagaming.unilib.utils.gui.controls.SliderControl
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen
import io.github.cdagaming.unicore.impl.Pair
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import xyz.pupbrained.drop_confirm.DropConfirm

class DropConfirmConfigScreen(parentScreen: Screen) : ExtendedScreen("DropConfirm") {

  private val config = DropConfirmConfig.get()

  // Store the parent screen to return to
  private val previousScreen: Screen = parentScreen

  // Store original values to restore on cancel
  private val originalEnabled: Boolean = config.enabled
  private val originalPlaySounds: Boolean = config.playSounds
  private val originalResetDelay: Float = config.confirmationResetDelay

  private val startY = 50
  private val controlSpacing = 24
  private val controlWidth = 150
  private val controlHeight = 20
  private val buttonWidth = 150
  private val buttonHeight = 20
  private val buttonSpacing = 4

  // Reference to UI controls that need updating if we cancel
  private var enabledCheckbox: CheckBoxControl? = null
  private var playSoundsCheckbox: CheckBoxControl? = null
  private var delaySlider: SliderControl? = null

  override fun initializeUi() {
    super.initializeUi()
    val centeredX = this.width / 2 - controlWidth / 2

    // Create and store references to controls
    enabledCheckbox = CheckBoxControl(
      0,
      centeredX,
      startY,
      DropConfirm.TRANSLATOR.translate("option.drop_confirm.enabled"),
      config.enabled
    ) { config.enabled = !config.enabled }

    playSoundsCheckbox = CheckBoxControl(
      1,
      centeredX,
      startY + controlSpacing,
      DropConfirm.TRANSLATOR.translate("option.drop_confirm.play_sounds"),
      config.playSounds
    ) { config.playSounds = !config.playSounds }

    delaySlider = SliderControl(
      3,
      Pair(centeredX, startY + (controlSpacing * 2)),
      Pair(controlWidth, controlHeight),
      config.confirmationResetDelay,
      1.0f,
      5.0f,
      0.1f,
      DropConfirm.TRANSLATOR.translate("option.drop_confirm.confirmation_reset_delay")
    ).apply {
      setOnSlide { config.confirmationResetDelay = this.sliderValue }
      valueFormat = "%.1fs"
    }

    // Add controls to screen
    addControl(enabledCheckbox!!)
    addControl(playSoundsCheckbox!!)
    addControl(delaySlider!!)

    val totalButtonWidth = (buttonWidth * 2) + buttonSpacing
    val groupStartX = this.width / 2 - totalButtonWidth / 2
    val buttonY = this.height - 30

    addControl(
      ExtendedButtonControl(
        groupStartX,
        buttonY,
        buttonWidth,
        buttonHeight,
        DropConfirm.TRANSLATOR.translate("option.drop_confirm.cancel"),
        {
          resetConfigToOriginalValues()
          Minecraft.getInstance().setScreen(previousScreen)
        }
      )
    )

    addControl(
      ExtendedButtonControl(
        groupStartX + buttonWidth + buttonSpacing,
        buttonY,
        buttonWidth,
        buttonHeight,
        DropConfirm.TRANSLATOR.translate("option.drop_confirm.save_and_close"),
        {
          DropConfirmConfig.save()
          Minecraft.getInstance().setScreen(previousScreen)
        }
      )
    )
  }

  private fun resetConfigToOriginalValues() {
    config.enabled = originalEnabled
    config.playSounds = originalPlaySounds
    config.confirmationResetDelay = originalResetDelay

    DropConfirmConfig.reload()
  }
}
*///?}
