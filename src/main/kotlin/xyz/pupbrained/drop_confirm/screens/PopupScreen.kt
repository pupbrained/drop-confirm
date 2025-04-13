package xyz.pupbrained.drop_confirm.screens

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.gui.screens.Screen
import net.minecraft.client.gui.components.Button
import net.minecraft.network.chat.Component
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font

class PopupScreen(private val message: String, private val title: String = "Confirmation") :
  Screen(Component.literal("Popup Screen")) {
  private val popupWidth = 220
  private val popupHeight = 120

  // Colors with transparency (alpha in first two hex digits)
  private val titleBarColor = 0xDD3B4DA7.toInt()     // Translucent blue for title bar
  private val contentAreaColor = 0xDD1F2142.toInt()  // Translucent dark navy for content
  private val borderColor = 0xDDFFFFFF.toInt()       // White border - same as separator

  override fun init() {
    super.init()
    val startX = (width - popupWidth) / 2
    val startY = (height - popupHeight) / 2

    val confirmButton = Button.builder(Component.literal("Confirm"))
    { _ -> this.minecraft?.setScreen(null) }
      .pos(startX + popupWidth / 2 - 70, startY + popupHeight - 35)
      .size(60, 20)
      .build()

    val closeButton = Button.builder(Component.literal("Close"))
    { _ -> this.minecraft?.setScreen(null) }
      .pos(startX + popupWidth / 2 + 10, startY + popupHeight - 35)
      .size(60, 20)
      .build()

    addRenderableWidget(confirmButton)
    addRenderableWidget(closeButton)
  }

  override fun renderBackground(poseStack: PoseStack) {
    val dimmingColor = 0xC0101010.toInt()
    if (this.minecraft?.level != null) {
      fillGradient(poseStack, 0, 0, this.width, this.height, 0, dimmingColor, dimmingColor)
    } else {
      super.renderBackground(poseStack)
    }
  }

  override fun render(poseStack: PoseStack, mouseX: Int, mouseY: Int, partialTick: Float) {
    this.renderBackground(poseStack)
    val startX = (width - popupWidth) / 2
    val startY = (height - popupHeight) / 2

    // Draw the popup with border and translucency
    drawMinecraftStylePopup(poseStack, startX, startY, popupWidth, popupHeight)

    // Draw title text
    drawCenteredString(
      poseStack,
      font,
      title,
      startX + popupWidth / 2,
      startY + 8,
      0xFFFFFF
    )

    // Draw the message text with proper padding
    drawMultiLineText(poseStack, font, message, startX + 15, startY + 40, popupWidth - 30, 0xDDDDDD)

    super.render(poseStack, mouseX, mouseY, partialTick)
  }

  // Updated to use the same color for border and separator
  private fun drawMinecraftStylePopup(poseStack: PoseStack, x: Int, y: Int, width: Int, height: Int) {
    val titleBarHeight = 25

    // Draw border first (1px around the entire popup)
    // Top border
    fill(poseStack, x + 1, y, x + width - 1, y + 1, borderColor)
    // Left border
    fill(poseStack, x, y + 1, x + 1, y + height - 1, borderColor)
    // Right border
    fill(poseStack, x + width - 1, y + 1, x + width, y + height - 1, borderColor)
    // Bottom border
    fill(poseStack, x + 1, y + height - 1, x + width - 1, y + height, borderColor)

    // Main areas (now translucent)
    // Title bar (top blue section)
    fill(poseStack, x + 1, y + 1, x + width - 1, y + titleBarHeight, titleBarColor)

    // Content area (bottom dark blue section)
    fill(poseStack, x + 1, y + titleBarHeight + 1, x + width - 1, y + height - 1, contentAreaColor)

    // Separator line (same color as the border)
    fill(poseStack, x + 1, y + titleBarHeight, x + width - 1, y + titleBarHeight + 1, borderColor)
  }

  private fun drawMultiLineText(
    poseStack: PoseStack,
    font: Font,
    text: String,
    x: Int,
    y: Int,
    maxWidth: Int,
    color: Int
  ) {
    val words = text.split(" ")
    var currentLine = ""
    var currentY = y

    for (word in words) {
      val testLine = if (currentLine.isEmpty()) word else "$currentLine $word"
      val width = font.width(testLine)

      if (width > maxWidth) {
        font.draw(poseStack, currentLine, x.toFloat(), currentY.toFloat(), color)
        currentLine = word
        currentY += font.lineHeight + 2
      } else {
        currentLine = testLine
      }
    }

    // Draw the last line
    if (currentLine.isNotEmpty()) {
      font.draw(poseStack, currentLine, x.toFloat(), currentY.toFloat(), color)
    }
  }

  override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
    if (super.mouseClicked(mouseX, mouseY, button)) return true

    val startX = (width - popupWidth) / 2.0
    val startY = (height - popupHeight) / 2.0
    val endX = startX + popupWidth
    val endY = startY + popupHeight

    if (mouseX < startX || mouseX >= endX || mouseY < startY || mouseY >= endY) {
      if (this.shouldCloseOnEsc()) {
        this.minecraft?.setScreen(null)
        return true
      }
    }
    return false
  }

  override fun shouldCloseOnEsc(): Boolean = true
  override fun isPauseScreen(): Boolean = false

  companion object {
    fun show(message: String, title: String = "Confirmation") {
      Minecraft.getInstance().execute {
        Minecraft.getInstance().setScreen(PopupScreen(message, title))
      }
    }
  }
}
