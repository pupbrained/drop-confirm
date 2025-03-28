package xyz.pupbrained.drop_confirm

import net.minecraft.client.Minecraft
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.common.Mod
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.client.gui.IConfigScreenFactory
import net.neoforged.neoforge.common.NeoForge
import xyz.pupbrained.drop_confirm.DropConfirm.TOGGLE_KEY
import xyz.pupbrained.drop_confirm.DropConfirm.handleKeyPresses

@Mod(Constants.MOD_ID)
class DropConfirmNeoForge {
  init {
    ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory::class.java) {
      IConfigScreenFactory { _, screen -> DropConfirmConfig.createScreen(screen) }
    }

    DropConfirm.init()

    if (FMLEnvironment.dist == Dist.CLIENT)
      NeoForge.EVENT_BUS.register(KeyInputHandler())
  }

  @EventBusSubscriber(modid = Constants.MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
  object KeyBindings {
    @SubscribeEvent
    @JvmStatic
    fun registerKeyMappings(event: RegisterKeyMappingsEvent) {
      event.register(TOGGLE_KEY)
    }
  }

  class KeyInputHandler {
    @SubscribeEvent
    fun onKeyInput(event: InputEvent.Key) {
      handleKeyPresses(Minecraft.getInstance())
    }
  }
}
