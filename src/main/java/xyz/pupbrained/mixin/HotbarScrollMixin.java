package xyz.pupbrained.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.client.Mouse;
import xyz.pupbrained.Util;
import xyz.pupbrained.config.DropConfirmConfig;

@Mixin(Mouse.class)
public abstract class HotbarScrollMixin {
  @Inject(method = "onMouseScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerInventory;scrollInHotbar(D)V", shift = At.Shift.BEFORE))
  private void onHotbarScroll(CallbackInfo ci) {
    if (!DropConfirmConfig.GSON.instance().enabled)
      return;

    Util.confirmed = false;
  }
}
