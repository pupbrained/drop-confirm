package xyz.pupbrained.drop_confirm.mixin;

import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
//? if <=1.21.1
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.pupbrained.drop_confirm.DropConfirm;
import xyz.pupbrained.drop_confirm.config.DropConfirmConfig;

@Mixin(Inventory.class)
public class SlotChangeMixin {
  @Unique private static int drop_confirm$lastSlot = 0;

  //? if <=1.21.1
  @Shadow public int selected;

  @Inject(
    method = /*? if >=1.21.5 {*//*"setSelectedSlot"*//*?} elif 1.21.4 {*//*"setSelectedHotbarSlot"*//*?} else {*/"tick"/*?}*/,
    at = @At("TAIL")
  )
  private void onSlotSet(
    /*? if >=1.21.4 {*//*int selected,*//*?}*/
    CallbackInfo ci
  ) {
    if (!DropConfirmConfig.isEnabled()) return;

    if (selected != drop_confirm$lastSlot) {
      DropConfirm.setConfirmed(false);
      drop_confirm$lastSlot = selected;
    }
  }
}
