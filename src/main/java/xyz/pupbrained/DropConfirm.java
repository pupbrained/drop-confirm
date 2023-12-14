package xyz.pupbrained;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;
import xyz.pupbrained.config.DropConfirmConfig;

import java.util.Objects;

public class DropConfirm implements ClientModInitializer {
  public static final Logger LOGGER = LogManager.getLogger("DropConfirm");

  @Override
  public void onInitializeClient() {
    DropConfirmConfig.GSON.load();

    var toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
        "key.drop_confirm.toggle",
        GLFW.GLFW_KEY_UNKNOWN,
        "category.drop_confirm.keybinds"));

    ClientTickEvents.END_CLIENT_TICK.register(client -> {
      while (toggleKey.wasPressed()) {
        var mc = MinecraftClient.getInstance();
        var config = DropConfirmConfig.GSON.instance();
        var player = Objects.requireNonNull(mc.player);

        config.enabled = !config.enabled;

        DropConfirmConfig.GSON.save();

        if (config.playSounds)
          player.playSound(SoundEvents.ENTITY_ITEM_PICKUP, 1.0f, config.enabled ? 1.0f : 0.5f);

        mc.inGameHud.setOverlayMessage(
            Text
                .literal("DropConfirm: ")
                .append(
                    Text
                        .translatable(config.enabled ? "drop_confirm.toggle.on" : "drop_confirm.toggle.off")
                        .formatted(config.enabled ? Formatting.GREEN : Formatting.RED)),
            false);
      }
    });
  }
}
