//? if fabric {
package com.example.mymod.loaders.fabric;

import com.example.mymod.ExampleMod;
import com.mojang.logging.LogUtils;
import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;

public class FabricEntrypoint implements ModInitializer {
    private static final Logger LOGGER = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        LOGGER.info("Hello from FabricEntrypoint!");
        ExampleMod.initialize();
    }
}
//?}
