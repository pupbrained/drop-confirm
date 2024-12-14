package com.example.mymod;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class MyModEntrypoint {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static void start() {
        LOGGER.info("Hello from MyMod!");
    }
}
