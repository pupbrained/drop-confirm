package com.example.mymod;

import com.mojang.logging.LogUtils;
import org.slf4j.Logger;

public class ExampleMod {
    public static final Logger LOGGER = LogUtils.getLogger();

    public static void initialize() {
        LOGGER.info("Hello from MyMod!");
    }
}
