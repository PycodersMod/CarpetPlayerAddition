package com.pycoder.carpetplayeraddition.core;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class TargetPlayerResolver {
    private TargetPlayerResolver() {
    }

    public static ServerPlayer requireOnline(MinecraftServer server, String name) {
        ServerPlayer player = server.getPlayerList().getPlayerByName(name);
        if (player == null) {
            throw new IllegalArgumentException("目标玩家 " + name + " 不在线");
        }
        return player;
    }
}
