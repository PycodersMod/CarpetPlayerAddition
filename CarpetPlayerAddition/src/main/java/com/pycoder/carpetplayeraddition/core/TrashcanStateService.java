package com.pycoder.carpetplayeraddition.core;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class TrashcanStateService {
    private static final DropService DROP_SERVICE = new DropService();
    private static final Map<UUID, TrashcanBinding> BINDINGS = new HashMap<>();

    private TrashcanStateService() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(TrashcanStateService::processTrashcans);
    }

    public static CommandResult configure(ServerPlayer fakePlayer, boolean enabled) {
        if (enabled) {
            BINDINGS.put(fakePlayer.getUUID(), new TrashcanBinding(fakePlayer.getScoreboardName()));
            return CommandResult.success("已将假人 " + fakePlayer.getScoreboardName() + " 设置为远程垃圾桶模式");
        }
        BINDINGS.remove(fakePlayer.getUUID());
        return CommandResult.success("已关闭假人 " + fakePlayer.getScoreboardName() + " 的远程垃圾桶模式");
    }

    private static void processTrashcans(net.minecraft.server.MinecraftServer server) {
        if (BINDINGS.isEmpty()) {
            return;
        }
        List<UUID> missing = new ArrayList<>();
        for (Map.Entry<UUID, TrashcanBinding> entry : BINDINGS.entrySet()) {
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player == null) {
                missing.add(entry.getKey());
                continue;
            }
            ServerPlayer fake;
            try {
                fake = FakePlayerResolver.requireFake(server, player.getScoreboardName());
            } catch (IllegalArgumentException exception) {
                missing.add(entry.getKey());
                continue;
            }
            List<SlotRef> slots = PlayerSlots.all(fake);
            if (hasAnyItem(slots)) {
                DROP_SERVICE.dropRange(fake, slots);
            }
        }
        for (UUID id : missing) {
            BINDINGS.remove(id);
        }
    }

    private static boolean hasAnyItem(List<SlotRef> slots) {
        for (SlotRef slot : slots) {
            ItemStack stack = slot.get();
            if (!stack.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    private record TrashcanBinding(String fakeName) {
    }
}
