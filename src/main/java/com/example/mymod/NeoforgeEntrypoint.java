//? if neoforge {
package com.example.mymod;

import com.mojang.logging.LogUtils;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

@Mod("example_mod")
public class NeoforgeEntrypoint {
    private static final Logger LOGGER = LogUtils.getLogger();

    public NeoforgeEntrypoint() {
        LOGGER.info("Hello from NeoforgeEntrypoint!");
        MyModEntrypoint.start();
    }
}
//?}
