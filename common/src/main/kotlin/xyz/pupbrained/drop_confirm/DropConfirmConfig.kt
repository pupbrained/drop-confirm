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
      .serializer { config ->
        GsonConfigSerializerBuilder.create(config)
          .setPath(getConfigDir().resolve("drop_confirm.json"))
          .setJson5(true)
          .build()
      }
      .build()

    @JvmStatic
    fun createScreen(parent: Screen): Screen = YetAnotherConfigLib.create(GSON) { defaults, config, builder ->
      val defaultCategoryBuilder = ConfigCategory.createBuilder()
        .name(Component.translatable("category.drop_confirm.general"))

      val enabled = createOption(
        "option.drop_confirm.enabled",
        "option.drop_confirm.enabled.description",
        defaults.enabled,
        { config.enabled },
        { config.enabled = it },
        { BooleanController(it, true) }
      )

      val playSounds = createOption(
        "option.drop_confirm.play_sounds",
        "option.drop_confirm.play_sounds.description",
        defaults.playSounds,
        { config.playSounds },
        { config.playSounds = it },
        { BooleanController(it, true) }
      )

      val treatAsWhitelist = createOption(
        "option.drop_confirm.treat_as_whitelist",
        "option.drop_confirm.treat_as_whitelist.description",
        defaults.treatAsWhitelist,
        { config.treatAsWhitelist },
        { value ->
          config.treatAsWhitelist = value
          Minecraft.getInstance().screen?.also {
            it.onClose()
            Minecraft.getInstance().setScreen(Minecraft.getInstance().screen?.let { it1 ->
              createScreen(
                it1
              )
            })
          }
        },
        { BooleanController(it, true) }
      )

      val confirmationResetDelay = createOption(
        "option.drop_confirm.confirmation_reset_delay",
        "option.drop_confirm.confirmation_reset_delay.description",
        defaults.confirmationResetDelay,
        { config.confirmationResetDelay },
        { config.confirmationResetDelay = it },
        { DoubleSliderController(it, 1.0, 5.0, 0.05) }
      )

      val blacklistedItems = createListOption(
        if (config.treatAsWhitelist) "option.drop_confirm.whitelisted_items" else "option.drop_confirm.blacklisted_items",
        if (config.treatAsWhitelist) "option.drop_confirm.whitelisted_items.description" else "option.drop_confirm.blacklisted_items.description",
        defaults.blacklistedItems,
        { config.blacklistedItems },
        { config.blacklistedItems = it },
        Items.AIR,
        true,
        { ItemController(it) }
      )

      builder
        .title(Component.translatable("config.drop_confirm.title"))
        .category(
          defaultCategoryBuilder
            .options(listOf(enabled, playSounds, confirmationResetDelay, treatAsWhitelist))
            .group(blacklistedItems)
            .build()
        )
    }.generateScreen(parent)

    private fun <T : Any> createOption(
      name: String,
      description: String,
      defaultValue: T,
      currentValue: () -> T,
      newValue: (T) -> Unit,
      customController: (Option<T>) -> Controller<T>
    ): Option<T> = Option.createBuilder<T>()
      .name(Component.translatable(name))
      .description(
        OptionDescription.createBuilder()
          .text(Component.translatable(description))
          .build()
      )
      .binding(defaultValue, currentValue, newValue)
      .customController(customController)
      .build()

    private fun <T: Any> createListOption(
      name: String,
      description: String,
      defaultValue: List<T>,
      currentValue: () -> List<T>,
      newValue: (List<T>) -> Unit,
      initialValue: T,
      insertEntriesAtEnd: Boolean,
      customController: (ListOptionEntry<T>) -> Controller<T>
    ): ListOption<T> = ListOption.createBuilder<T>()
      .name(Component.translatable(name))
      .description(
        OptionDescription.createBuilder()
          .text(Component.translatable(description))
          .build()
      )
      .binding(defaultValue, currentValue, newValue)
      .initial(initialValue)
      .insertEntriesAtEnd(insertEntriesAtEnd)
      .customController(customController)
      .build()
  }
}
