package xyz.pupbrained.drop_confirm.platform

import net.minecraft.client.gui.Font

/**
 * Platform-independent rendering interface
 */
interface RenderInterface {
  fun drawString(font: Font, text: String, x: Int, y: Int, color: Int, shadow: Boolean = true): RenderInterface
  fun drawCenteredString(font: Font, text: String, x: Int, y: Int, color: Int): RenderInterface
  fun fill(x1: Int, y1: Int, x2: Int, y2: Int, color: Int): RenderInterface
  fun fillGradient(x1: Int, y1: Int, x2: Int, y2: Int, colorStart: Int, colorEnd: Int): RenderInterface
}
