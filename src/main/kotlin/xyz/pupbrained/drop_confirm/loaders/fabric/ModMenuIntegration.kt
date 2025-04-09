//? if fabric && <=1.21.5 {
/*package xyz.pupbrained.drop_confirm.loaders.fabric

//? if <1.15.2 {
/^import net.minecraft.client.gui.screens.Screen
import io.github.prospector.modmenu.api.ModMenuApi
import java.util.function.Function
^///?} elif 1.15.2 {
/^import io.github.prospector.modmenu.api.ConfigScreenFactory
import io.github.prospector.modmenu.api.ModMenuApi
^///?} else {
import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
//?}
import xyz.pupbrained.drop_confirm.config.DropConfirmConfigScreen

class ModMenuIntegration : ModMenuApi {
  //? if >=1.15.2 {
  override fun getModConfigScreenFactory(): ConfigScreenFactory<*> =
    ConfigScreenFactory {
      /^? if >= 1.20.1 {^/DropConfirmConfigScreen.createScreen/^?} else {^//^DropConfirmConfigScreen^//^?}^/(it)
    }
  //?} else {
  /^override fun getModId(): String = "drop_confirm"
  override fun getConfigScreenFactory(): Function<Screen, out Screen> {
    return Function { DropConfirmConfigScreen(it) }
  }
  ^///?}
}
*///?}