//? if <=1.21.6-alpha.25.15.a {
package xyz.pupbrained.drop_confirm.screens

//? if >=1.20.1 {
import net.minecraft.client.gui.GuiGraphics as PoseStack
import xyz.pupbrained.drop_confirm.platform.impl.GuiGraphicsRenderImpl
//?} elif >=1.16.5 {
/*import com.mojang.blaze3d.vertex.PoseStack
import xyz.pupbrained.drop_confirm.platform.impl.PoseStackRenderImpl
*///?} else {
/*import xyz.pupbrained.drop_confirm.platform.impl.LegacyRenderImpl
*///?}

//? if >=1.16.5 {
import net.minecraft.network.chat.Component as Text
//?} else {
/*import kotlin.String as Text
import kotlin.String
*///?}

//? if fabric {
import net.minecraft.client.gui.components./*? if <=1.18.2 {*//*Widget as*//*?}*/ Renderable
//?}

import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.screens.Screen
import xyz.pupbrained.drop_confirm.platform.RenderInterface
import xyz.pupbrained.drop_confirm.util.ComponentUtils

class PopupScreen(private val displayMessage: String) : Screen(ComponentUtils.literal("Popup Screen")) {
  //? if <=1.15.2
  /*@Suppress("UNUSED_PARAMETER")*/
  private fun getRenderImpl(context: Any? = null): RenderInterface {
    //? if >=1.20.1 {
    return GuiGraphicsRenderImpl(context as PoseStack)
    //?} else if >=1.16.5 {
    /*return PoseStackRenderImpl(context as PoseStack)
    *///?} else {
    /*return LegacyRenderImpl()
    *///?}
  }

  companion object {
    // Background
    /*? if <=1.20.1 {*//*const val DIMMING = 0xC0101010.toInt()*//*?}*/

    // Popup Structure
    const val BORDER = 0xDD9BA8FF.toInt()
    const val TRANSPARENT = 0x00000000  // For corners
    const val SEPARATOR = 0xDDC0C9FF.toInt()

    // Popup Areas - Title bar and content gradients
    const val TITLE_BAR1 = 0xDD4B61D1.toInt()
    const val TITLE_BAR2 = 0xDD3B4DA7.toInt()
    const val CONTENT_AREA1 = 0xDD242852.toInt()
    const val CONTENT_AREA2 = 0xDD1A2040.toInt()

    // Decorations
    const val CORNER_DECORATION = 0xAAC0C9FF.toInt()

    // Text
    const val TEXT = 0xFFFFFFFF.toInt()

    // Buttons
    const val BUTTON_CONFIRM = 0xFF2D7D4C.toInt()
    const val BUTTON_CONFIRM_HOVER = 0xFF3A8E5A.toInt()
    const val BUTTON_CANCEL = 0xFF8D3F3F.toInt()
    const val BUTTON_CANCEL_HOVER = 0xFF9E4F4F.toInt()
  }

  // Positioning
  private val x1: Int get() = (width - popupWidth) / 2
  private val y1: Int get() = (height - popupHeight) / 2
  private val x2: Int get() = x1 + popupWidth
  private val y2: Int get() = y1 + popupHeight
  private val titleY: Int get() = y1 + titleBarHeight
  private val centerX: Int get() = x1 + popupWidth / 2

  // UI dimensions
  private val popupWidth = 220
  private val popupHeight = 120
  private val titleBarHeight = 25

  //? if fabric
  private val renderables: MutableList<Renderable> = mutableListOf()

  override fun shouldCloseOnEsc(): Boolean = true
  override fun isPauseScreen(): Boolean = false

  inner class StyledButton(
    x: Int, y: Int, width: Int, height: Int,
    component: Text,
    onPress: OnPress,
    private val baseColor: Int,
    private val hoverColor: Int
  ) : Button(x, y, width, height, component, onPress/*? if >=1.19.4 {*/, DEFAULT_NARRATION/*?}*/) {
    override fun /*? if >=1.20.4 {*/renderWidget/*?} elif >=1.17.1 {*//*render*//*?} else {*//*renderButton*//*?}*/(
      /*? if >=1.16.5 {*/poseStack: PoseStack,/*?}*/
      mouseX: Int,
      mouseY: Int,
      partialTick: Float
    ) {
      isHovered = mouseX >= x && mouseY >= y
        && mouseX < x + width && mouseY < y + height

      val color = if (isHovered) hoverColor else baseColor

      with(getRenderImpl(/*? if >=1.16.5 {*/poseStack/*?}*/)) {
        fill(x, y + 1, x + width, y + height - 1, color)
        fill(x + 1, y, x + width - 1, y + height, color)
        drawCenteredString(
          font,
          message/*? if >=1.16.5 {*/.string/*?}*/,
          x + width / 2,
          y + (height - 8) / 2,
          TEXT
        )
      }
    }
  }

