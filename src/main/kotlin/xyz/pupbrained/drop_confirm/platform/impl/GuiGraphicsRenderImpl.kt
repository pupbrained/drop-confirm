//? if >=1.20.1 {
package xyz.pupbrained.drop_confirm.platform.impl

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.util.FormattedCharSequence
import xyz.pupbrained.drop_confirm.platform.RenderInterface

class GuiGraphicsRenderImpl(private val graphics: GuiGraphics) : RenderInterface {
  override fun drawString(font: Font, text: String, x: Int, y: Int, color: Int, shadow: Boolean): RenderInterface {
    graphics.drawString(font, text, x, y, color, shadow)

    return this
  }

  override fun drawCenteredString(font: Font, text: String, x: Int, y: Int, color: Int): RenderInterface {
    graphics.drawCenteredString(font, text, x, y, color)

    return this
  }

  override fun drawCenteredString(
    font: Font,
    text: FormattedCharSequence,
    x: Int,
    y: Int,
    color: Int
  ): RenderInterface {
    graphics.drawCenteredString(font, text, x, y, color)

    return this
  }

  override fun fill(x1: Int, y1: Int, x2: Int, y2: Int, color: Int): RenderInterface {
    graphics.fill(x1, y1, x2, y2, color)

    return this
  }

  override fun fillGradient(x1: Int, y1: Int, x2: Int, y2: Int, colorStart: Int, colorEnd: Int): RenderInterface {
    graphics.fillGradient(x1, y1, x2, y2, colorStart, colorEnd)

    return this
  }
}
//?}
