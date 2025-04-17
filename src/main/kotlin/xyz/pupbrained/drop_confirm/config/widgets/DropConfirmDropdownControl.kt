//? if <1.20.1 || forge {
/*package xyz.pupbrained.drop_confirm.config.widgets

import com.gitlab.cdagaming.unilib.ModUtils
import com.mojang.blaze3d.platform.InputConstants
import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.narration.NarratedElementType
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.resources.sounds.SimpleSoundInstance
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import kotlin.math.floor

class DropConfirmDropdownControl(
  x: Int,
  y: Int,
  width: Int,
  height: Int,
  private val placeholder: Component,
  private var options: List<Component>,
  initialIndex: Int = -1,
  private val onOptionSelected: OnOptionSelected
) : AbstractWidget(x, y, width, height, Component.empty()) {
  companion object {
    private const val DROPDOWN_BG_COLOR = 0xFF000000.toInt()
    private const val DROPDOWN_HIGHLIGHT_COLOR = 0xA0FFFFFF.toInt()
    private const val OPTION_TEXT_COLOR = 0xFFE0E0E0.toInt()
    private const val OPTION_PADDING_Y = 2
    private const val OPTION_PADDING_X = 4
    private const val DROPDOWN_GAP = 1
    private const val ARROW_WIDTH = 20
  }

  private val mc: Minecraft = ModUtils.getMinecraft()
  private val font = mc.font
  private val optionHeight get() = font.lineHeight + (OPTION_PADDING_Y * 2)

  private val listX get() = x
  private val listY get() = y + height + DROPDOWN_GAP
  private val listWidth get() = width

  private var isHoveredWidget = false
  var isExpanded = false
    set(value) {
      if (field != value) {
        field = value
        arrowButton.message = if (value) Component.literal("▼") else Component.literal("▶")
        keyboardHighlightIndex = if (value && options.isNotEmpty())
          if (selectedOptionIndex in options.indices) selectedOptionIndex else 0
        else -1
      }
    }

  private var keyboardHighlightIndex: Int = -1

  var selectedOptionIndex = initialIndex
    set(value) {
      val newIndex = if (value in options.indices) value else -1
      if (field != newIndex) {
        field = newIndex
        textButton.message = getFormattedMessage()
        if (isExpanded && textButton.isFocused)
          keyboardHighlightIndex = newIndex
      }
    }

  private val textButton = Button.Builder(getFormattedMessage()) { toggleExpand() }
    .pos(x, y)
    .size(width - ARROW_WIDTH, height)
    .build()

  private val arrowButton = Button.Builder(Component.literal("▶")) { toggleExpand() }
    .pos(x + width - ARROW_WIDTH, y)
    .size(ARROW_WIDTH, height)
    .build()

  private fun getFormattedMessage(): Component =
    if (selectedOptionIndex in options.indices)
      placeholder.copy().append(": ").append(options[selectedOptionIndex])
    else
      placeholder

  private fun playInteractionSound() =
    mc.soundManager.play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f))

  private fun toggleExpand() {
    if (active && visible) {
      isExpanded = !isExpanded
      playInteractionSound()
    }
  }

  private fun collapse() {
    if (isExpanded) isExpanded = false
  }

  private fun selectOption(indexToSelect: Int) {
    if (indexToSelect in options.indices) {
      selectedOptionIndex = indexToSelect
      onOptionSelected.onSelect(this, selectedOptionIndex, options[selectedOptionIndex])
    }
    collapse()
  }

//  fun setOnHover(callback: () -> Unit): DropConfirmDropdownControl {
//    this.onHoverCallback = callback
//    return this
//  }
//
//  var onHoverCallback: (() -> Unit)? = null

  override fun renderWidget(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
    updateButtonPositions()

    isHoveredWidget = isMouseOverButton(mouseX.toDouble(), mouseY.toDouble())

//    if (isHoveredWidget) {
//      onHoverCallback?.invoke()
//    }

    val shouldHighlight = isHoveredWidget || isFocused
    textButton.isFocused = shouldHighlight
    arrowButton.isFocused = shouldHighlight

    textButton.render(poseStack, mouseX, mouseY, partialTick)
    arrowButton.render(poseStack, mouseX, mouseY, partialTick)
  }

  private fun isMouseOverButton(mouseX: Double, mouseY: Double): Boolean =
    textButton.isMouseOver(mouseX, mouseY) || arrowButton.isMouseOver(mouseX, mouseY)

  private fun updateButtonPositions() {
    textButton.x = x
    textButton.y = y
    textButton.setWidth(width - ARROW_WIDTH)

    arrowButton.x = x + width - ARROW_WIDTH
    arrowButton.y = y
  }

  fun renderDropdownIfNeeded(poseStack: PoseStack, mouseX: Int, mouseY: Int) {
    if (!isExpanded || !visible || options.isEmpty()) return

    val listHeight = options.size * optionHeight

    fill(
      poseStack,
      listX, listY,
      listX + listWidth, listY + listHeight,
      DROPDOWN_BG_COLOR
    )

    options.forEachIndexed { index, option ->
      val textMinX = listX + OPTION_PADDING_X
      val textMaxX = listX + listWidth - OPTION_PADDING_X
      val rowMinY = listY + index * optionHeight
      val rowMaxY = rowMinY + optionHeight

      val isMouseHovering = mouseX >= listX && mouseX < listX + listWidth &&
        mouseY >= rowMinY && mouseY < rowMaxY
      val isKeyboardHighlighted = isFocused && index == keyboardHighlightIndex

      if (isMouseHovering || isKeyboardHighlighted) {
        fill(
          poseStack,
          listX + 1, rowMinY,
          listX + listWidth - 1, rowMaxY,
          DROPDOWN_HIGHLIGHT_COLOR
        )
      }

      renderScrollingString(
        poseStack,
        font,
        option,
        textMinX,
        rowMinY,
        textMaxX,
        rowMaxY,
        OPTION_TEXT_COLOR
      )
    }
  }

  override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
    if (!active || !visible || button != InputConstants.MOUSE_BUTTON_LEFT)
      return false

    if (textButton.isMouseOver(mouseX, mouseY) || arrowButton.isMouseOver(mouseX, mouseY)) {
      toggleExpand()
      return true
    }

    if (isExpanded && isMouseOverDropdown(mouseX, mouseY)) {
      val clickedIndex = floor((mouseY - listY) / optionHeight).toInt()
      selectOption(clickedIndex)
      playInteractionSound()
      return true
    }

    if (isExpanded) {
      collapse()
      return false
    }

    return false
  }

  fun isMouseOverDropdown(mouseX: Double, mouseY: Double): Boolean {
    if (!isExpanded || !visible || options.isEmpty()) return false

    return mouseX >= listX &&
      mouseX < listX + listWidth &&
      mouseY >= listY &&
      mouseY < listY + options.size * optionHeight
  }

  override fun isMouseOver(mouseX: Double, mouseY: Double): Boolean =
    isMouseOverButton(mouseX, mouseY) || (isExpanded && isMouseOverDropdown(mouseX, mouseY))

  override fun setFocused(focused: Boolean) {
    super.setFocused(focused)
    if (!focused && isExpanded) collapse()
  }

  override fun keyPressed(keyCode: Int, scanCode: Int, modifiers: Int): Boolean {
    if (!active || !visible) return false
    if (options.isEmpty() && keyCode != InputConstants.KEY_ESCAPE) return false

    return when (keyCode) {
      InputConstants.KEY_RETURN, InputConstants.KEY_SPACE, InputConstants.KEY_NUMPADENTER -> {
        if (!isExpanded) {
          toggleExpand()
        } else if (keyboardHighlightIndex in options.indices) {
          selectOption(keyboardHighlightIndex)
          playInteractionSound()
        } else {
          collapse()
          playInteractionSound()
        }
        true
      }

      InputConstants.KEY_ESCAPE -> {
        if (isExpanded) {
          collapse()
          playInteractionSound()
          true
        } else false
      }

      InputConstants.KEY_DOWN -> {
        if (isExpanded) {
          keyboardHighlightIndex = (keyboardHighlightIndex + 1) % options.size
          playInteractionSound()
          true
        } else false
      }

      InputConstants.KEY_UP -> {
        if (isExpanded) {
          keyboardHighlightIndex = (keyboardHighlightIndex - 1 + options.size) % options.size
          playInteractionSound()
          true
        } else false
      }

      else -> false
    }
  }

  override fun setX(x: Int) {
    super.setX(x)
    updateButtonPositions()
  }

  override fun setY(y: Int) {
    super.setY(y)
    updateButtonPositions()
  }

  override fun setWidth(width: Int) {
    super.setWidth(width)
    updateButtonPositions()
  }

  override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {
    narrationElementOutput.add(
      NarratedElementType.TITLE,
      Component.translatable("gui.narrate.editBox.title", getFormattedMessage())
    )

    if (active) {
      val state = if (isExpanded)
        Component.translatable("narration.dropdown.expanded")
      else
        Component.translatable("narration.dropdown.collapsed")

      narrationElementOutput.add(NarratedElementType.HINT, state)

      if (isFocused) {
        narrationElementOutput.add(
          NarratedElementType.USAGE,
          Component.translatable("narration.dropdown.usage.toggle")
        )

        if (isExpanded && options.isNotEmpty()) {
          narrationElementOutput.add(
            NarratedElementType.USAGE,
            Component.translatable("narration.dropdown.usage.navigation")
          )

          if (keyboardHighlightIndex in options.indices) {
            narrationElementOutput.add(
              NarratedElementType.HINT,
              Component.translatable(
                "narration.dropdown.usage.highlighted",
                options[keyboardHighlightIndex],
                keyboardHighlightIndex + 1,
                options.size
              )
            )
          }
        }
      } else if (isHovered) {
        narrationElementOutput.add(
          NarratedElementType.HINT,
          Component.translatable("narration.dropdown.usage.hover")
        )
      }
    } else {
      narrationElementOutput.add(
        NarratedElementType.TITLE,
        Component.translatable("gui.narrate.button.disabled", getFormattedMessage())
      )
    }
  }

  fun interface OnOptionSelected {
    fun onSelect(button: DropConfirmDropdownControl, index: Int, selectedOption: Component)
  }
}
*///?}
