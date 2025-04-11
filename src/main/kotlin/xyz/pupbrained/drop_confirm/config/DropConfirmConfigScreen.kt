//? if >=1.20.1 && !forge {
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
import xyz.pupbrained.drop_confirm.config.DropConfirmConfig.HANDLER

object DropConfirmConfigScreen {
  @JvmStatic
  operator fun invoke(parent: Screen): Screen = YetAnotherConfigLib.create(HANDLER) { defaults, config, builder ->
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
        default = defaults.shouldPlaySounds,
        get = { config.shouldPlaySounds },
        set = { config.shouldPlaySounds = it },
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
          Minecraft.getInstance().apply { execute { setScreen(invoke(parent)) } }
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

//? if >=1.18.2 {
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority
import net.minecraft.client.gui.narration.NarrationElementOutput
//?}
//? if >=1.15.2 {
import xyz.pupbrained.drop_confirm.config.widgets.ModernButtonControl as ButtonControl
import xyz.pupbrained.drop_confirm.config.widgets.ModernSliderControl as SliderControl
//?} else {
/^import com.gitlab.cdagaming.unilib.utils.gui.controls.SliderControl
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl as ButtonControl
^///?}
import com.gitlab.cdagaming.unilib.utils.gui.controls.CheckBoxControl
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen
import io.github.cdagaming.unicore.impl.Pair
import net.minecraft.client.gui.screens.Screen
import xyz.pupbrained.drop_confirm.DropConfirm

class DropConfirmConfigScreen(parentScreen: Screen) : ExtendedScreen("DropConfirm") {
  //? if >=1.18.2 {
  override fun updateNarration(output: NarrationElementOutput) {}
  override fun narrationPriority() = NarrationPriority.NONE
  //?}

  private val config = DropConfirmConfig
  private val previousScreen = parentScreen

  private val originalEnabled = config.enabled
  private val originalPlaySounds = config.shouldPlaySounds
  private val originalResetDelay = config.confirmationResetDelay

  private val controlWidth = 150
  private val controlHeight = 20

  private var enabledCheckbox: CheckBoxControl? = null
  private var playSoundsCheckbox: CheckBoxControl? = null
  private var delaySlider: SliderControl? = null

  private val adjustedControlSpacing = 4
  private val checkboxWidth = 16

  private val buttonSpacing = 8

  private val buttonY: Int
    get() = this.height - controlHeight - 8

  private val groupStartX: Int
    get() = this.width / 2 - ((controlWidth * 2) + buttonSpacing) / 2

  private val centeredStartY: Int
    get() {
      val totalControls = 3
      val titleReservedSpace = 25 // Space reserved at the top

      val controlsTotalHeight = (controlHeight * totalControls) + (adjustedControlSpacing * (totalControls - 1))
      val availableSpaceForControls = buttonY - titleReservedSpace
      val topPadding = (availableSpaceForControls - controlsTotalHeight) / 2

      return titleReservedSpace + topPadding.coerceAtLeast(0)
    }

  override fun initializeUi() {
    val enabledText = DropConfirm.TRANSLATOR.translate("option.drop_confirm.enabled")
    val playSoundsText = DropConfirm.TRANSLATOR.translate("option.drop_confirm.play_sounds")

    enabledCheckbox = CheckBoxControl(
      this.width / 2 - (fontRenderer.width(enabledText) + checkboxWidth) / 2,
      centeredStartY,
      enabledText,
      config.enabled
    ) { config.enabled = !config.enabled }

    playSoundsCheckbox = CheckBoxControl(
      this.width / 2 - (fontRenderer.width(playSoundsText) + checkboxWidth) / 2,
      centeredStartY + controlHeight + adjustedControlSpacing,
      playSoundsText,
      config.shouldPlaySounds
    ) { config.shouldPlaySounds = !config.shouldPlaySounds }

    delaySlider = SliderControl(
      Pair(this.width / 2 - controlWidth / 2, centeredStartY + (controlHeight * 2) + (adjustedControlSpacing * 2)),
      Pair(controlWidth, controlHeight),
      config.confirmationResetDelay,
      1.0f,
      5.0f,
      0.1f,
      DropConfirm.TRANSLATOR.translate("option.drop_confirm.confirmation_reset_delay")
    ).apply {
      setOnSlide { config.confirmationResetDelay = this.sliderValue }
      valueFormat += "s" // Add 's' for seconds
    }

    addControl(enabledCheckbox!!)
    addControl(playSoundsCheckbox!!)
    addControl(delaySlider!!)

    addControl(
      ButtonControl(
        groupStartX,
        buttonY,
        controlWidth,
        controlHeight,
        DropConfirm.TRANSLATOR.translate("option.drop_confirm.cancel"),
        /^? if >=1.15.2 {^/onPushEvent =/^?}^/ {
          resetConfigToOriginalValues()
          minecraft?.setScreen(previousScreen)
        }
      )
    )

    addControl(
      ButtonControl(
        groupStartX + controlWidth + buttonSpacing,
        buttonY,
        controlWidth,
        controlHeight,
        DropConfirm.TRANSLATOR.translate("option.drop_confirm.save_and_close"),
        /^? if >=1.15.2 {^/onPushEvent =/^?}^/ {
          DropConfirmConfig.save()
          minecraft?.setScreen(previousScreen)
        }
      )
    )

    super.initializeUi()
  }

  private fun resetConfigToOriginalValues() {
    config.enabled = originalEnabled
    config.shouldPlaySounds = originalPlaySounds
    config.confirmationResetDelay = originalResetDelay
  }
}
*///?}
