package xyz.pupbrained.drop_confirm

import dev.isxander.yacl3.api.*
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler
import dev.isxander.yacl3.config.v2.api.SerialEntry
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder
import dev.isxander.yacl3.gui.controllers.BooleanController
import dev.isxander.yacl3.gui.controllers.dropdown.ItemController
import dev.isxander.yacl3.gui.controllers.slider.DoubleSliderController
import dev.isxander.yacl3.platform.YACLPlatform.getConfigDir
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.item.Item
import net.minecraft.world.item.Items

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

    @JvmStatic
    fun createScreen(parent: Screen): Screen = YetAnotherConfigLib.create(GSON) { defaults, config, builder ->
      fun String.t() = Component.translatable(this)
      fun String.desc() = OptionDescription.createBuilder().text(this.t()).build()

      fun <T : Any> option(
        key: String,
        default: T,
        get: () -> T,
        set: (T) -> Unit,
        controller: (Option<T>) -> Controller<T>,
      ): Option<T> = Option.createBuilder<T>()
        .name("option.drop_confirm.$key".t())
        .description("option.drop_confirm.$key.description".desc())
        .binding(default, get, set)
        .customController(controller)
        .build()

      val options = listOf(
        option(
          key = "enabled",
          default = defaults.enabled,
          get = { config.enabled },
          set = { config.enabled = it },
          controller = { BooleanController(it, true) }
        ),

        option(
          key = "play_sounds",
          default = defaults.playSounds,
          get = { config.playSounds },
          set = { config.playSounds = it },
          controller = { BooleanController(it, true) }
        ),

        option(
          key = "confirmation_reset_delay",
          default = defaults.confirmationResetDelay,
          get = { config.confirmationResetDelay },
          set = { config.confirmationResetDelay = it },
          controller = { DoubleSliderController(it, 1.0, 5.0, 0.05) }
        ),

        option(
          key = "treat_as_whitelist",
          default = defaults.treatAsWhitelist,
          get = { config.treatAsWhitelist },
          set = { value ->
            config.treatAsWhitelist = value
            Minecraft.getInstance().apply { execute { setScreen(createScreen(parent)) } }
          },
          controller = { BooleanController(it, true) }
        )
      )

      val listKey = if (config.treatAsWhitelist) "whitelisted" else "blacklisted"
      val itemList = ListOption.createBuilder<Item>().apply {
        name("option.drop_confirm.${listKey}_items".t())
        description("option.drop_confirm.${listKey}_items.description".desc())
        binding(defaults.blacklistedItems, { config.blacklistedItems }, { config.blacklistedItems = it })
        initial(Items.AIR)
        insertEntriesAtEnd(true)
        customController { ItemController(it) }
      }.build()

      builder.apply {
        title("config.drop_confirm.title".t())
        category(
          ConfigCategory.createBuilder().apply {
            name("category.drop_confirm.general".t())
            options(options)
            group(itemList)
          }.build()
        )
      }
    }.generateScreen(parent)
  }
}
