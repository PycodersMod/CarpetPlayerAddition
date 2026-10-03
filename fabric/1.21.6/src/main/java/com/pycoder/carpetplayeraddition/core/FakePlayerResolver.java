package com.pycoder.carpetplayeraddition.core;

import carpet.patches.EntityPlayerMPFake;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

public final class FakePlayerResolver {
    private FakePlayerResolver() {
    }

    public static ServerPlayer requireFake(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "player");
        return requireFake(context.getSource().getServer(), name);
    }

    public static ServerPlayer requireFake(MinecraftServer server, String name) {
        ServerPlayer player = server.getPlayerList().getPlayerByName(name);
        if (!(player instanceof EntityPlayerMPFake)) {
            throw new IllegalArgumentException("目标 " + name + " 不是在线 Carpet 假人");
        }
        return player;
    }
}
