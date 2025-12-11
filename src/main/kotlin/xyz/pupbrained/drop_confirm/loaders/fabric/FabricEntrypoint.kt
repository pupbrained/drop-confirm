//? if fabric {
package xyz.pupbrained.drop_confirm.loaders.fabric

import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import xyz.pupbrained.drop_confirm.DropConfirm.TOGGLE_KEY
import xyz.pupbrained.drop_confirm.DropConfirm.handleKeyPresses
import xyz.pupbrained.drop_confirm.config.DropConfirmConfig

class FabricEntrypoint : ModInitializer {
  override fun onInitialize() {
    DropConfirmConfig.load()
    KeyBindingHelper.registerKeyBinding(TOGGLE_KEY)
    ClientTickEvents.END_CLIENT_TICK.register { handleKeyPresses(it) }
  }
}
//?}
