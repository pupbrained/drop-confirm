package xyz.pupbrained.drop_confirm.util

/**
 * Defines standard colors for UI components throughout the mod's interface.
 *
 * These colors provide consistent visual styling and accessibility across
 * different states of UI elements (normal, disabled, hover, error, etc.).
 *
 * @property rgb The value of the color as an integer in 0x(AA)RRGGBB format
 * @constructor Creates a ComponentColor with the specified RGB value
 * @since 5.0.0
 */
enum class Color(val rgb: Int) {
  /**
   * Standard text color (light gray: `0xE0E0E0`).
   *
   * Used for normal, interactive text elements throughout the interface.
   */
  TEXT(0xE0E0E0),

  /**
   * Color for disabled components (gray: `0xA0A0A0`).
   *
   * Used to visually indicate when UI elements are non-interactive
   * or unavailable for the current context.
   */
  DISABLED(0xA0A0A0),

  /**
   * Highlight color for hovered components (yellow: `0xFFFFA0`).
   *
   * Note: This color is only used in Minecraft 1.14.4 builds.
   * In other versions, the default Minecraft hover handling is used.
   */
  //? if 1.14.4
  /*HOVERED(0xFFFFA0),*/

  /**
   * Color for success states and confirmations (green: `0x55FF55`).
   *
   * Used for valid inputs, successful operations, and positive feedback.
   */
  SUCCESS(0x55FF55),

  /**
   * Color for error states and warnings (red: `0xFF5555`).
   *
   * Used for invalid inputs, error messages, and critical warnings.
   */
  ERROR(0xFF5555),

  /**
   * Fully transparent color (`0x00000000`).
   *
   * Used for invisible backgrounds or elements that should not
   * visually appear but still occupy space in the layout.
   * Format: `0xAARRGGBB` (Alpha=0, R=0, G=0, B=0)
   */
  TRANSPARENT(0x00000000);

  /**
   * Provides direct conversion of this color enum to its integer value.
   *
   * This operator allows using color enums directly in rendering methods
   * that expect integer colors without explicitly accessing the rgb property.
   *
   * Example usage:
   * ```kotlin
   * drawText("Example", x, y, ComponentColor.TEXT())
   * ```
   *
   * @return The RGB value as an integer
   */
  operator fun invoke(): Int = rgb
}
