package top.local.carpetplayeraddition;

import net.fabricmc.api.ClientModInitializer;
import top.local.carpetplayeraddition.client.PickClientHandler;

public final class CarpetPlayerAdditionClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PickClientHandler.register();
    }
}
