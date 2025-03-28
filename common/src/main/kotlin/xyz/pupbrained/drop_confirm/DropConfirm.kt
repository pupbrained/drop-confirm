package xyz.pupbrained.drop_confirm

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.ChatFormatting
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import org.lwjgl.glfw.GLFW

object DropConfirm {
  var isConfirmed = false

  val TOGGLE_KEY = KeyMapping(
    "key.drop_confirm.toggle",
    InputConstants.Type.KEYSYM,
    GLFW.GLFW_KEY_UNKNOWN,
    "category.drop_confirm.keybinds"
  )

  fun handleKeyPresses(mc: Minecraft) {
    while (TOGGLE_KEY.consumeClick()) {
      val config = DropConfirmConfig.GSON.instance()
      val player = mc.player ?: return

      (!config.enabled).also { config.enabled = it }
      DropConfirmConfig.GSON.save()

      if (config.playSounds)
        player.playSound(
          SoundEvents.ITEM_PICKUP,
          1.0f,
          if (config.enabled) 1.0f else 0.5f
        )

      mc.gui.setOverlayMessage(
        Component.literal("DropConfirm: ").append(
          Component.translatable(
            if (config.enabled) "drop_confirm.toggle.on" else "drop_confirm.toggle.off"
          ).withStyle(
            if (config.enabled) ChatFormatting.GREEN else ChatFormatting.RED
          )
        ),
        false
      )
    }
  }

  fun init() {
    DropConfirmConfig.GSON.load()
  }
}
