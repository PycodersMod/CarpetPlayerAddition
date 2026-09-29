package top.local.carpetplayeraddition.network;

import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import top.local.carpetplayeraddition.core.PickStateService;

public final class PickNetworking {
    private PickNetworking() {
    }

    public static void registerCommon() {
        PayloadTypeRegistry.playC2S().register(PickBlockPayload.TYPE, PickBlockPayload.CODEC);
        ServerPlayNetworking.registerGlobalReceiver(PickBlockPayload.TYPE, (payload, context) ->
                context.server().execute(() -> PickStateService.handlePickRequest(context.player(), payload.itemId())));
    }
}
