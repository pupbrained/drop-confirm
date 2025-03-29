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
import xyz.pupbrained.drop_confirm.DropConfirm.MOD_ID
import xyz.pupbrained.drop_confirm.DropConfirm.TOGGLE_KEY
import xyz.pupbrained.drop_confirm.DropConfirm.handleKeyPresses

@Mod(MOD_ID)
@EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
class DropConfirmNeoForge {
  companion object {
    @SubscribeEvent
    @JvmStatic
    fun registerKeyMappings(event: RegisterKeyMappingsEvent) = event.register(TOGGLE_KEY)
  }

  init {
    ModLoadingContext.get().registerExtensionPoint(IConfigScreenFactory::class.java) {
      IConfigScreenFactory { _, screen -> DropConfirmConfig.createScreen(screen) }
    }

    DropConfirmConfig.GSON.load()

    if (FMLEnvironment.dist == Dist.CLIENT)
      NeoForge.EVENT_BUS.register(object {
        @SubscribeEvent
        fun onKeyInput(event: InputEvent.Key) = handleKeyPresses(Minecraft.getInstance())
      })
  }
}