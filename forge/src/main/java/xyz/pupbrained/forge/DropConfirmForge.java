package xyz.pupbrained.forge;

import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import xyz.pupbrained.DropConfirm;
import xyz.pupbrained.config.DropConfirmConfig;

@Mod(DropConfirm.MOD_ID)
public class DropConfirmForge {
  public DropConfirmForge() {
    ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () ->
      new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> DropConfirmConfig.createScreen(parent)));
    DropConfirm.init();
  }
}
