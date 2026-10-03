package com.pycoder.carpetplayeraddition.core;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class PickStateService {
    private static final Map<UUID, PickBinding> BINDINGS = new HashMap<>();
    private static final PickTransferService PICK_TRANSFER_SERVICE = new PickTransferService();

    private PickStateService() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(PickStateService::syncGameModeDefaults);
    }

    public static CommandResult configure(ServerPlayer player, String fakeName, boolean enabled) {
        PickBinding binding = BINDINGS.get(player.getUUID());
        if (!enabled) {
            if (binding == null || !binding.fakeName().equals(fakeName)) {
                return CommandResult.success("假人 " + fakeName + " 不是当前中键取物绑定目标，当前设置未改变");
            }
            BINDINGS.remove(player.getUUID());
            return CommandResult.success("已关闭与假人 " + fakeName + " 的中键取物绑定");
        }

        FakePlayerResolver.requireFake(player.getServer(), fakeName);
        BINDINGS.put(player.getUUID(), new PickBinding(fakeName, enabled, player.gameMode.getGameModeForPlayer()));
        return CommandResult.success("已将中键取物绑定到假人 " + fakeName + "，功能：" + (enabled ? "开启" : "关闭"));
    }

    public static void handlePickRequest(ServerPlayer player, ResourceLocation itemId) {
        PickBinding binding = BINDINGS.get(player.getUUID());
        if (binding == null || !binding.enabled()) {
            return;
        }

        Item item = BuiltInRegistries.ITEM.getValue(itemId);
        if (item == Items.AIR) {
            showActionbar(player, "无法识别目标方块对应物品");
            return;
        }

        ServerPlayer fakePlayer;
        try {
            fakePlayer = FakePlayerResolver.requireFake(player.getServer(), binding.fakeName());
        } catch (IllegalArgumentException exception) {
            showActionbar(player, exception.getMessage());
            return;
        }

        CommandResult result = PICK_TRANSFER_SERVICE.pick(fakePlayer, player, item);
        showActionbar(player, result.message());
    }

    private static void syncGameModeDefaults(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PickBinding binding = BINDINGS.get(player.getUUID());
            if (binding == null) {
                continue;
            }
            GameType current = player.gameMode.getGameModeForPlayer();
            if (current == binding.lastGameType()) {
                continue;
            }
            if (current == GameType.CREATIVE) {
                BINDINGS.put(player.getUUID(), binding.withMode(false, current));
                showActionbar(player, "已切换创造模式，中键取物自动关闭");
                continue;
            }
            if (current == GameType.SURVIVAL) {
                BINDINGS.put(player.getUUID(), binding.withMode(true, current));
                showActionbar(player, "已切换生存模式，中键取物自动开启");
                continue;
            }
            BINDINGS.put(player.getUUID(), binding.withMode(binding.enabled(), current));
        }
    }

    private static void showActionbar(ServerPlayer player, String message) {
        player.displayClientMessage(Component.literal(message), true);
    }

    private record PickBinding(String fakeName, boolean enabled, GameType lastGameType) {
        private PickBinding withMode(boolean enabled, GameType mode) {
            return new PickBinding(fakeName, enabled, mode);
        }
    }
}
