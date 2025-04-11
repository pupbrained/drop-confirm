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

import /^? if fabric {^/net.fabricmc.loader.api.FabricLoader/^?} else {^//^net.minecraftforge.fml.loading.FMLPaths^//^?}^/
import com.google.gson.GsonBuilder
import net.minecraft.world.item.Item
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
  @get:JvmName("shouldTreatAsWhitelist")
  var treatAsWhitelist = false // Added

  @JvmStatic
  @get:JvmName("getResetDelay")
  var confirmationResetDelay = 1.0F

  @JvmStatic
  @get:JvmName("getBlacklistedItems")
  var blacklistedItems: MutableList<Item> = mutableListOf()

  private val GSON = GsonBuilder().setPrettyPrinting().create()

  private val configFile =
    /^? if fabric {^/FabricLoader.getInstance().configDir/^?} else {^//^FMLPaths.CONFIGDIR.get()^//^?}^/
    .resolve("drop_confirm.json")

  private var isLoaded = false

  fun load() {
    if (isLoaded) return

    if (configFile.exists()) {
      try {
        Files.newBufferedReader(configFile, StandardCharsets.UTF_8).use { reader ->
          val loadedData = GSON.fromJson(reader, Map::class.java) ?: emptyMap<Any, Any>()

          enabled = loadedData["enabled"] as? Boolean ?: enabled
          shouldPlaySounds = loadedData["playSounds"] as? Boolean ?: shouldPlaySounds
          treatAsWhitelist = loadedData["treatAsWhitelist"] as? Boolean ?: treatAsWhitelist // Added loading
          confirmationResetDelay =
            (loadedData["confirmationResetDelay"] as? Number)?.toFloat() ?: confirmationResetDelay
          (loadedData["blacklistedItems"] as? List<*>)?.let { list ->
            blacklistedItems = list.mapNotNull { it as Item? }.toMutableList()
          }
        }
      } catch (e: Exception) {
        DropConfirm.LOGGER.error("Failed to load DropConfirmConfig from ${configFile.absolutePathString()}", e)
      }
    } else {
      save()
    }

    isLoaded = true
  }

  fun save() {
    try {
      configFile.parent.createDirectories()

      val dataToSave = mapOf(
        "enabled" to enabled,
        "playSounds" to shouldPlaySounds,
        "treatAsWhitelist" to treatAsWhitelist, // Added saving
        "confirmationResetDelay" to confirmationResetDelay,
        "blacklistedItems" to blacklistedItems // Added saving
      )

      Files.newBufferedWriter(configFile, StandardCharsets.UTF_8).use { GSON.toJson(dataToSave, it) }
    } catch (e: Exception) {
      DropConfirm.LOGGER.error("Failed to save DropConfirm config to ${configFile.absolutePathString()}", e)
    }
  }
}
*///?}
