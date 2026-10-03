package com.pycoder.carpetplayeraddition;

import net.fabricmc.api.ClientModInitializer;
import com.pycoder.carpetplayeraddition.client.PickClientHandler;

public final class CarpetPlayerAdditionClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        PickClientHandler.register();
    }
}
