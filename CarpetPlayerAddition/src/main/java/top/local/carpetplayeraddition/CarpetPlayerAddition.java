package top.local.carpetplayeraddition;

import net.fabricmc.api.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.local.carpetplayeraddition.command.PlayerCommandExtensionRegistrar;
import top.local.carpetplayeraddition.core.PickStateService;
import top.local.carpetplayeraddition.core.MagnetStateService;
import top.local.carpetplayeraddition.core.RestockStateService;
import top.local.carpetplayeraddition.core.TrashcanStateService;
import top.local.carpetplayeraddition.network.PickNetworking;

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
