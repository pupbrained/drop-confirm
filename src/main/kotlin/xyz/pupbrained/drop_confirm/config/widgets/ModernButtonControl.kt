//? if (>=1.15.2 && <1.20.1) || forge {
/*package xyz.pupbrained.drop_confirm.config.widgets

//? if >=1.16.5 && !forge {
/^import com.mojang.blaze3d.vertex.PoseStack
^///?} elif forge {
/^import net.minecraft.client.gui.GuiGraphics as PoseStack
^///?}
import com.gitlab.cdagaming.unilib.ModUtils
import com.gitlab.cdagaming.unilib.utils.gui.RenderUtils
import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl

class ModernButtonControl(
  x: Int,
  y: Int,
  width: Int,
  height: Int,
  text: String,
  onPushEvent: () -> Unit,
) : ExtendedButtonControl(x, y, width, height, text, onPushEvent) {
  companion object {
    const val DEFAULT_TEXT_COLOR: Int = 0xE0E0E0
    const val DISABLED_TEXT_COLOR: Int = 0xA0A0A0
    const val ERROR_TEXT_COLOR: Int = 0xFF5555
  }

  // Property to hold the current text color
  var textColor: Int = DEFAULT_TEXT_COLOR

  @Suppress("DuplicatedCode")
  override fun /^? if forge {^//^m_87963_^//^?} else {^/render/^?}^/(
    /^? if >=1.16.5 {^//^matrixStack: PoseStack,^//^?}^/
    mouseX: Int,
    mouseY: Int,
    partialTicks: Float
  ) {
    if (!this.isControlVisible) return

    val mc = ModUtils.getMinecraft() ?: return

    this.isHoveringOver = this.isOverScreen && RenderUtils.isMouseOver(
      mouseX.toDouble(),
      mouseY.toDouble(),
      this,
    )

    this.renderBg(/^? if >=1.16.5 {^//^matrixStack,^//^?}^/ mc, mouseX, mouseY)

    val colorToRender = if (!this.isControlEnabled) {
      DISABLED_TEXT_COLOR
    } else {
      this.textColor
    }

    RenderUtils.renderScrollingString(
      /^? if >=1.16.5 {^//^matrixStack,^//^?}^/
      mc,
      mc.font,
      this.controlMessage,
      this.left + 2,
      this.top,
      this.right - 2,
      this.bottom,
      colorToRender
    )
  }
}
*///?}
