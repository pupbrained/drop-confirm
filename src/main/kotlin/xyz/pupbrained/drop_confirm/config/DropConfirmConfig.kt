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

import net./^? if fabric {^/fabricmc.loader.api.FabricLoader/^?} else {^//^minecraftforge.fml.loading.FMLPaths^//^?}^/

//? if fabric {
import net.minecraft.core./^? if <=1.18.2 {^//^Registry as BuiltInRegistries^//^?} else {^/registries.BuiltInRegistries/^?}^/
//?} else {
/^import net.minecraftforge.registries.ForgeRegistries as BuiltInRegistries
^///?}

import com.google.gson.GsonBuilder
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import xyz.pupbrained.drop_confirm.DropConfirm
import java.nio.charset.StandardCharsets
import java.nio.file.Files
import kotlin.io.path.createDirectories
import kotlin.io.path.exists
import kotlin.io.path.absolutePathString

enum class ConfirmationMode {
  DIALOG, HOTBAR, CHAT;

  fun getTranslationKey(): String = "option.drop_confirm.confirmation_mode.${name.lowercase()}"
}

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
  @get:JvmName("getConfirmationMode")
  var confirmationMode = ConfirmationMode.DIALOG

  @JvmStatic
  @get:JvmName("getBlacklistedItems")
  var blacklistedItems: MutableList<Item> = mutableListOf()

  private val gson = GsonBuilder().setPrettyPrinting().create()

  private val configFile =
    /^? if fabric {^/FabricLoader.getInstance().configDir/^?} else {^//^FMLPaths.CONFIGDIR.get()^//^?}^/
    .resolve("drop_confirm.json")

  private var isLoaded = false

  fun load() {
    if (isLoaded) return

    try {
      if (configFile.exists()) {
        Files.newBufferedReader(configFile, StandardCharsets.UTF_8).use { reader ->
          val loadedData = gson.fromJson(reader, Map::class.java) ?: emptyMap<Any, Any>()

          enabled = loadedData["enabled"] as? Boolean ?: enabled
          shouldPlaySounds = loadedData["playSounds"] as? Boolean ?: shouldPlaySounds
          treatAsWhitelist = loadedData["treatAsWhitelist"] as? Boolean ?: treatAsWhitelist
          confirmationResetDelay =
            (loadedData["confirmationResetDelay"] as? Number)?.toFloat() ?: confirmationResetDelay

          (loadedData["confirmationMode"] as? String)?.let {
            runCatching { ConfirmationMode.valueOf(it.uppercase()) }
              .getOrNull()?.let { mode -> confirmationMode = mode }
          }

          (loadedData["blacklistedItems"] as? List<*>)?.mapNotNull { itemId ->
            (itemId as? String)?.let { id ->
              val fullId = if (id.contains(":")) id else "minecraft:$id"
              BuiltInRegistries./^? if fabric {^/ITEM.get/^?} else {^//^ITEMS.getValue^//^?}^/(
                ResourceLocation.tryParse(
                  fullId
                )
              )
            }
          }?.toMutableList()?.let { blacklistedItems = it }
        }
      }
    } catch (e: Exception) {
      DropConfirm.LOGGER.error("Failed to load configuration", e)
    } finally {
      if (!configFile.exists()) save()
      isLoaded = true
    }
  }

  fun save() {
    try {
      configFile.parent.createDirectories()

      // Convert Item objects to their string identifiers before saving
      val itemIdentifiers = blacklistedItems.map { item ->
        BuiltInRegistries./^? if fabric {^/ITEM/^?} else {^//^ITEMS^//^?}^/.getKey(item).toString()
      }

      val dataToSave = mapOf(
        "enabled" to enabled,
        "playSounds" to shouldPlaySounds,
        "treatAsWhitelist" to treatAsWhitelist,
        "confirmationResetDelay" to confirmationResetDelay,
        "confirmationMode" to confirmationMode.name,
        "blacklistedItems" to itemIdentifiers
      )

      Files.newBufferedWriter(configFile, StandardCharsets.UTF_8).use { gson.toJson(dataToSave, it) }
    } catch (e: Exception) {
      DropConfirm.LOGGER.error("Failed to save DropConfirm config to ${configFile.absolutePathString()}", e)
    }
  }
}
*///?}