  override fun init() {
    super.init()

    renderables.clear()

    // Use the pre-calculated positions
    with(renderables) {
      val closeAction = Button.OnPress { minecraft?.setScreen(null) }
      add(
        StyledButton(
          centerX - 70, y2 - 35,
          60, 20,
          ComponentUtils.translatable("gui.yes")/*? if <=1.15.2 {*//*.string*//*?}*/,
          closeAction,
          BUTTON_CONFIRM,
          BUTTON_CONFIRM_HOVER
        )
      )
      add(
        StyledButton(
          centerX + 10,
          y2 - 35,
          60,
          20,
          ComponentUtils.translatable("gui.no")/*? if <=1.15.2 {*//*.string*//*?}*/,
          closeAction,
          BUTTON_CANCEL,
          BUTTON_CANCEL_HOVER
        )
      )
    }
  }

  override fun render(/*? if >=1.16.5 {*/poseStack: PoseStack,/*?}*/ mouseX: Int, mouseY: Int, partialTick: Float) {
    val renderer = getRenderImpl(/*? if >=1.16.5 {*/poseStack/*?}*/)

    with(renderer) {
      //? if <=1.20.1 {
      /*fillGradient(0, 0, width, height, DIMMING, DIMMING)
      *///?} else if 1.20.4 {
      /*renderTransparentBackground(poseStack)
      *///?} else {
      renderBlurredBackground(/*? if <=1.21.1 {*//*partialTick*//*?}*/)
      //?}

      // Border
      fill(x1 + 1, y1, x2 - 1, y1 + 1, BORDER)
      fill(x1, y1 + 1, x1 + 1, y2 - 1, BORDER)
      fill(x2 - 1, y1 + 1, x2, y2 - 1, BORDER)
      fill(x1 + 1, y2 - 1, x2 - 1, y2, BORDER)

      // Cutouts
      fill(x1, y1, x1 + 1, y1 + 1, TRANSPARENT)
      fill(x2 - 1, y1, x2, y1 + 1, TRANSPARENT)
      fill(x1, y2 - 1, x1 + 1, y2, TRANSPARENT)
      fill(x2 - 1, y2 - 1, x2, y2, TRANSPARENT)

      // Content
      fillGradient(x1 + 1, y1 + 1, x2 - 1, titleY, TITLE_BAR1, TITLE_BAR2)
      fillGradient(x1 + 1, titleY + 1, x2 - 1, y2 - 1, CONTENT_AREA1, CONTENT_AREA2)
      fill(x1 + 1, titleY, x2 - 1, titleY + 1, SEPARATOR)

      // Add decorative diagonal elements in corners
      fun drawCornerDecoration(cornerX: Int, cornerY: Int) =
        fill(cornerX, cornerY, cornerX + 1, cornerY + 1, CORNER_DECORATION)

      for (i in 0..3) {
        drawCornerDecoration(x1 + 3 + i, y1 + 3 + i) // Top left
        drawCornerDecoration(x2 - 4 - i, y1 + 3 + i) // Top right
        drawCornerDecoration(x1 + 3 + i, y2 - 4 - i) // Bottom left
        drawCornerDecoration(x2 - 4 - i, y2 - 4 - i) // Bottom right
      }

      drawString(
        font,
        title.string,
        centerX - font.width(title.string) / 2,
        y1 + 9,
        TEXT,
        true
      )

      drawCenteredString(
        font,
        displayMessage,
        centerX,
        y1 + 40,
        TEXT
      )
    }

    renderables.forEach { it.render(/*? if >=1.16.5 {*/poseStack,/*?}*/ mouseX, mouseY, partialTick) }
  }

  override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
    // True if meets Screen's conditions
    if (super.mouseClicked(mouseX, mouseY, button)) return true

    // True if any button is clicked
    for (widget in renderables)
      if (widget is Button && widget.mouseClicked(mouseX, mouseY, button))
        return true

    // True if mouse is outside the popup (closes the screen)
    if ((mouseX < x1 || mouseX >= x2 || mouseY < y1 || mouseY >= y2) && shouldCloseOnEsc()) {
      minecraft?.setScreen(null)
      return true
    }

    // Otherwise false
    return false
  }
}
//?}
