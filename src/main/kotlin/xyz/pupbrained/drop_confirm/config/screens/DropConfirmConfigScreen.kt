//? if >=1.20.1 && !forge {
package xyz.pupbrained.drop_confirm.config.screens

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
/*package xyz.pupbrained.drop_confirm.config.screens

import xyz.pupbrained.drop_confirm.config.widgets.DropConfirmSliderControl as SliderControl
import xyz.pupbrained.drop_confirm.config.widgets.DropConfirmButtonControl as ButtonControl
import com.gitlab.cdagaming.unilib.utils.gui.controls.CheckBoxControl
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen
import io.github.cdagaming.unicore.impl.Pair
import io.github.cdagaming.unicore.utils.StringUtils
import net.minecraft.client.gui.screens.Screen
import xyz.pupbrained.drop_confirm.DropConfirm.TRANSLATOR
import xyz.pupbrained.drop_confirm.config.DropConfirmConfig
import xyz.pupbrained.drop_confirm.util.ComponentUtils // Ensure this import is correct

/^*
 * UniLib Config Screen
 * @param parentScreen The screen to return to when closing
 ^/
class DropConfirmConfigScreen(parentScreen: Screen) : ExtendedScreen("DropConfirm") {
  // --- Companion Object for Constants ---
  companion object {
    // Layout Constants
    private const val CONTROL_WIDTH = 150
    private const val CONTROL_HEIGHT = 20
    private const val CONTROL_SPACING = 6
    private const val CHECKBOX_WIDTH = 16 // Used in createCheckbox calculation
    private const val BUTTON_SPACING = 8
    private const val TITLE_SPACE = 25
    private const val CHECKBOX_HEIGHT = 12

    // Translation Key Constants
    private const val ENABLED_KEY = "option.drop_confirm.enabled"
    private const val PLAY_SOUNDS_KEY = "option.drop_confirm.play_sounds"
    private const val TREAT_AS_WHITELIST_KEY = "option.drop_confirm.treat_as_whitelist"
    private const val DELAY_KEY = "option.drop_confirm.confirmation_reset_delay"
  }

  // --- Instance State & Original Values ---
  private val config = DropConfirmConfig
  private val previousScreen = parentScreen

  private val originalEnabled = config.enabled
  private val originalPlaySounds = config.shouldPlaySounds
  private val originalTreatAsWhitelist = config.treatAsWhitelist
  private val originalResetDelay = config.confirmationResetDelay

  // --- GUI Widget Declarations ---
  private lateinit var enabledCheckbox: CheckBoxControl
  private lateinit var playSoundsCheckbox: CheckBoxControl
  private lateinit var treatAsWhitelistCheckbox: CheckBoxControl
  private lateinit var delaySlider: SliderControl
  private lateinit var listEditorButton: ButtonControl

  // --- Computed Properties (Layout & State) ---
  private val buttonY: Int
    get() = this.height - CONTROL_HEIGHT - BUTTON_SPACING

  private val groupStartX: Int
    get() = this.width / 2 - ((CONTROL_WIDTH * 2) + BUTTON_SPACING) / 2

  private val totalControlsStackHeight: Int
    get() {
      val checkboxStackHeight = 3 * CHECKBOX_HEIGHT
      val sliderAndButtonHeight = 2 * CONTROL_HEIGHT
      val totalGapsHeight = 4 * CONTROL_SPACING // Gap below each of 3 checkboxes, slider, button
      return checkboxStackHeight + sliderAndButtonHeight + totalGapsHeight
    }

  private val centeredStartY: Int
    get() = TITLE_SPACE + (buttonY - TITLE_SPACE - totalControlsStackHeight) / 2.coerceAtLeast(0)

  private val listTypeKey: String // Renamed from listType for clarity
    get() = "option.drop_confirm.${if (config.treatAsWhitelist) "white" else "black"}listed_items"

  // --- Core Lifecycle & Override Methods ---
  override fun initializeUi() {
    val startX = this.width / 2 - CONTROL_WIDTH / 2
    var currentY = centeredStartY

    // Enabled Checkbox
    enabledCheckbox = createCheckbox(
      Pair(startX, currentY),
      TRANSLATOR.translate(ENABLED_KEY),
      config.enabled,
      { config.enabled = !config.enabled },
      { drawMultiLineString(StringUtils.splitTextByNewLine(TRANSLATOR.translate("$ENABLED_KEY.description"))) }
    )
    currentY += CHECKBOX_HEIGHT + CONTROL_SPACING

    // Play Sounds Checkbox
    playSoundsCheckbox = createCheckbox(
      Pair(startX, currentY),
      TRANSLATOR.translate(PLAY_SOUNDS_KEY),
      config.shouldPlaySounds,
      { config.shouldPlaySounds = !config.shouldPlaySounds },
      { drawMultiLineString(StringUtils.splitTextByNewLine(TRANSLATOR.translate("$PLAY_SOUNDS_KEY.description"))) }
    )
    currentY += CHECKBOX_HEIGHT + CONTROL_SPACING

    // Treat as Whitelist Checkbox
    treatAsWhitelistCheckbox = createCheckbox(
      Pair(startX, currentY),
      TRANSLATOR.translate(TREAT_AS_WHITELIST_KEY),
      config.treatAsWhitelist,
      {
        config.treatAsWhitelist = !config.treatAsWhitelist
        // Update button using computed property
        if (::listEditorButton.isInitialized) {
          listEditorButton.message = ComponentUtils.translatable(listTypeKey)/^? if <=1.15.2 {^//^.string^//^?}^/
          listEditorButton.setOnHover {
            drawMultiLineString(
              StringUtils.splitTextByNewLine(
                TRANSLATOR.translate("$listTypeKey.description")
              )
            )
          }
        }
      },
      { drawMultiLineString(StringUtils.splitTextByNewLine(TRANSLATOR.translate("$TREAT_AS_WHITELIST_KEY.description"))) }
    )
    currentY += CHECKBOX_HEIGHT + CONTROL_SPACING

    // Delay Slider
    delaySlider = SliderControl(
      Pair(startX, currentY),
      Pair(CONTROL_WIDTH, CONTROL_HEIGHT),
      config.confirmationResetDelay,
      1.0f, 5.0f, 0.1f,
      TRANSLATOR.translate(DELAY_KEY)
    ).apply {
      setOnSlide { config.confirmationResetDelay = this.sliderValue }
      setOnHover { drawMultiLineString(StringUtils.splitTextByNewLine(TRANSLATOR.translate("$DELAY_KEY.description"))) }
      valueFormat += "s"
    }
    currentY += CONTROL_HEIGHT + CONTROL_SPACING

    // List Editor Button
    listEditorButton = ButtonControl(
      startX,
      currentY,
      CONTROL_WIDTH,
      CONTROL_HEIGHT,
      TRANSLATOR.translate(listTypeKey)
    ) {
      minecraft?.setScreen(DropConfirmListEditorScreen(this))
    }.apply {
      setOnHover {
        drawMultiLineString(
          StringUtils.splitTextByNewLine(
            TRANSLATOR.translate("$listTypeKey.description") // Use computed property
          )
        )
      }
    }

    addControls()
    addActionButtons()

    super.initializeUi()
  }

  // --- Helper Methods ---
  private fun createCheckbox(
    position: Pair<Int, Int>,
    text: String,
    isChecked: Boolean,
    onChange: () -> Unit,
    onHover: () -> Unit
  ): CheckBoxControl {
    // Calculate precise X for centered text + checkbox
    val textWidth = this.fontRenderer.width(text)
    val checkboxStartX =
      position.first + (CONTROL_WIDTH - (textWidth + CHECKBOX_WIDTH + 4)) / 2
    return CheckBoxControl(
      checkboxStartX,
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
    addControl(treatAsWhitelistCheckbox)
    addControl(delaySlider)
    addControl(listEditorButton)
  }

  private fun addActionButtons() {
    // Cancel Button
    addControl(
      ButtonControl(
        groupStartX,
        buttonY,
        CONTROL_WIDTH,
        CONTROL_HEIGHT,
        TRANSLATOR.translate("option.drop_confirm.cancel") // Maybe add key constant?
      ) {
        resetConfigToOriginalValues()
        minecraft?.setScreen(previousScreen)
      }
    )

    // Save Button
    addControl(
      ButtonControl(
        groupStartX + CONTROL_WIDTH + BUTTON_SPACING,
        buttonY,
        CONTROL_WIDTH,
        CONTROL_HEIGHT,
        TRANSLATOR.translate("option.drop_confirm.save_and_close") // Maybe add key constant?
      ) {
        DropConfirmConfig.save()
        minecraft?.setScreen(previousScreen)
      }
    )
  }

  private fun resetConfigToOriginalValues() {
    config.enabled = originalEnabled
    config.shouldPlaySounds = originalPlaySounds
    config.treatAsWhitelist = originalTreatAsWhitelist
    config.confirmationResetDelay = originalResetDelay
  }
}
*///?}
