//? if >=1.20.1 && !forge {
/*package xyz.pupbrained.drop_confirm.config.screens

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
*///?} else {
package xyz.pupbrained.drop_confirm.config.screens

//? if >=1.18.2 {
import net.minecraft.client.gui.narration.NarratableEntry.NarrationPriority
import net.minecraft.client.gui.narration.NarrationElementOutput
//?}
//? if >=1.15.2 {
import xyz.pupbrained.drop_confirm.config.widgets.ModernButtonControl as ButtonControl
import xyz.pupbrained.drop_confirm.config.widgets.ModernSliderControl as SliderControl
//?} else {
/*import com.gitlab.cdagaming.unilib.utils.gui.controls.SliderControl
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl as ButtonControl
*///?}
import com.gitlab.cdagaming.unilib.utils.gui.controls.CheckBoxControl
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen
import io.github.cdagaming.unicore.impl.Pair
import io.github.cdagaming.unicore.utils.StringUtils
import net.minecraft.client.gui.screens.Screen
import xyz.pupbrained.drop_confirm.DropConfirm.TRANSLATOR
import xyz.pupbrained.drop_confirm.config.DropConfirmConfig

/**
 *  UniLib Config Screen
 *  @param parentScreen The screen to return to when closing
 */
class DropConfirmConfigScreen(parentScreen: Screen) : ExtendedScreen("DropConfirm") {
  companion object {
    private const val CONTROL_WIDTH = 150
    private const val CONTROL_HEIGHT = 20
    private const val CONTROL_SPACING = 4
    private const val CHECKBOX_WIDTH = 16
    private const val BUTTON_SPACING = 8
    private const val TITLE_SPACE = 25

    private const val ENABLED_KEY = "option.drop_confirm.enabled"
    private const val PLAY_SOUNDS_KEY = "option.drop_confirm.play_sounds"
    private const val DELAY_KEY = "option.drop_confirm.confirmation_reset_delay"
  }

  //? if >=1.18.2 {
  override fun updateNarration(output: NarrationElementOutput) {}
  override fun narrationPriority() = NarrationPriority.NONE
  //?}

  private val config = DropConfirmConfig
  private val previousScreen = parentScreen

  private val originalEnabled = config.enabled
  private val originalPlaySounds = config.shouldPlaySounds
  private val originalResetDelay = config.confirmationResetDelay

  private lateinit var enabledCheckbox: CheckBoxControl
  private lateinit var playSoundsCheckbox: CheckBoxControl
  private lateinit var delaySlider: SliderControl

  private val buttonY: Int
    get() = this.height - CONTROL_HEIGHT - BUTTON_SPACING

  private val groupStartX: Int
    get() = this.width / 2 - ((CONTROL_WIDTH * 2) + BUTTON_SPACING) / 2

  private fun calculateControlPosition(index: Int): Pair<Int, Int> {
    val x = this.width / 2 - CONTROL_WIDTH / 2
    val y = centeredStartY + (CONTROL_HEIGHT + CONTROL_SPACING) * index
    return Pair(x, y)
  }

  private fun createCheckbox(
    position: Pair<Int, Int>,
    text: String,
    isChecked: Boolean,
    onChange: () -> Unit,
    onHover: () -> Unit
  ): CheckBoxControl {
    return CheckBoxControl(
      position.first - (fontRenderer.width(text) + CHECKBOX_WIDTH) / 2 + CONTROL_WIDTH / 2,
      position.second,
      text,
      isChecked,
      onChange,
      onHover
    )
  }

  private fun addControls() {
    addControl(enabledCheckbox)
    addControl(playSoundsCheckbox)
    addControl(delaySlider)
  }

  private fun addActionButtons() {
    // Cancel button
    addControl(
      ButtonControl(
        groupStartX,
        buttonY,
        CONTROL_WIDTH,
        CONTROL_HEIGHT,
        TRANSLATOR.translate("option.drop_confirm.cancel")
      ) {
        resetConfigToOriginalValues()
        minecraft?.setScreen(previousScreen)
      }
    )

    // Save button
    addControl(
      ButtonControl(
        groupStartX + CONTROL_WIDTH + BUTTON_SPACING,
        buttonY,
        CONTROL_WIDTH,
        CONTROL_HEIGHT,
        TRANSLATOR.translate("option.drop_confirm.save_and_close")
      ) {
        DropConfirmConfig.save()
        minecraft?.setScreen(previousScreen)
      }
    )
  }

  // Simplified centeredStartY calculation
  private val centeredStartY: Int
    get() {
      val totalControlsHeight = (CONTROL_HEIGHT * 3) + (CONTROL_SPACING * 2)
      val availableSpace = buttonY - TITLE_SPACE
      return TITLE_SPACE + (availableSpace - totalControlsHeight) / 2.coerceAtLeast(0)
    }

  override fun initializeUi() {
    // Create UI elements with clear positioning
    val pos1 = calculateControlPosition(0)
    val pos2 = calculateControlPosition(1)
    val pos3 = calculateControlPosition(2)

    enabledCheckbox = createCheckbox(
      pos1,
      TRANSLATOR.translate(ENABLED_KEY),
      config.enabled,
      { config.enabled = !config.enabled },
      { drawMultiLineString(StringUtils.splitTextByNewLine(TRANSLATOR.translate("$ENABLED_KEY.description"))) }
    )

    playSoundsCheckbox = createCheckbox(
      pos2,
      TRANSLATOR.translate(PLAY_SOUNDS_KEY),
      config.shouldPlaySounds,
      { config.shouldPlaySounds = !config.shouldPlaySounds },
      { drawMultiLineString(StringUtils.splitTextByNewLine(TRANSLATOR.translate("$PLAY_SOUNDS_KEY.description"))) }
    )

    delaySlider = SliderControl(
      Pair(pos3.first, pos3.second),
      Pair(CONTROL_WIDTH, CONTROL_HEIGHT),
      config.confirmationResetDelay,
      1.0f, 5.0f, 0.1f,
      TRANSLATOR.translate(DELAY_KEY)
    ).apply {
      setOnSlide { config.confirmationResetDelay = this.sliderValue }
      setOnHover { drawMultiLineString(StringUtils.splitTextByNewLine(TRANSLATOR.translate("$DELAY_KEY.description"))) }
      valueFormat += "s"  // Add seconds indicator
    }

    addControls()
    addActionButtons()

    super.initializeUi()
  }

  private fun resetConfigToOriginalValues() {
    config.enabled = originalEnabled
    config.shouldPlaySounds = originalPlaySounds
    config.confirmationResetDelay = originalResetDelay
  }
}
//?}
