//? if fabric {
/*package xyz.pupbrained.drop_confirm.loaders.fabric

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import xyz.pupbrained.drop_confirm.DropConfirm.TOGGLE_KEY

//? if >=1.20.1 {
import xyz.pupbrained.drop_confirm.config.DropConfirmConfig
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import xyz.pupbrained.drop_confirm.DropConfirm.handleKeyPresses
//?}

class FabricEntrypoint : ModInitializer {
  override fun onInitialize() {
    //? if >=1.20.1
    DropConfirmConfig.GSON.load()
    KeyBindingHelper.registerKeyBinding(TOGGLE_KEY)
    //? if >=1.20.1
    ClientTickEvents.END_CLIENT_TICK.register { handleKeyPresses(it) }
  }
}
*///?}