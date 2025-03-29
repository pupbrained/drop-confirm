package xyz.pupbrained.drop_confirm.mixin;

import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.pupbrained.drop_confirm.DropConfirm;
import xyz.pupbrained.drop_confirm.DropConfirmConfig;

@Mixin(Inventory.class)
public class InventorySlotChangeMixin {
  @Unique
  private static int drop_confirm$lastSlot = 0;

  @Inject(
    method = "setSelectedHotbarSlot",
    at = @At("TAIL")
  )
  private void onSlotSet(int slot, CallbackInfo ci) {
    if (!DropConfirmConfig.Companion.getGSON().instance().getEnabled())
      return;

    if (slot != drop_confirm$lastSlot) {
      DropConfirm.INSTANCE.setConfirmed(false);
      drop_confirm$lastSlot = slot;
    }
  }
}
