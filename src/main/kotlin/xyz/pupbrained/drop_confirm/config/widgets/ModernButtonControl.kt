//? if (>=1.15.2 && <1.20.1) || forge {
/*package xyz.pupbrained.drop_confirm.config.widgets

//? if >=1.16.5 && !forge {
import com.mojang.blaze3d.vertex.PoseStack
//?} elif forge {
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
  @Suppress("DuplicatedCode")
  override fun /^? if forge {^//^m_87963_^//^?} else {^/render/^?}^/(
    /^? if >=1.16.5 {^/matrixStack: PoseStack,/^?}^/
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

    this.renderBg(/^? if >=1.16.5 {^/matrixStack,/^?}^/ mc, mouseX, mouseY)

    RenderUtils.renderScrollingString(
      /^? if >=1.16.5 {^/matrixStack,/^?}^/
      mc,
      mc.font,
      this.controlMessage,
      this.left + 2,
      this.top,
      this.right - 2,
      this.bottom,
      if (!this.isControlEnabled) 10526880 else 14737632,
    )
  }
}
*///?}
