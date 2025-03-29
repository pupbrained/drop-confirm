package xyz.pupbrained.drop_confirm.platform

import net.fabricmc.loader.api.FabricLoader
import xyz.pupbrained.drop_confirm.platform.services.IPlatformHelper

class FabricPlatformHelper : IPlatformHelper {
  override fun getPlatformName() = "Fabric"
  override fun isModLoaded(modId: String) = FabricLoader.getInstance().isModLoaded(modId)
  override fun isDevelopmentEnvironment() = FabricLoader.getInstance().isDevelopmentEnvironment
}