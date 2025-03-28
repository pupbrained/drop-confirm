package xyz.pupbrained.drop_confirm.platform

import xyz.pupbrained.drop_confirm.platform.services.IPlatformHelper
import net.fabricmc.loader.api.FabricLoader

class FabricPlatformHelper : IPlatformHelper {
  override fun getPlatformName(): String {
    return "Fabric"
  }

  override fun isModLoaded(modId: String): Boolean {
    return FabricLoader.getInstance().isModLoaded(modId)
  }

  override fun isDevelopmentEnvironment(): Boolean {
    return FabricLoader.getInstance().isDevelopmentEnvironment
  }
}