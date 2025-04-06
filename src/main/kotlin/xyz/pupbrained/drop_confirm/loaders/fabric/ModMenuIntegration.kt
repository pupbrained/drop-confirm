//? if fabric {
package xyz.pupbrained.drop_confirm.loaders.fabric

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi
import xyz.pupbrained.drop_confirm.config.DropConfirmConfigScreen

class ModMenuIntegration : ModMenuApi {
  override fun getModConfigScreenFactory(): ConfigScreenFactory<*> =
    ConfigScreenFactory { DropConfirmConfigScreen.createScreen(it) }
}
//?}