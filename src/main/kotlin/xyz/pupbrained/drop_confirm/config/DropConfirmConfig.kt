//? if >=1.20.1 {
package xyz.pupbrained.drop_confirm.config

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.SerialEntry
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import dev.isxander.yacl3.platform.YACLPlatform.getConfigDir
import net.minecraft.world.item.Item

class DropConfirmConfig {
  @SerialEntry
  var enabled = true

  @SerialEntry
  var playSounds = true

  @SerialEntry
  var treatAsWhitelist = false

  @SerialEntry
  var confirmationResetDelay = 1.0

  @SerialEntry
  var blacklistedItems: List<Item> = emptyList()

  companion object {
    val GSON: ConfigClassHandler<DropConfirmConfig> = ConfigClassHandler.createBuilder(DropConfirmConfig::class.java)
      .serializer {
        GsonConfigSerializerBuilder.create(it)
          .setPath(getConfigDir().resolve("drop_confirm.json"))
          .setJson5(true)
          .build()
      }
      .build()
  }
}
//?}