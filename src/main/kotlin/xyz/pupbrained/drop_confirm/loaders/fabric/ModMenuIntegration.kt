//? if fabric {
/*package xyz.pupbrained.drop_confirm.loaders.fabric

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import xyz.pupbrained.drop_confirm.config./^? if >=1.20.1 {^/DropConfirmConfigScreen/^?} else {^//^DropConfirmConfigScreenOld^//^?}^/

class ModMenuIntegration : ModMenuApi {
  override fun getModConfigScreenFactory(): ConfigScreenFactory<*> =
    ConfigScreenFactory {
      /^? if >= 1.20.1 {^/DropConfirmConfigScreen.createScreen(it)/^?} else {^//^DropConfirmConfigScreenOld()^//^?}^/
    }
}
*///?}