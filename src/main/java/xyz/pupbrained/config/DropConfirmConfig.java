package xyz.pupbrained.config;

import dev.isxander.yacl3.api.*;
import dev.isxander.yacl3.config.v2.api.ConfigClassHandler;
import dev.isxander.yacl3.config.v2.api.SerialEntry;
import dev.isxander.yacl3.config.v2.api.serializer.GsonConfigSerializerBuilder;
import dev.isxander.yacl3.gui.controllers.BooleanController;
import dev.isxander.yacl3.gui.controllers.dropdown.ItemController;
import dev.isxander.yacl3.gui.controllers.slider.DoubleSliderController;
import dev.isxander.yacl3.platform.YACLPlatform;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class DropConfirmConfig {
  public static final ConfigClassHandler<DropConfirmConfig> GSON = ConfigClassHandler.createBuilder(DropConfirmConfig.class)
    .serializer(config -> GsonConfigSerializerBuilder.create(config)
      .setPath(YACLPlatform.getConfigDir().resolve("drop_confirm.json"))
      .build())
    .build();

  @SerialEntry
  public boolean enabled = true;

  @SerialEntry
  public boolean playSounds = true;

  @SerialEntry
  public boolean treatAsWhitelist = false;

  @SerialEntry
  public double confirmationResetDelay = 1.0;

  @SerialEntry
  public List<Item> blacklistedItems = List.of();

  public static Screen createScreen(Screen parent) {
    return YetAnotherConfigLib.create(DropConfirmConfig.GSON, ((defaults, config, builder) -> {
      var defaultCategoryBuilder = ConfigCategory.createBuilder()
        .name(Text.translatable("category.drop_confirm.general"));

      var enabled = createOption(
        "option.drop_confirm.enabled",
        "option.drop_confirm.enabled.description",
        defaults.enabled,
        () -> config.enabled,
        val -> config.enabled = val,
        booleanOption -> new BooleanController(booleanOption, true)
      );

      var playSounds = createOption(
        "option.drop_confirm.play_sounds",
        "option.drop_confirm.play_sounds.description",
        defaults.playSounds,
        () -> config.playSounds,
        val -> config.playSounds = val,
        booleanOption -> new BooleanController(booleanOption, true)
      );

      var treatAsWhitelist = createOption(
        "option.drop_confirm.treat_as_whitelist",
        "option.drop_confirm.treat_as_whitelist.description",
        defaults.treatAsWhitelist,
        () -> config.treatAsWhitelist,
        val -> config.treatAsWhitelist = val,
        booleanOption -> new BooleanController(booleanOption, true)
      );

      var confirmationResetDelay = createOption(
        "option.drop_confirm.confirmation_reset_delay",
        "option.drop_confirm.confirmation_reset_delay.description",
        defaults.confirmationResetDelay,
        () -> config.confirmationResetDelay,
        val -> config.confirmationResetDelay = val,
        doubleOption -> new DoubleSliderController(doubleOption, 1.0, 5.0, 0.05)
      );

      // FIXME: Figure out a way to update the strings instantly
      //        Currently, the strings are only updated when the
      //        config screen is reloaded.
      var blacklistedItems = createListOption(
        config.treatAsWhitelist ? "option.drop_confirm.whitelisted_items" : "option.drop_confirm.blacklisted_items",
        config.treatAsWhitelist ? "option.drop_confirm.whitelisted_items.description" : "option.drop_confirm.blacklisted_items.description",
        defaults.blacklistedItems,
        () -> config.blacklistedItems,
        val -> config.blacklistedItems = val,
        Items.AIR,
        true,
        ItemController::new
      );

      return builder
        .title(Text.translatable("config.drop_confirm.title"))
        .category(
          defaultCategoryBuilder
            .options(List.of(enabled, playSounds, confirmationResetDelay, treatAsWhitelist))
            .group(blacklistedItems)
            .build()
        );
    })).generateScreen(parent);
  }

  private static <T> Option<T> createOption (
    String name,
    String description,
    T defaultValue,
    Supplier<T> currentValue,
    Consumer<T> newValue,
    Function<Option<T>, Controller<T>> customController
  ) {
    return Option.<T>createBuilder()
      .name(Text.translatable(name))
      .description(
        OptionDescription.createBuilder()
          .text(Text.translatable(description))
          .build()
      )
      .binding(defaultValue, currentValue, newValue)
      .customController(customController)
      .build();
  }

  private static <T> ListOption<T> createListOption (
    String name,
    String description,
    List<T> defaultValue,
    Supplier<List<T>> currentValue,
    Consumer<List<T>> newValue,
    T initialValue,
    Boolean insertEntriesAtEnd,
    Function<ListOptionEntry<T>, Controller<T>> customController
  ) {
    return ListOption.<T>createBuilder()
      .name(Text.translatable(name))
      .description(
        OptionDescription.createBuilder()
          .text(Text.translatable(description))
          .build()
      )
      .binding(defaultValue, currentValue, newValue)
      .initial(initialValue)
      .insertEntriesAtEnd(insertEntriesAtEnd)
      .customController(customController)
      .build();
  }
}
