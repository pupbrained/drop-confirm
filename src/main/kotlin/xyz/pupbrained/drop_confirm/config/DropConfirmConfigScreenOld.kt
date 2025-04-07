//? if <1.20.1 {
/*package xyz.pupbrained.drop_confirm.config

import com.gitlab.cdagaming.unilib.utils.gui.controls.ExtendedButtonControl
import com.gitlab.cdagaming.unilib.utils.gui.integrations.ExtendedScreen
import io.github.cdagaming.unicore.utils.StringUtils
import net.minecraft.client.Minecraft
//? if >1.16.5
import net.minecraft.client.gui.narration.NarrationElementOutput

class DropConfirmConfigScreenOld : ExtendedScreen("About") {
  //? if >1.16.5
  override fun updateNarration(narrationElementOutput: NarrationElementOutput) {}

  override fun initializeUi() {
    val screenWidth = this.width // More Kotlin-idiomatic way to access width/height
    val screenHeight = this.height

    // Uncomment and adapt if you implement version checking
    addControl(
      ExtendedButtonControl(
        (screenWidth / 2) - 90, (screenHeight - 26),
        180, 20,
        "Version Info",
        { println("Version check button clicked - Implement action") }
      )
    )

    // Adding Back Button
    addControl(
      ExtendedButtonControl(
        6, (screenHeight - 26),
        95, 20,
        "Back",
        { openScreen(parent) }
      )
    )

    // Adding View Source Button
    addControl(
      ExtendedButtonControl(
        (screenWidth / 2) - 90, (screenHeight - 51), // Position from Java code
        180, 20,
        "View Source", {}
      )
    )

    // Adding Wiki Button
    addControl(
      ExtendedButtonControl(
        (screenWidth - 101), (screenHeight - 26),
        95, 20,
        "Wiki", {}
      )
    )

    // Call super.initializeUi() AFTER adding your controls, if that's the intended logic
    // In many frameworks, super.init() is called first, but follow UniLib's pattern if it differs.
    // The Java example calls it last.
    super.initializeUi()
  }

  override fun renderStringData() {
    // Call super first if it renders background elements
    super.renderStringData()

    val screenWidth = this.width
    val screenHeight = this.height

    // Use your mod's specific credits text
    val notice: List<String> = StringUtils.splitTextByNewLine("")

    drawMultiLineString(
      notice,
      10, // Add some padding from the left edge (adjust as needed)
      screenHeight / 3, // Vertical position
      screenWidth - 20, // Max width with padding
      -1, -1, // Default text color / shadow color
      true, false // Centered: true, Shadow: false (adjust as needed)
    )
  }

  // Companion object to hold static members like the Minecraft instance
  companion object {
    private val mc: Minecraft = Minecraft.getInstance()
  }
}
*///?}