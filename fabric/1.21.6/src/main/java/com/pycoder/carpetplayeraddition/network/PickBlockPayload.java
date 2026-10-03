package com.pycoder.carpetplayeraddition.network;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import com.pycoder.carpetplayeraddition.CarpetPlayerAddition;

public record PickBlockPayload(ResourceLocation itemId) implements CustomPacketPayload {
    public static final Type<PickBlockPayload> TYPE = new Type<>(ResourceLocation.fromNamespaceAndPath(CarpetPlayerAddition.MOD_ID, "pick_block"));
    public static final StreamCodec<RegistryFriendlyByteBuf, PickBlockPayload> CODEC = StreamCodec.composite(
            ResourceLocation.STREAM_CODEC.cast(),
            PickBlockPayload::itemId,
            PickBlockPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
