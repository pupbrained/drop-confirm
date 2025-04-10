//? if (>=1.15.2 && <1.20.1) || forge {
/*package xyz.pupbrained.drop_confirm.config.widgets

import com.gitlab.cdagaming.unilib.ModUtils
import com.gitlab.cdagaming.unilib.utils.gui.RenderUtils
import com.gitlab.cdagaming.unilib.utils.gui.controls.SliderControl
import io.github.cdagaming.unicore.impl.Pair

//? if >=1.16.5
import com.mojang.blaze3d.vertex.PoseStack

class ModernSliderControl(
  positionData: Pair<Int, Int>,
  dimensions: Pair<Int, Int>,
  startValue: Float,
  minValue: Float,
  maxValue: Float,
  valueStep: Float,
  displayString: String,
) :
  SliderControl(positionData, dimensions, startValue, minValue, maxValue, valueStep, displayString) {
  @Suppress("DuplicatedCode")
  override fun render(
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
