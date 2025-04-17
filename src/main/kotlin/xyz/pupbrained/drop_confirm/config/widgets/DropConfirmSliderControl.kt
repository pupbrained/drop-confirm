//? if <1.20.1 || forge {
/*package xyz.pupbrained.drop_confirm.config.widgets

//? if >=1.16.5 {
import com.mojang.blaze3d.vertex.PoseStack
//?}
import com.gitlab.cdagaming.unilib.ModUtils
import com.gitlab.cdagaming.unilib.utils.gui.RenderUtils
import com.gitlab.cdagaming.unilib.utils.gui.controls.SliderControl
import io.github.cdagaming.unicore.impl.Pair

class DropConfirmSliderControl(
  positionData: Pair<Int, Int>,
  dimensions: Pair<Int, Int>,
  startValue: Float,
  minValue: Float,
  maxValue: Float,
  valueStep: Float,
  displayString: String,
) :
  SliderControl(positionData, dimensions, startValue, minValue, maxValue, valueStep, displayString) {
  companion object {
    const val DEFAULT_TEXT_COLOR: Int = 0xE0E0E0
    const val DISABLED_TEXT_COLOR: Int = 0xA0A0A0

    //? if 1.14.4
    /^const val HOVERED_TEXT_COLOR: Int = 0xFFFFA0^/
  }

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

    val colorToRender =
      if (!this.isControlEnabled) DISABLED_TEXT_COLOR
      //? if 1.14.4
      /^else if (this.isHoveringOrFocusingOver) HOVERED_TEXT_COLOR^/
      else DEFAULT_TEXT_COLOR

    RenderUtils.renderScrollingString(
      /^? if >=1.16.5 {^/matrixStack,/^?}^/
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
