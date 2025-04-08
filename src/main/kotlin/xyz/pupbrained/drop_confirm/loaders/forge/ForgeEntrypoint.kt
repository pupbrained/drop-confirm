//? if forge {
package xyz.pupbrained.drop_confirm.loaders.forge

import net.minecraft.client.Minecraft
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.ConfigScreenHandler
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.client.event.RegisterKeyMappingsEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventBusSubscriber
import net.minecraftforge.fml.loading.FMLEnvironment
import thedarkcolour.kotlinforforge.forge.FORGE_BUS
import thedarkcolour.kotlinforforge.forge.LOADING_CONTEXT
import xyz.pupbrained.drop_confirm.DropConfirm
import xyz.pupbrained.drop_confirm.DropConfirm.TOGGLE_KEY
import xyz.pupbrained.drop_confirm.DropConfirm.handleKeyPresses
import xyz.pupbrained.drop_confirm.config.DropConfirmConfig
import xyz.pupbrained.drop_confirm.config.DropConfirmConfigScreen

@Mod("drop_confirm")
@EventBusSubscriber(modid = "drop_confirm", bus = EventBusSubscriber.Bus.MOD, value = [Dist.CLIENT])
class ForgeEntrypoint {
  companion object {
    @SubscribeEvent
    @JvmStatic
    fun registerKeyMappings(event: RegisterKeyMappingsEvent) = event.register(TOGGLE_KEY)
  }

  init {
    LOADING_CONTEXT.registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory::class.java) {
      ConfigScreenHandler.ConfigScreenFactory { _, screen ->
        try {
          DropConfirmConfigScreen(screen)
        } catch (e: Throwable) {
          DropConfirm.LOGGER.error("Failed to load config screen", e)
          screen
        }
      }
    }

    DropConfirmConfig.load()

    if (FMLEnvironment.dist == Dist.CLIENT)
      FORGE_BUS.register(object {
        @SubscribeEvent
        fun onKeyInput(event: InputEvent.Key) = handleKeyPresses(Minecraft.getInstance())
      })
  }
}
//?}
