//? if >=1.20.1 && !forge {
package xyz.pupbrained.drop_confirm.config

import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.SerialEntry
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import net.minecraft.world.item.Item

import dev.isxander.yacl3.platform.YACLPlatform
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty

object DropConfirmConfig {
  class DropConfirmConfigInternal {
    @SerialEntry
    var enabled = true

    @SerialEntry
    var shouldPlaySounds = true

    @SerialEntry
    var treatAsWhitelist = false

    @SerialEntry
    var confirmationResetDelay = 1.0

    @SerialEntry
    var blacklistedItems: List<Item> = emptyList()
  }

  val HANDLER: ConfigClassHandler<DropConfirmConfigInternal> =
    ConfigClassHandler.createBuilder(DropConfirmConfigInternal::class.java)
      .serializer {
        GsonConfigSerializerBuilder.create(it)
          .setPath(YACLPlatform.getConfigDir().resolve("drop_confirm.json"))
          .build()
      }
      .build()

  fun save() = HANDLER.save()
  fun load() = HANDLER.load()

  @JvmStatic
  @get:JvmName("isEnabled")
  var enabled: Boolean by ConfigDelegate(DropConfirmConfigInternal::enabled)

  @JvmStatic
  @get:JvmName("shouldPlaySounds")
  var shouldPlaySounds: Boolean by ConfigDelegate(DropConfirmConfigInternal::shouldPlaySounds)

  @JvmStatic
  @get:JvmName("shouldTreatAsWhitelist")
  var treatAsWhitelist: Boolean by ConfigDelegate(DropConfirmConfigInternal::treatAsWhitelist)

  @JvmStatic
  @get:JvmName("getResetDelay")
  var confirmationResetDelay: Double by ConfigDelegate(DropConfirmConfigInternal::confirmationResetDelay)

  @JvmStatic
  @get:JvmName("getBlacklistedItems")
  var blacklistedItems: List<Item> by ConfigDelegate(DropConfirmConfigInternal::blacklistedItems)

  private class ConfigDelegate<T>(private val propertyRef: KMutableProperty1<DropConfirmConfigInternal, T>) {
    operator fun getValue(thisRef: Any?, property: KProperty<*>): T = propertyRef.get(HANDLER.instance())
    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) = propertyRef.set(HANDLER.instance(), value)
  }
}
//?} else {
/*package xyz.pupbrained.drop_confirm.config

import com.google.gson.GsonBuilder
import net.fabricmc.loader.api.FabricLoader
import xyz.pupbrained.drop_confirm.DropConfirm
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.absolutePathString

object DropConfirmConfig {
  @JvmStatic
  @get:JvmName("isEnabled")
  var enabled = true

  @JvmStatic
  @get:JvmName("shouldPlaySounds")
  var shouldPlaySounds = true

  @JvmStatic
  @get:JvmName("getResetDelay")
  var confirmationResetDelay = 1.0F

  private val GSON = GsonBuilder().setPrettyPrinting().create()

  private val configFile = FabricLoader.getInstance().configDir.resolve("drop_confirm.json")

  private var isLoaded = false

  fun load() {
    if (isLoaded) return

    if (configFile.exists()) {
      try {
        Files.newBufferedReader(configFile, StandardCharsets.UTF_8).use { reader ->
          val loadedData = GSON.fromJson(reader, Map::class.java)

          enabled = loadedData["enabled"] as? Boolean ?: enabled
          shouldPlaySounds = loadedData["playSounds"] as? Boolean ?: shouldPlaySounds
          confirmationResetDelay =
            (loadedData["confirmationResetDelay"] as? Number)?.toFloat() ?: confirmationResetDelay
        }
      } catch (e: Exception) {
        DropConfirm.LOGGER.error("Failed to load DropConfirmConfig from ${configFile.absolutePathString()}", e)
      }
    }

    isLoaded = true
  }

  fun save() {
    try {
      configFile.parent.createDirectories()

      val dataToSave = mapOf(
        "enabled" to enabled,
        "playSounds" to shouldPlaySounds,
        "confirmationResetDelay" to confirmationResetDelay
      )

      Files.newBufferedWriter(configFile, StandardCharsets.UTF_8).use { GSON.toJson(dataToSave, it) }
    } catch (e: Exception) {
      DropConfirm.LOGGER.error("Failed to save DropConfirm config to ${configFile.absolutePathString()}", e)
    }
  }
}
*///?}
