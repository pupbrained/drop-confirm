//? if neoforge {
package xyz.pupbrained.drop_confirm.loaders.neoforge

import net.minecraft.client.Minecraft
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.common./*? if <=1.20.4 {*//*Mod.*//*?}*/EventBusSubscriber
import net.neoforged.neoforge.client./*? if <=1.20.4 {*//*ConfigScreenHandler.ConfigScreenFactory*//*?} else {*/gui.IConfigScreenFactory as ConfigScreenFactory/*?}*/
import net.neoforged.fml.common.Mod
import net.neoforged.fml.loading.FMLEnvironment
import net.neoforged.neoforge.client.event.InputEvent
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent
import net.neoforged.neoforge.common.NeoForge
import xyz.pupbrained.drop_confirm.DropConfirm.TOGGLE_KEY
import xyz.pupbrained.drop_confirm.DropConfirm.handleKeyPresses
import xyz.pupbrained.drop_confirm.config.DropConfirmConfig
import xyz.pupbrained.drop_confirm.config.DropConfirmConfigScreen

@Mod("drop_confirm")
@EventBusSubscriber(modid = "drop_confirm", bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
class NeoforgeEntrypoint {
  companion object {
    @SubscribeEvent
    @JvmStatic
    fun registerKeyMappings(event: RegisterKeyMappingsEvent) = event.register(TOGGLE_KEY)
  }

  init {
    ModLoadingContext.get().registerExtensionPoint(ConfigScreenFactory::class.java) {
      ConfigScreenFactory { _, screen -> DropConfirmConfigScreen.createScreen(screen) }
    }

    DropConfirmConfig.GSON.load()

    if (FMLEnvironment.dist == Dist.CLIENT)
      NeoForge.EVENT_BUS.register(object {
        @SubscribeEvent
        fun onKeyInput(event: InputEvent.Key) = handleKeyPresses(Minecraft.getInstance())
      })
  }
}
//?}