package xyz.pupbrained.drop_confirm.screens

//? if >=1.20.1 {
import net.minecraft.client.gui.GuiGraphics as PoseStack
//?} elif >=1.16.5 {
/*import com.mojang.blaze3d.vertex.PoseStack
*///?} elif 1.15.2 {
/*import com.mojang.blaze3d.systems.RenderSystem
*///?} else {
/*import com.mojang.blaze3d.platform.GlStateManager as RenderSystem
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
import xyz.pupbrained.drop_confirm.util.ComponentUtils

class PopupScreen(private val displayMessage: String) : Screen(ComponentUtils.literal("Popup Screen")) {
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

  private class StyledButton(
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
    override fun /*? if >=1.20.4 {*/renderWidget/*?} else {*//*render*//*?}*/(
      /*? if >=1.16.5 {*/poseStack: PoseStack,/*?}*/
      mouseX: Int,
      mouseY: Int,
      partialTick: Float
    ) {
      isHovered =
        mouseX >= x
          && mouseY >= y
          && mouseX < x + width
          && mouseY < y + height

      // Draw custom button background with rounded appearance
      val color = if (isHovered) hoverColor else baseColor

      //? if >=1.20.1 {
      poseStack.fill(x, y + 1, x + width, y + height - 1, color)
      poseStack.fill(x + 1, y, x + width - 1, y + height, color)
      poseStack.drawCenteredString(
        Minecraft.getInstance().font,
        component/*? if >=1.18.2 {*/.string/*?}*/,
        x + width / 2,
        y + (height - 8) / 2,
        TEXT
      )
      //?} else {
      /*fill(/^? if >=1.16.5 {^/poseStack,/^?}^/ x, y + 1, x + width, y + height - 1, color)
      fill(/^? if >=1.16.5 {^/poseStack,/^?}^/ x + 1, y, x + width - 1, y + height, color)

      // Draw button text with hover effect
      drawCenteredString(
        /^? if >=1.16.5 {^/poseStack,/^?}^/
        Minecraft.getInstance().font,
        component/^? if >=1.18.2 {^/.string/^?}^/,
        x + width / 2,
        y + (height - 8) / 2,
        TEXT
      )
      *///?}
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

    val confirmText =
      ComponentUtils.translatable("gui.yes")/*? if <=1.15.2 {*//*.string*//*?}*/
    val cancelText =
      ComponentUtils.translatable("gui.no")/*? if <=1.15.2 {*//*.string*//*?}*/

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
        startX + popupWidth / 2 + 10, startY + popupHeight - 35,
        60, 20,
        cancelText,
        closeAction,
        BUTTON_CANCEL,
        BUTTON_CANCEL_HOVER
      )
    )
  }

  override fun renderBackground(
    /*? if >=1.16.5 {*/poseStack: PoseStack,/*?}*/
    //? if >=1.20.4 {
    mouseX: Int,
    mouseY: Int,
    partialTick: Float
    //?}
  ) {
    if (minecraft?.level != null)
    //? if >=1.20.1 {
      poseStack.fillGradient(
        0,
        0,
        width,
        height,
        /*? if >=1.18.2 && <=1.21.6-alpha.25.14.craftmine {*/0,/*?}*/
        DIMMING,
        DIMMING
      )
    //?} else {
    /*fillGradient(
      /^? if >=1.16.5 {^/poseStack,/^?}^/
      0,
      0,
      width,
      height,
      /^? if >=1.18.2 {^/0,/^?}^/
      DIMMING,
      DIMMING
    )
  *///?}
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

    val startX = (width - popupWidth) / 2
    val startY = (height - popupHeight) / 2

    // Draw the popup with border and translucency
    drawEnhancedPopup(
      /*? if >=1.16.5 {*/poseStack,/*?}*/
      startX,
      startY,
      popupWidth,
      popupHeight
    )

    //? if >=1.21.6-alpha.25.15.a {
    /*val pose = poseStack.pose()
    pose.pushMatrix()
    pose.translate(0.0F, 0.0F, pose)
    poseStack.drawString(
      font,
      title,
      startX + popupWidth / 2 - font.width(title) / 2,
      startY + 9,
      TEXT,
      true
    )
    pose.popMatrix()
    *///?} elif >=1.20.1 {
    val pose = poseStack.pose()
    pose.pushPose()
    pose.translate(0.0, 0.0, 100.0)
    poseStack.drawString(
      font,
      title,
      startX + popupWidth / 2 - font.width(title) / 2,
      startY + 9,
      TEXT,
      true
    )
    pose.popPose()
    //?} elif >=1.16.5 {
    /*poseStack.pushPose()
    poseStack.translate(0.0, 0.0, 100.0)
    font.drawShadow(
      poseStack,
      title,
      startX + popupWidth / 2 - font.width(title) / 2f,
      startY + 9.5f,
      TEXT
    )
    poseStack.popPose()
    *///?} else {
    /*RenderSystem.pushMatrix()
    RenderSystem.translated(0.0, 0.0, 100.0)
    font.drawShadow(
      title.string,
      startX + popupWidth / 2 - font.width(title.string) / 2f,
      startY + 9.5f,
      TEXT
    )
    RenderSystem.popMatrix()
    *///?}

    // Draw message with word wrapping
    drawMultiLineText(
      /*? if >=1.16.5 {*/poseStack,/*?}*/
      font,
      displayMessage,
      startX + 15,
      startY + 40,
      popupWidth - 30,
      TEXT
    )

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
    /*? if >=1.16.5 {*/poseStack: PoseStack,/*?}*/
    x: Int,
    y: Int,
    width: Int,
    height: Int
  ) {
    // Draw border
    drawBorder(
      /*? if >=1.16.5 {*/poseStack,/*?}*/
      x,
      y,
      width,
      height
    )

    // Title bar with gradient
    //? if >=1.20.1 {
    poseStack.fillGradient(
      x + 1, y + 1,
      x + width - 1, y + titleBarHeight,
      TITLE_BAR1,
      TITLE_BAR2
    )

    // Content area with subtle gradient
    poseStack.fillGradient(
      x + 1, y + titleBarHeight + 1,
      x + width - 1, y + height - 1,
      CONTENT_AREA,
      CONTENT_AREA_GRADIENT
    )

    // Separator line
    poseStack.fill(
      x + 1,
      y + titleBarHeight,
      x + width - 1,
      y + titleBarHeight + 1,
      SEPARATOR
    )
    //?} else {
    /*fillGradient(
      /^? if >=1.16.5 {^/poseStack,/^?}^/
      x + 1, y + 1,
      x + width - 1, y + titleBarHeight,
      TITLE_BAR1,
      TITLE_BAR2
    )

    // Content area with subtle gradient
    fillGradient(
      /^? if >=1.16.5 {^/poseStack,/^?}^/
      x + 1, y + titleBarHeight + 1,
      x + width - 1, y + height - 1,
      CONTENT_AREA,
      CONTENT_AREA_GRADIENT
    )

    // Separator line
    fill(
      /^? if >=1.16.5 {^/poseStack,/^?}^/
      x + 1,
      y + titleBarHeight,
      x + width - 1,
      y + titleBarHeight + 1,
      SEPARATOR
    )
    *///?}

    // Add decorative diagonal elements in corners
    drawCornerDecorations(
      /*? if >=1.16.5 {*/poseStack,/*?}*/
      x,
      y,
      width,
      height
    )
  }

  private fun drawBorder(
    /*? if >=1.16.5 {*/poseStack: PoseStack,/*?}*/
    x: Int,
    y: Int,
    width: Int,
    height: Int
  ) {
    //? if >=1.20.1 {
    poseStack.fill(x + 1, y, x + width - 1, y + 1, BORDER) // Top
    poseStack.fill(x, y + 1, x + 1, y + height - 1, BORDER) // Left
    poseStack.fill(x + width - 1, y + 1, x + width, y + height - 1, BORDER) // Right
    poseStack.fill(x + 1, y + height - 1, x + width - 1, y + height, BORDER) // Bottom

    poseStack.fill(x, y, x + 1, y + 1, TRANSPARENT)
    poseStack.fill(x + width - 1, y, x + width, y + 1, TRANSPARENT)
    poseStack.fill(x, y + height - 1, x + 1, y + height, TRANSPARENT)
    poseStack.fill(x + width - 1, y + height - 1, x + width, y + height, TRANSPARENT)
    //?} else {
    /*fill(/^? if >=1.16.5 {^/poseStack,/^?}^/ x + 1, y, x + width - 1, y + 1, BORDER) // Top
    fill(/^? if >=1.16.5 {^/poseStack,/^?}^/ x, y + 1, x + 1, y + height - 1, BORDER) // Left
    fill(/^? if >=1.16.5 {^/poseStack,/^?}^/ x + width - 1, y + 1, x + width, y + height - 1, BORDER) // Right
    fill(/^? if >=1.16.5 {^/poseStack,/^?}^/ x + 1, y + height - 1, x + width - 1, y + height, BORDER) // Bottom

    fill(/^? if >=1.16.5 {^/poseStack,/^?}^/ x, y, x + 1, y + 1, TRANSPARENT)
    fill(/^? if >=1.16.5 {^/poseStack,/^?}^/ x + width - 1, y, x + width, y + 1, TRANSPARENT)
    fill(/^? if >=1.16.5 {^/poseStack,/^?}^/ x, y + height - 1, x + 1, y + height, TRANSPARENT)
    fill(/^? if >=1.16.5 {^/poseStack,/^?}^/ x + width - 1, y + height - 1, x + width, y + height, TRANSPARENT)
    *///?}
  }

  private fun drawCornerDecorations(
    /*? if >=1.16.5 {*/poseStack: PoseStack,/*?}*/
    x: Int,
    y: Int,
    width: Int,
    height: Int
  ) {
    for (i in 0..3) {
      //? if >=1.20.1 {
      fun drawCornerDecoration(cornerX: Int, cornerY: Int) =
        poseStack.fill(cornerX, cornerY, cornerX + 1, cornerY + 1, CORNER_DECORATION)
      //?} else {
      /*fun drawCornerDecoration(cornerX: Int, cornerY: Int) =
        fill(/^? if >=1.16.5 {^/poseStack,/^?}^/ cornerX, cornerY, cornerX + 1, cornerY + 1, CORNER_DECORATION)
      *///?}

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
    /*? if >=1.16.5 {*/poseStack: PoseStack,/*?}*/
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
          //? if >=1.20.1 {
          poseStack.drawString(
            font,
            currentLine.toString(),
            x,
            currentY,
            color
          )
          //?} else {
          /*font.draw(
            /^? if >=1.16.5 {^/poseStack,/^?}^/
            currentLine.toString(),
            x.toFloat(),
            currentY.toFloat(),
            color
          )
          *///?}
          currentLine = StringBuilder(word)
          currentY += font.lineHeight + 2
        } else {
          // Special case: Single word too long for the line - need to break it
          val chars = word.toCharArray()
          var lineBuilder = StringBuilder()

          for (char in chars) {
            val testWidth = font.width(lineBuilder.toString() + char)
            if (testWidth > maxWidth) {
              //? if >=1.20.1 {
              poseStack.drawString(
                font,
                lineBuilder.toString(),
                x,
                currentY,
                color
              )
              //?} else {
              /*font.draw(
                /^? if >=1.16.5 {^/poseStack,/^?}^/
                lineBuilder.toString(),
                x.toFloat(),
                currentY.toFloat(),
                color
              )
              *///?}
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

    // Draw the last line
    //? if >=1.20.1 {
    if (currentLine.isNotEmpty())
      poseStack.drawString(font, currentLine.toString(), x, currentY, color)
    //?} else {
    /*if (currentLine.isNotEmpty())
      font.draw(/^? if >=1.16.5 {^/poseStack,/^?}^/ currentLine.toString(), x.toFloat(), currentY.toFloat(), color)
    *///?}
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
