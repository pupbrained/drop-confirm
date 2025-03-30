package xyz.pupbrained.drop_confirm.mixin;

import net.minecraft.world.entity.player.Inventory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xyz.pupbrained.drop_confirm.DropConfirm;
import xyz.pupbrained.drop_confirm.DropConfirmConfig;

@Mixin(Inventory.class)
public abstract class InventorySlotChangeMixin {
  @Shadow
  public int selected;

  @Unique
  private int drop_confirm$lastSlot = -1;

  @Inject(
    method = "tick",
    at = @At("HEAD")
  )
  private void onTickCheckSlot(CallbackInfo ci) {
    if (!DropConfirmConfig.Companion.getGSON().instance().getEnabled()) return;

    if (this.drop_confirm$lastSlot == -1) {
      this.drop_confirm$lastSlot = this.selected;
      return;
    }

    if (this.selected != this.drop_confirm$lastSlot) {
      DropConfirm.INSTANCE.setConfirmed(false);
      this.drop_confirm$lastSlot = this.selected;
    }
  }
}
