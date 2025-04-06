package xyz.pupbrained.drop_confirm.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xyz.pupbrained.drop_confirm.DropConfirm;
import xyz.pupbrained.drop_confirm.config.DropConfirmConfig;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Mixin(LocalPlayer.class)
public class ItemDropMixin {
  @Unique
  private static final ScheduledExecutorService drop_confirm$scheduler =
    Executors.newSingleThreadScheduledExecutor(r -> {
      Thread thread = new Thread(r, "DropConfirm-Scheduler");
      thread.setDaemon(true);
      return thread;
    });

  @Inject(method = "swing", at = @At("HEAD"), cancellable = true)
  private void onHandSwing(InteractionHand hand, CallbackInfo ci) {
    if (DropConfirm.INSTANCE.isConfirmed()) ci.cancel();
  }

  @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
  private void onItemDrop(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
    final var mc = Minecraft.getInstance();
    if (mc.player == null) return;

    final var config = DropConfirmConfig.Companion.getGSON().instance();
    final var player = mc.player;
    final var inventory = player.getInventory();
    var itemStack = inventory./*? if >=1.21.5 {*/getSelectedItem/*?} else {*//*getSelected*//*?}*/();

    if (!config.getEnabled() || itemStack.isEmpty())
      return;

    final var action = entireStack
      ? ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS
      : ServerboundPlayerActionPacket.Action.DROP_ITEM;

    if (config.getBlacklistedItems().contains(itemStack.getItem()) ^ config.getTreatAsWhitelist())
      return;

    if (!DropConfirm.INSTANCE.isConfirmed()) {
      mc.gui.setOverlayMessage(
        Component.translatable(
          "drop_confirm.confirmation",
          mc.options.keyDrop.getTranslatedKeyMessage().getString()
        ),
        false
      );

      DropConfirm.INSTANCE.setConfirmed(true);

      drop_confirm$scheduler.schedule(() -> {
        synchronized (DropConfirm.class) {
          DropConfirm.INSTANCE.setConfirmed(false);
        }
      }, (long) (config.getConfirmationResetDelay() * 1000), TimeUnit.MILLISECONDS);
    } else {
      DropConfirm.INSTANCE.setConfirmed(false);
      itemStack = inventory.removeFromSelected(entireStack);

      mc.gui.setOverlayMessage(Component.empty(), false);

      if (config.getPlaySounds())
        player.playSound(SoundEvents.BUNDLE_DROP_CONTENTS, 1.0F, 1.0F);

      player.connection.send(new ServerboundPlayerActionPacket(action, BlockPos.ZERO, Direction.DOWN));
    }

    cir.setReturnValue(!itemStack.isEmpty());
  }
}