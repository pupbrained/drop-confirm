package xyz.pupbrained.drop_confirm.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
//? if >=1.19.4 {
import net.minecraft.network.chat.Component;
 //?} else {
/*import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
*///?}
import net.minecraft.sounds.SoundEvents;
import net.minecraft.network.protocol.game.ServerboundPlayerActionPacket;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
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

  @Shadow
  @Final
  protected Minecraft minecraft;

  @Inject(method = "swing", at = @At("HEAD"), cancellable = true)
  private void onHandSwing(InteractionHand hand, CallbackInfo ci) {
    if (DropConfirm.isConfirmed()) ci.cancel();
  }

  @Inject(method = "drop", at = @At("HEAD"), cancellable = true)
  private void onItemDrop(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
    final LocalPlayer player = (LocalPlayer) (Object) this;
    final Inventory inventory = player./*? if >1.16.5 {*/getInventory()/*?} else {*//*inventory*//*?}*/;
    ItemStack itemStack = inventory./*? if >=1.21.5 {*//*getSelectedItem*//*?} else {*/getSelected/*?}*/();

    if (!DropConfirmConfig.isEnabled() || itemStack.isEmpty())
      return;

    final ServerboundPlayerActionPacket.Action action = entireStack
      ? ServerboundPlayerActionPacket.Action.DROP_ALL_ITEMS
      : ServerboundPlayerActionPacket.Action.DROP_ITEM;

    //? if >=1.20.1 && !forge {
    if (DropConfirmConfig.getBlacklistedItems().contains(itemStack.getItem()) ^ DropConfirmConfig.shouldTreatAsWhitelist())
      return;
    //?}

    if (!DropConfirm.isConfirmed()) {
      minecraft.gui.setOverlayMessage(
        /*? if >=1.19.4 {*/Component.translatable/*?} else {*//*new TranslatableComponent*//*?}*/(
          "drop_confirm.confirmation",
          minecraft.options.keyDrop.getTranslatedKeyMessage()/*? if >1.15.2 {*/.getString()/*?}*//*? if <=1.19.4 {*//*.toUpperCase()*//*?}*/
        ),
        false
      );

      DropConfirm.setConfirmed(true);

      drop_confirm$scheduler.schedule(() -> {
        synchronized (DropConfirm.class) {
          DropConfirm.setConfirmed(false);
        }
      }, (long) (DropConfirmConfig.getResetDelay() * 1000), TimeUnit.MILLISECONDS);
    } else {
      DropConfirm.setConfirmed(false);
      //? if >=1.19.4 {
      itemStack = inventory.removeFromSelected(entireStack);
       //?} else {
      /*itemStack = inventory.removeItem(inventory.selected, entireStack && !inventory.getSelected().isEmpty() ? inventory.getSelected().getCount() : 1);
      *///?}

      minecraft.gui.setOverlayMessage(/*? if >=1.19.4 {*/Component.empty()/*?} else if >=1.16.5 {*//*TextComponent.EMPTY*//*?} else {*//*new TextComponent("")*//*?}*/, false);

      if (DropConfirmConfig.shouldPlaySounds())
        player.playSound(SoundEvents./*? if >=1.20.1 {*/BUNDLE_DROP_CONTENTS/*?} else {*//*ITEM_PICKUP*//*?}*/, 1.0F, 1.0F);

      player.connection.send(new ServerboundPlayerActionPacket(action, BlockPos.ZERO, Direction.DOWN));
    }

    cir.setReturnValue(!itemStack.isEmpty());
  }
}
