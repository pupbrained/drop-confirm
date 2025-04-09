package xyz.pupbrained.drop_confirm

//? if <1.20.1 || forge {
/*import io.github.cdagaming.unicore.utils.TranslationUtils
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
*///?}

//? if >=1.19.4 {
import net.minecraft.network.chat.Component
//?} else {
/*import net.minecraft.network.chat.TextComponent
import net.minecraft.network.chat.TranslatableComponent
*///?}

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.ChatFormatting
import net.minecraft.client.KeyMapping
import net.minecraft.client.Minecraft
import net.minecraft.sounds.SoundEvents
import org.lwjgl.glfw.GLFW
import xyz.pupbrained.drop_confirm.config.DropConfirmConfig

object DropConfirm {
  //? if <1.20.1 || forge {
  /*@JvmStatic
  val TRANSLATOR: TranslationUtils = TranslationUtils("drop_confirm", true)
    .setDefaultLanguage("en_us")
    .build()

  @JvmStatic
  val LOGGER: Logger = LogManager.getLogger("DropConfirm")
  *///?}

  var isConfirmed = false

  val TOGGLE_KEY = KeyMapping(
    "key.drop_confirm.toggle",
    InputConstants.Type.KEYSYM,
    GLFW.GLFW_KEY_J,
    "category.drop_confirm.keybinds"
  )

  fun handleKeyPresses(mc: Minecraft) {
    while (TOGGLE_KEY.consumeClick()) {
      /*? if >=1.20.1 && !forge {*/DropConfirmConfig.GSON.instance()/*?} else {*//*DropConfirmConfig*//*?}*/.apply {
        @Suppress("UNNECESSARY_SAFE_CALL", "KotlinRedundantDiagnosticSuppress")
        mc.player?.let {
          //? if >=1.20.1 && !forge {
          enabled = !enabled
          DropConfirmConfig.GSON.save()
          //?} else {
          /*get().enabled = !isEnabled()
          save()

          val enabled = isEnabled()
          val playSounds = shouldPlaySounds()
          *///?}

          if (playSounds)
            it.playSound(SoundEvents.ITEM_PICKUP, 1.0f, if (enabled) 1.0f else 0.5f)

          mc.gui.setOverlayMessage(
            //? if >=1.19.4 {
            Component.literal("DropConfirm: ").append(
              Component
                .translatable(if (enabled) "drop_confirm.toggle.on" else "drop_confirm.toggle.off")
                .withStyle(if (enabled) ChatFormatting.GREEN else ChatFormatting.RED)
            ),
            //?} else {
            /*TextComponent("DropConfirm: ").append(
              TranslatableComponent(if (enabled) "drop_confirm.toggle.on" else "drop_confirm.toggle.off")
                .withStyle(if (enabled) ChatFormatting.GREEN else ChatFormatting.RED)
            ),
            *///?}
            false
          )
        }
      }
    }
  }
}