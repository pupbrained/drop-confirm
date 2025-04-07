package xyz.pupbrained.drop_confirm

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.KeyMapping
import org.lwjgl.glfw.GLFW

//? if >=1.20.1 {
import xyz.pupbrained.drop_confirm.config.DropConfirmConfig
import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.ChatFormatting
//?}

object DropConfirm {
  var isConfirmed = false

  val TOGGLE_KEY = KeyMapping(
    "key.drop_confirm.toggle",
    InputConstants.Type.KEYSYM,
    GLFW.GLFW_KEY_UNKNOWN,
    "category.drop_confirm.keybinds"
  )

  //? if >=1.20.1 {
  fun handleKeyPresses(mc: Minecraft) {
    while (TOGGLE_KEY.consumeClick()) {
      DropConfirmConfig.GSON.instance().apply {
        mc.player?.let {
          enabled = !enabled

          DropConfirmConfig.GSON.save()

          if (playSounds) it.playSound(SoundEvents.ITEM_PICKUP, 1.0f, if (enabled) 1.0f else 0.5f)

          mc.gui.setOverlayMessage(
            Component.literal("DropConfirm: ").append(
              Component
                .translatable(if (enabled) "drop_confirm.toggle.on" else "drop_confirm.toggle.off")
                .withStyle(if (enabled) ChatFormatting.GREEN else ChatFormatting.RED)
            ),
            false
          )
        } ?: return
      }
    }
  }
  //?}
}