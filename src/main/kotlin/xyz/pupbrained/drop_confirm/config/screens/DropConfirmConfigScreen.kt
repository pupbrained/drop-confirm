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
import xyz.pupbrained.drop_confirm.config.widgets.DropConfirmDropdownControl as DropdownControl
import com.gitlab.cdagaming.unilib.utils.gui.controls.CheckBoxControl
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen
import com.mojang.blaze3d.vertex.PoseStack
import io.github.cdagaming.unicore.impl.Pair
import io.github.cdagaming.unicore.utils.StringUtils
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarratableEntry
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.gui.screens.Screen
import xyz.pupbrained.drop_confirm.DropConfirm.TRANSLATOR
import xyz.pupbrained.drop_confirm.config.DropConfirmConfig
import xyz.pupbrained.drop_confirm.util.ComponentUtils

/^*
 * UniLib Config Screen
 * @param parentScreen The screen to return to when closing
 ^/
class DropConfirmConfigScreen(parentScreen: Screen) : ExtendedScreen("DropConfirm Settings") {
  // --- Companion Object for Constants ---
  companion object {
    // Layout Constants
    private const val CONTROL_WIDTH = 150
    private const val CONTROL_HEIGHT = 20
    private const val CONTROL_SPACING = 6
    private const val COLUMN_SPACING = 8
    private const val TITLE_SPACE = 25
    private const val CHECKBOX_HEIGHT = 12

    // Translation Key Constants
    private const val ENABLED_KEY = "option.drop_confirm.enabled"
    private const val PLAY_SOUNDS_KEY = "option.drop_confirm.play_sounds"
    private const val TREAT_AS_WHITELIST_KEY = "option.drop_confirm.treat_as_whitelist"
    private const val DELAY_KEY = "option.drop_confirm.confirmation_reset_delay"
    private const val CONFIRMATION_MODE_KEY = "option.drop_confirm.confirmation_mode" // Add this key
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
  private lateinit var confirmationModeDropdown: DropdownControl

  // --- Computed Properties (Layout & State) ---
  private val leftColumnX: Int
    get() = this.width / 2 - (COLUMN_SPACING / 2) - CONTROL_WIDTH

  private val rightColumnX: Int
    get() = this.width / 2 + (COLUMN_SPACING / 2)

  private val buttonY: Int
    get() = this.height - CONTROL_HEIGHT - COLUMN_SPACING

  private val totalControlsStackHeight: Int
    get() {
      // Calculate max height of the two columns
      val checkboxStackHeight = 3 * CHECKBOX_HEIGHT + 2 * CONTROL_SPACING
      val rightColumnHeight = 3 * CONTROL_HEIGHT + 2 * CONTROL_SPACING
      return maxOf(checkboxStackHeight, rightColumnHeight)
    }

  private val centeredStartY: Int
    get() = TITLE_SPACE + (buttonY - TITLE_SPACE - totalControlsStackHeight) / 2.coerceAtLeast(0)

  private val listTypeKey: String
    get() = "option.drop_confirm.${if (config.treatAsWhitelist) "white" else "black"}listed_items"

  override fun updateNarration(p0: NarrationElementOutput) {}

  override fun narrationPriority(): NarratableEntry.NarrationPriority = NarratableEntry.NarrationPriority.NONE

  override fun initializeUi() {
    // Start with a clean layout and work from left to right
    val startY = centeredStartY

    // Calculate positions
    val checkboxOffsetY = (CONTROL_HEIGHT - CHECKBOX_HEIGHT) / 2  // Center checkbox in control height

    // --- LEFT COLUMN ---
    val firstCheckboxY = startY + checkboxOffsetY
    enabledCheckbox = createCheckbox(
      Pair(leftColumnX, firstCheckboxY),
      TRANSLATOR.translate(ENABLED_KEY),
      config.enabled,
      { config.enabled = !config.enabled },
      { drawMultiLineString(StringUtils.splitTextByNewLine(TRANSLATOR.translate("$ENABLED_KEY.description"))) }
    ).apply {
      // Constrain width to prevent overflow
      this.width = CONTROL_WIDTH
    }

    // Second checkbox - Play Sounds
    val secondCheckboxY = startY + CONTROL_HEIGHT + CONTROL_SPACING + checkboxOffsetY
    playSoundsCheckbox = createCheckbox(
      Pair(leftColumnX, secondCheckboxY),
      TRANSLATOR.translate(PLAY_SOUNDS_KEY),
      config.shouldPlaySounds,
      { config.shouldPlaySounds = !config.shouldPlaySounds },
      { drawMultiLineString(StringUtils.splitTextByNewLine(TRANSLATOR.translate("$PLAY_SOUNDS_KEY.description"))) }
    ).apply {
      this.width = CONTROL_WIDTH
    }

    val thirdCheckboxY = startY + (2 * (CONTROL_HEIGHT + CONTROL_SPACING)) + checkboxOffsetY
    treatAsWhitelistCheckbox = createCheckbox(
      Pair(leftColumnX, thirdCheckboxY),
      TRANSLATOR.translate(TREAT_AS_WHITELIST_KEY),
      config.treatAsWhitelist,
      {
        config.treatAsWhitelist = !config.treatAsWhitelist
        // Update button text when whitelist/blacklist setting changes
        listEditorButton.message = ComponentUtils.translatable(listTypeKey)
      },
      { drawMultiLineString(StringUtils.splitTextByNewLine(TRANSLATOR.translate("$TREAT_AS_WHITELIST_KEY.description"))) }
    ).apply {
      this.width = CONTROL_WIDTH
    }

    // --- RIGHT COLUMN ---
    confirmationModeDropdown = DropdownControl(
      rightColumnX,
      startY,
      CONTROL_WIDTH,
      CONTROL_HEIGHT,
      ComponentUtils.translatable(CONFIRMATION_MODE_KEY),
      listOf(
        ComponentUtils.translatable("option.drop_confirm.confirmation_mode.dialog"),
        ComponentUtils.translatable("option.drop_confirm.confirmation_mode.hotbar"),
        ComponentUtils.translatable("option.drop_confirm.confirmation_mode.chat")
      ),
      -1,
      onOptionSelected = { _, index, selectedOption ->
        println("Selected option: ${selectedOption.string} at index $index")
      }
    ).apply {
      // TODO: fix rendering for this so it shows above the dropdown component itself
//      setOnHover {
//        drawMultiLineString(
//          StringUtils.splitTextByNewLine(
//            TRANSLATOR.translate("$CONFIRMATION_MODE_KEY.description")
//          )
//        )
//      }
    }

    delaySlider = SliderControl(
      Pair(rightColumnX, startY + CONTROL_HEIGHT + CONTROL_SPACING),
      Pair(CONTROL_WIDTH, CONTROL_HEIGHT),
      config.confirmationResetDelay,
      1.0f, 5.0f, 0.1f,
      TRANSLATOR.translate(DELAY_KEY)
    ).apply {
      setOnSlide { config.confirmationResetDelay = this.sliderValue }
      setOnHover { drawMultiLineString(StringUtils.splitTextByNewLine(TRANSLATOR.translate("$DELAY_KEY.description"))) }
      valueFormat += "s"
    }

    listEditorButton = ButtonControl(
      rightColumnX,
      startY + 2 * (CONTROL_HEIGHT + CONTROL_SPACING),
      CONTROL_WIDTH,
      CONTROL_HEIGHT,
      TRANSLATOR.translate(listTypeKey)
    ) {
      minecraft?.setScreen(DropConfirmListEditorScreen(this))
    }.apply {
      setOnHover {
        drawMultiLineString(
          StringUtils.splitTextByNewLine(
            TRANSLATOR.translate("$listTypeKey.description")
          )
        )
      }
    }

    addControls()
    addActionButtons()

    super.initializeUi()
  }

  override fun setFocused(control: net.minecraft.client.gui.components.events.GuiEventListener?) {
    if (focused != control && focused is AbstractWidget)
      focused?.isFocused = false

    super.setFocused(control)

    if (::confirmationModeDropdown.isInitialized && control != confirmationModeDropdown)
      confirmationModeDropdown.isExpanded = false
  }

  override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
    val isMouseOverDropdownList =
      ::confirmationModeDropdown.isInitialized &&
        confirmationModeDropdown.isExpanded &&
        confirmationModeDropdown.isMouseOverDropdown(mouseX.toDouble(), mouseY.toDouble())

    val (adjustedX, adjustedY) = if (isMouseOverDropdownList) -9999 to -9999 else mouseX to mouseY

    super.render(poseStack, adjustedX, adjustedY, partialTick)

    if (::confirmationModeDropdown.isInitialized && confirmationModeDropdown.isExpanded) {
      confirmationModeDropdown.renderDropdownIfNeeded(poseStack, mouseX, mouseY)
    }
  }

