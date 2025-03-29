package xyz.pupbrained.drop_confirm.platform

import net.neoforged.fml.ModList
import net.neoforged.fml.loading.FMLLoader
import xyz.pupbrained.drop_confirm.platform.services.IPlatformHelper

class NeoForgePlatformHelper : IPlatformHelper {
  override fun getPlatformName() = "NeoForge"
  override fun isModLoaded(modId: String) = ModList.get().isLoaded(modId)
  override fun isDevelopmentEnvironment() = !FMLLoader.isProduction()
}