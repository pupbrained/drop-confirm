package xyz.pupbrained.mixin;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.pupbrained.DropConfirm;
import xyz.pupbrained.Util;
import xyz.pupbrained.config.DropConfirmConfig;

import java.util.Objects;

@Mixin(ClientPlayerEntity.class)
public abstract class ItemDropMixin {
  @Inject(method = "dropSelectedItem", at = @At("HEAD"), cancellable = true)
  private void onItemDrop(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
    final var mc = MinecraftClient.getInstance();
    final var config = DropConfirmConfig.GSON.instance();
    final var player = Objects.requireNonNull(mc.player);

    if (Util.isDisabled(config) || Util.isMainHandStackEmpty(player))
      return;

    final var action = entireStack
      ? PlayerActionC2SPacket.Action.DROP_ALL_ITEMS
      : PlayerActionC2SPacket.Action.DROP_ITEM;
    final var inventory = player.getInventory();
    var itemStack = inventory.getMainHandStack();

    if (config.blacklistedItems.contains(itemStack.getItem())) {
      if(!config.treatAsWhitelist) return;
    } else if (config.treatAsWhitelist) {
      return;
    }

    if (!Util.confirmed) {
      mc.inGameHud.setOverlayMessage(
        Text.of(
          String.format(
            Text
              .translatable("drop_confirm.confirmation")
              .getString(),
            mc
              .options
              .dropKey
              .getBoundKeyLocalizedText()
              .getString()
          )
        ), false);
      Util.confirmed = true;
      new Thread(() -> {
        try {
          Thread.sleep((long) (config.confirmationResetDelay * 1000));

          synchronized (Util.class) {
            Util.confirmed = false;
          }
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          DropConfirm.LOGGER.error("Interrupted while waiting to reset confirmation.", e);
        }
      }).start();
    } else {
      Util.confirmed = false;
      itemStack = inventory.dropSelectedItem(entireStack);

      mc.inGameHud.setOverlayMessage(Text.empty(), false);

      if (config.playSounds)
        player.playSound(SoundEvents.ITEM_BUNDLE_DROP_CONTENTS, 1.0F, 1.0F);

      player.networkHandler.sendPacket(new PlayerActionC2SPacket(action, BlockPos.ORIGIN, Direction.DOWN));
    }

    cir.setReturnValue(!itemStack.isEmpty());
  }
}
