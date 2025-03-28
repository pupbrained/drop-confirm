package xyz.pupbrained.drop_confirm

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper
import xyz.pupbrained.drop_confirm.DropConfirm.TOGGLE_KEY
import xyz.pupbrained.drop_confirm.DropConfirm.handleKeyPresses

class DropConfirmFabric : ClientModInitializer {
  override fun onInitializeClient() {
    DropConfirm.init()

    KeyBindingHelper.registerKeyBinding(TOGGLE_KEY)

    ClientTickEvents.END_CLIENT_TICK.register { handleKeyPresses(it) }
  }
}