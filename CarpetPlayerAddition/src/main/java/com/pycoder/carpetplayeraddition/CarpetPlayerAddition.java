package com.pycoder.carpetplayeraddition;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.pycoder.carpetplayeraddition.command.PlayerCommandExtensionRegistrar;
import com.pycoder.carpetplayeraddition.core.PickStateService;
import com.pycoder.carpetplayeraddition.core.MagnetStateService;
import com.pycoder.carpetplayeraddition.core.RestockStateService;
import com.pycoder.carpetplayeraddition.core.TrashcanStateService;
import com.pycoder.carpetplayeraddition.network.PickNetworking;

public final class CarpetPlayerAddition implements ModInitializer {
    public static final String MOD_ID = "carpet-player-addition";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    @Override
    public void onInitialize() {
        PickNetworking.registerCommon();
        PickStateService.register();
        RestockStateService.register();
        MagnetStateService.register();
        TrashcanStateService.register();
        PlayerCommandExtensionRegistrar.register();
        LOGGER.info("CarpetPlayerAddition registered /player command suffixes");
    }
}
