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
          .setPath(getConfigDir().resolve("drop_confirm.json5"))
          .setJson5(true)
          .build()
      }
      .build()
  }
}
//?} else {
/*package xyz.pupbrained.drop_confirm.config

import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import xyz.pupbrained.drop_confirm.DropConfirm
import java.io.FileReader
import java.io.FileWriter
import java.nio.file.Paths

class DropConfirmConfig {
  var enabled: Boolean = true
  var playSounds: Boolean = true
  var confirmationResetDelay: Float = 1.0F

  companion object {
    private val GSON = GsonBuilder().setPrettyPrinting().create()
    private val configFile = Paths.get("config", "drop_confirm.json").toFile()
    private val CONFIG_TYPE = object : TypeToken<DropConfirmConfig>() {}.type
    private var instance: DropConfirmConfig? = null

    fun load(): DropConfirmConfig =
      instance ?: (if (configFile.exists()) {
        try {
          FileReader(configFile).use { GSON.fromJson(it, CONFIG_TYPE) }
        } catch (e: Exception) {
          DropConfirm.LOGGER.error("Failed to load DropConfirmConfig from ${configFile.absolutePath}", e)
          DropConfirmConfig()
        }
      } else {
        DropConfirmConfig()
      }).also { instance = it }

    fun save() {
      configFile.parentFile.mkdirs()
      try {
        FileWriter(configFile).use { GSON.toJson(instance, it) }
      } catch (e: Exception) {
        DropConfirm.LOGGER.error("Failed to save DropConfirm config to ${configFile.absolutePath}", e)
      }
    }

    fun get(): DropConfirmConfig = instance ?: load()

    // Add a reload method to refresh from the saved file
    fun reload() {
      instance = null
      load()
    }

    // Convenience methods for accessing config values
    fun isEnabled(): Boolean = get().enabled

    // Check if sounds should be played
    fun shouldPlaySounds(): Boolean = get().playSounds

    // Get the confirmation reset delay
    fun getResetDelay(): Float = get().confirmationResetDelay
  }
}
*///?}