  // --- Helper Methods ---
  private fun createCheckbox(
    position: Pair<Int, Int>,
    text: String,
    isChecked: Boolean,
    onChange: () -> Unit,
    onHover: () -> Unit
  ): CheckBoxControl =
    CheckBoxControl(
      position.first,
      position.second,
      text,
      isChecked,
      onChange,
      onHover
    ).apply {
      width = CONTROL_WIDTH
    }

  private fun addControls() {
    addControl(enabledCheckbox)
    addControl(playSoundsCheckbox)
    addControl(treatAsWhitelistCheckbox)
    addControl(confirmationModeDropdown)
    addControl(delaySlider)
    addControl(listEditorButton)
  }

  private fun addActionButtons() {
    // Center the action buttons at the bottom
    val buttonGroupStartX = this.width / 2 - ((CONTROL_WIDTH * 2) + COLUMN_SPACING) / 2

    // Cancel Button
    addControl(
      ButtonControl(
        buttonGroupStartX,
        buttonY,
        CONTROL_WIDTH,
        CONTROL_HEIGHT,
        TRANSLATOR.translate("option.drop_confirm.cancel")
      ) {
        resetConfigToOriginalValues()
        minecraft?.setScreen(previousScreen)
      }
    )

    // Save Button
    addControl(
      ButtonControl(
        buttonGroupStartX + CONTROL_WIDTH + COLUMN_SPACING,
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

  override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean =
    if (
      ::confirmationModeDropdown.isInitialized &&
      confirmationModeDropdown.isExpanded &&
      confirmationModeDropdown.isMouseOverDropdown(mouseX, mouseY)
    )
      confirmationModeDropdown.mouseClicked(mouseX, mouseY, button)
    else
      super.mouseClicked(mouseX, mouseY, button)

  override fun mouseDragged(mouseX: Double, mouseY: Double, button: Int, dragX: Double, dragY: Double): Boolean =
    if (
      ::confirmationModeDropdown.isInitialized &&
      confirmationModeDropdown.isExpanded &&
      confirmationModeDropdown.isMouseOverDropdown(mouseX, mouseY)
    )
      true
    else
      super.mouseDragged(mouseX, mouseY, button, dragX, dragY)

  override fun mouseReleased(mouseX: Double, mouseY: Double, button: Int): Boolean =
    if (
      ::confirmationModeDropdown.isInitialized &&
      confirmationModeDropdown.isExpanded &&
      confirmationModeDropdown.isMouseOverDropdown(mouseX, mouseY)
    )
      confirmationModeDropdown.mouseReleased(mouseX, mouseY, button)
    else
      super.mouseReleased(mouseX, mouseY, button)

  override fun mouseMoved(mouseX: Double, mouseY: Double) {
    if (
      ::confirmationModeDropdown.isInitialized &&
      confirmationModeDropdown.isExpanded &&
      confirmationModeDropdown.isMouseOverDropdown(mouseX, mouseY)
    ) return

    super.mouseMoved(mouseX, mouseY)
  }

  private fun resetConfigToOriginalValues() {
    config.enabled = originalEnabled
    config.shouldPlaySounds = originalPlaySounds
    config.treatAsWhitelist = originalTreatAsWhitelist
    config.confirmationResetDelay = originalResetDelay
  }
}
*///?}
