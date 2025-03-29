package xyz.pupbrained.drop_confirm

import com.terraformersmc.modmenu.api.ConfigScreenFactory
import com.terraformersmc.modmenu.api.ModMenuApi

class ModMenuIntegration : ModMenuApi {
  override fun getModConfigScreenFactory(): ConfigScreenFactory<*> =
    ConfigScreenFactory { DropConfirmConfig.createScreen(it) }
}
