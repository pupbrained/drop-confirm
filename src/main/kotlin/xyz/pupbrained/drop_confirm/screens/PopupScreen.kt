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

//? if >=1.19.4 {
import net.minecraft.client.gui.components.Renderable
//?} elif >=1.17.1 {
/*import net.minecraft.client.gui.components.Widget as Renderable
*///?}

//? if >=1.17.1
import net.minecraft.client.gui.narration.NarratableEntry
//? if <=1.16.5
/*import net.minecraft.client.gui.components.AbstractWidget*/
//? if >=1.16.5
import net.minecraft.network.chat.Component
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.components.Button
import net.minecraft.client.gui.components.events.GuiEventListener
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

  // UI dimensions
  private val popupWidth = 220
  private val popupHeight = 120
  private val titleBarHeight = 25

  //? if <=1.16.5 {
  /*private val buttonList: MutableList<AbstractWidget> = mutableListOf()
  private val childrenList: MutableList<GuiEventListener> = mutableListOf()
  *///?} else {
  private val renderables: MutableList<Renderable> = mutableListOf()
  //?}

  // Color scheme using consistent format
  companion object {
    const val BORDER = 0xDD9BA8FF.toInt()

    const val BUTTON_CANCEL = 0xFF8D3F3F.toInt()
    const val BUTTON_CANCEL_HOVER = 0xFF9E4F4F.toInt()
    const val BUTTON_CONFIRM = 0xFF2D7D4C.toInt()
    const val BUTTON_CONFIRM_HOVER = 0xFF3A8E5A.toInt()

    const val CONTENT_AREA = 0xDD242852.toInt()
    const val CONTENT_AREA_GRADIENT = 0xDD1A2040.toInt()

    const val CORNER_DECORATION = 0xAAC0C9FF.toInt()

    const val DIMMING = 0xC0101010.toInt()
    const val SEPARATOR = 0xDDC0C9FF.toInt()

    const val TEXT = 0xFFFFFF

    const val TITLE_BAR1 = 0xDD4B61D1.toInt()
    const val TITLE_BAR2 = 0xDD3B4DA7.toInt()

    const val TRANSPARENT = 0x00000000
  }

  inner class StyledButton(
    x: Int, y: Int, width: Int, height: Int,
    private val component: /*? if >=1.16.5 {*/Component/*?} else {*//*String*//*?}*/,
    onPress: OnPress,
    private val baseColor: Int,
    private val hoverColor: Int
  ) : Button(
    x,
    y,
    width,
    height,
    component,
    onPress,
    /*? if >=1.19.4 {*/DEFAULT_NARRATION/*?}*/
  ) {
    override fun /*? if >=1.20.4 {*/renderWidget/*?} elif >=1.17.1 {*//*render*//*?} else {*//*renderButton*//*?}*/(
      /*? if >=1.16.5 {*/poseStack: PoseStack,/*?}*/
      mouseX: Int,
      mouseY: Int,
      partialTick: Float
    ) {
      val renderer = getRenderImpl(/*? if >=1.16.5 {*/poseStack/*?}*/)

      isHovered = mouseX >= x && mouseY >= y
        && mouseX < x + width && mouseY < y + height

      // Draw custom button background with rounded appearance
      val color = if (isHovered) hoverColor else baseColor

      renderer
        .fill(x, y + 1, x + width, y + height - 1, color)
        .fill(x + 1, y, x + width - 1, y + height, color)
        .drawCenteredString(
          Minecraft.getInstance().font,
          component/*? if >=1.16.5 {*/.string/*?}*/,
          x + width / 2,
          y + (height - 8) / 2,
          TEXT
        )
    }
  }

  //? if <=1.16.5 {
  /*override fun <T : AbstractWidget> addButton(abstractWidget: T): T {
    buttonList.add(abstractWidget)
    childrenList.add(abstractWidget)
    return abstractWidget
  }
  *///?} else {
  override fun <T> addRenderableWidget(widget: T): T where T : GuiEventListener, T : Renderable, T : NarratableEntry {
    renderables.add(widget)
    return addWidget(widget) as T
  }
  //?}

  override fun init() {
    super.init()
    val startX = (width - popupWidth) / 2
    val startY = (height - popupHeight) / 2

    val closeAction = Button.OnPress { minecraft?.setScreen(null) }

    val confirmText = ComponentUtils.translatable("gui.yes")/*? if <=1.15.2 {*//*.string*//*?}*/
    val cancelText = ComponentUtils.translatable("gui.no")/*? if <=1.15.2 {*//*.string*//*?}*/

    /*? if <=1.16.5 {*//*addButton*//*?} else {*/addRenderableWidget/*?}*/(
      StyledButton(
        startX + popupWidth / 2 - 70, startY + popupHeight - 35,
        60, 20,
        confirmText,
        closeAction,
        BUTTON_CONFIRM,
        BUTTON_CONFIRM_HOVER
      )
    )

    /*? if <=1.16.5 {*//*addButton*//*?} else {*/addRenderableWidget/*?}*/(
      StyledButton(
        startX + popupWidth / 2 + 10,
        startY + popupHeight - 35,
        60,
        20,
        cancelText,
        closeAction,
        BUTTON_CANCEL,
        BUTTON_CANCEL_HOVER
      )
    )
  }

  override fun renderBackground(
    /*? if >=1.16.5 {*/poseStack: PoseStack,
    /*?}*/
    //? if >=1.20.4 {
    mouseX: Int,
    mouseY: Int,
    partialTick: Float
    //?}
  ) {
    val renderer = getRenderImpl(/*? if >=1.16.5 {*/poseStack/*?}*/)

    if (minecraft?.level != null)
      renderer.fillGradient(0, 0, width, height, DIMMING, DIMMING)
    else
      super.renderBackground(
        /*? if >=1.16.5 {*/poseStack,
        /*?}*/
        //? if >=1.20.4 {
        mouseX,
        mouseY,
        partialTick
        //?}
      )
  }

  override fun render(
    /*? if >=1.16.5 {*/poseStack: PoseStack,/*?}*/
    mouseX: Int,
    mouseY: Int,
    partialTick: Float
  ) {
    //? if <=1.20.1 {
    /*renderBackground(
      /^? if >=1.16.5 {^/poseStack,
      /^?}^/
      //? if >=1.20.4 {
      mouseX,
      mouseY,
      partialTick
      //?}
    )
    *///?} else {
    renderTransparentBackground(poseStack)
    //?}
    val renderer = getRenderImpl(/*? if >=1.16.5 {*/poseStack/*?}*/)

    val startX = (width - popupWidth) / 2
    val startY = (height - popupHeight) / 2

    // Draw the popup with border and translucency
    drawEnhancedPopup(renderer, startX, startY, popupWidth, popupHeight)

    renderer.drawString(
      font,
      title.string,
      startX + popupWidth / 2 - font.width(title.string) / 2,
      startY + 9,
      TEXT,
      true
    )

    // Draw message with word wrapping
    drawMultiLineText(renderer, font, displayMessage, startX + 15, startY + 40, popupWidth - 30, TEXT)

    //? if <=1.16.5 {
    /*for (k in buttonList.indices)
      buttonList[k].render(/^? if >=1.16.5 {^/poseStack,/^?}^/ mouseX, mouseY, partialTick)
    *///?} else {
    for (renderable in this.renderables)
      renderable.render(poseStack, mouseX, mouseY, partialTick)
    //?}
  }

  @Suppress("SameParameterValue")
  private fun drawEnhancedPopup(
    renderer: RenderInterface,
    x: Int,
    y: Int,
    width: Int,
    height: Int
  ) {
    // Draw border
    drawBorder(renderer, x, y, width, height)

    renderer
      .fillGradient(x + 1, y + 1, x + width - 1, y + titleBarHeight, TITLE_BAR1, TITLE_BAR2)
      .fillGradient(x + 1, y + titleBarHeight + 1, x + width - 1, y + height - 1, CONTENT_AREA, CONTENT_AREA_GRADIENT)
      .fill(x + 1, y + titleBarHeight, x + width - 1, y + titleBarHeight + 1, SEPARATOR)

    // Add decorative diagonal elements in corners
    drawCornerDecorations(renderer, x, y, width, height)
  }

  private fun drawBorder(
    renderer: RenderInterface,
    x: Int,
    y: Int,
    width: Int,
    height: Int
  ) {
    renderer
      .fill(x + 1, y, x + width - 1, y + 1, BORDER) // Top
      .fill(x, y + 1, x + 1, y + height - 1, BORDER) // Left
      .fill(x + width - 1, y + 1, x + width, y + height - 1, BORDER) // Right
      .fill(x + 1, y + height - 1, x + width - 1, y + height, BORDER) // Bottom
      .fill(x, y, x + 1, y + 1, TRANSPARENT)
      .fill(x + width - 1, y, x + width, y + 1, TRANSPARENT)
      .fill(x, y + height - 1, x + 1, y + height, TRANSPARENT)
      .fill(x + width - 1, y + height - 1, x + width, y + height, TRANSPARENT)
  }

  private fun drawCornerDecorations(
    renderer: RenderInterface,
    x: Int,
    y: Int,
    width: Int,
    height: Int
  ) {
    for (i in 0..3) {
      fun drawCornerDecoration(cornerX: Int, cornerY: Int) =
        renderer.fill(cornerX, cornerY, cornerX + 1, cornerY + 1, CORNER_DECORATION)

      drawCornerDecoration(x + 3 + i, y + 3 + i) // Top left
      drawCornerDecoration(x + width - 4 - i, y + 3 + i) // Top right
      drawCornerDecoration(x + 3 + i, y + height - 4 - i) // Bottom left
      drawCornerDecoration(x + width - 4 - i, y + height - 4 - i) // Bottom right
    }
  }

  /**
   * Draw multi-line text with improved word-wrapping
   */
  @Suppress("SameParameterValue")
  private fun drawMultiLineText(
    renderer: RenderInterface,
    font: Font,
    text: String,
    x: Int,
    y: Int,
    maxWidth: Int,
    color: Int
  ) {
    val words = text.split(" ")
    var currentLine = StringBuilder()
    var currentY = y

    for (word in words) {
      val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
      val width = font.width(testLine)

      if (width > maxWidth) {
        if (currentLine.isNotEmpty()) {
          renderer.drawString(
            font,
            currentLine.toString(),
            x,
            currentY,
            color
          )
          currentLine = StringBuilder(word)
          currentY += font.lineHeight + 2
        } else {
          // Special case: Single word too long for the line - need to break it
          val chars = word.toCharArray()
          var lineBuilder = StringBuilder()

          for (char in chars) {
            val testWidth = font.width(lineBuilder.toString() + char)
            if (testWidth > maxWidth) {
              renderer.drawString(
                font,
                lineBuilder.toString(),
                x,
                currentY,
                color
              )
              lineBuilder = StringBuilder("$char")
              currentY += font.lineHeight + 2
            } else {
              lineBuilder.append(char)
            }
          }

          currentLine = lineBuilder
        }
      } else {
        if (currentLine.isNotEmpty() && currentLine.toString() != word)
          currentLine.append(" ")

        currentLine.append(word)
      }
    }

    if (currentLine.isNotEmpty())
      renderer.drawString(font, currentLine.toString(), x, currentY, color)
  }

  override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
    if (super.mouseClicked(mouseX, mouseY, button)) return true

    val startX = (width - popupWidth) / 2.0
    val startY = (height - popupHeight) / 2.0
    val endX = startX + popupWidth
    val endY = startY + popupHeight

    if ((mouseX < startX || mouseX >= endX || mouseY < startY || mouseY >= endY) && shouldCloseOnEsc()) {
      minecraft?.setScreen(null)
      return true
    }

    return false
  }

  override fun shouldCloseOnEsc(): Boolean = true
  override fun isPauseScreen(): Boolean = false
}
