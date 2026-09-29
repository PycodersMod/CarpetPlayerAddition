package com.pycoder.carpetplayeraddition.core;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class MagnetStateService {
    private static final Map<UUID, MagnetBinding> BINDINGS = new HashMap<>();
    private static final Map<UUID, List<PendingPickup>> PENDING_PICKUPS = new HashMap<>();

    private MagnetStateService() {
    }

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(MagnetStateService::processPendingPickups);
    }

    public static CommandResult configure(ServerPlayer player, String fakeName, boolean enabled) {
        MagnetBinding binding = BINDINGS.get(player.getUUID());
        if (!enabled) {
            if (binding == null || !binding.fakeName().equals(fakeName)) {
                return CommandResult.success("假人 " + fakeName + " 不是当前磁力传输绑定目标，当前设置未改变");
            }
            BINDINGS.remove(player.getUUID());
            return CommandResult.success("已关闭与假人 " + fakeName + " 的磁力传输绑定");
        }

        FakePlayerResolver.requireFake(player.getServer(), fakeName);
        BINDINGS.put(player.getUUID(), new MagnetBinding(fakeName));
        return CommandResult.success("已将磁力传输绑定到假人 " + fakeName + "，功能：开启");
    }

    public static void recordPickupAttempt(ServerPlayer player, ItemStack itemEntityStack) {
        if (itemEntityStack.isEmpty() || !BINDINGS.containsKey(player.getUUID())) {
            return;
        }
        ItemStack stack = itemEntityStack.copy();
        int beforeCount = countMatching(player, stack);
        PENDING_PICKUPS.compute(player.getUUID(), (uuid, pickups) -> mergePickup(pickups, stack, beforeCount));
    }

    private static List<PendingPickup> mergePickup(List<PendingPickup> pickups, ItemStack stack, int beforeCount) {
        List<PendingPickup> result = pickups == null ? new ArrayList<>() : pickups;
        for (int i = 0; i < result.size(); i++) {
            PendingPickup pickup = result.get(i);
            if (ItemStack.isSameItemSameComponents(pickup.stack(), stack)) {
                ItemStack merged = pickup.stack().copy();
                merged.grow(stack.getCount());
                result.set(i, new PendingPickup(merged, Math.min(pickup.beforeCount(), beforeCount)));
                return result;
            }
        }
        result.add(new PendingPickup(stack, beforeCount));
        return result;
    }

    private static void processPendingPickups(MinecraftServer server) {
        if (PENDING_PICKUPS.isEmpty()) {
            return;
        }
        Iterator<Map.Entry<UUID, List<PendingPickup>>> iterator = PENDING_PICKUPS.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<UUID, List<PendingPickup>> entry = iterator.next();
            ServerPlayer player = server.getPlayerList().getPlayer(entry.getKey());
            if (player != null) {
                for (PendingPickup pickup : entry.getValue()) {
                    transferPickedDelta(player, pickup);
                }
            }
            iterator.remove();
        }
    }

    private static void transferPickedDelta(ServerPlayer player, PendingPickup pickup) {
        int currentCount = countMatching(player, pickup.stack());
        int pickedCount = Math.min(pickup.stack().getCount(), Math.max(0, currentCount - pickup.beforeCount()));
        if (pickedCount <= 0) {
            return;
        }

        MagnetBinding binding = BINDINGS.get(player.getUUID());
        if (binding == null) {
            return;
        }
        ServerPlayer fakePlayer;
        try {
            fakePlayer = FakePlayerResolver.requireFake(player.getServer(), binding.fakeName());
        } catch (IllegalArgumentException exception) {
            showActionbar(player, exception.getMessage());
            return;
        }

        ItemStack transfer = pickup.stack().copy();
        transfer.setCount(pickedCount);
        int removed = removePickedStack(player, transfer);
        if (removed == 0) {
            return;
        }
        transfer.setCount(removed);
        insertOrDropAtFake(fakePlayer, transfer);
        SyncHelper.sync(player);
        SyncHelper.sync(fakePlayer);
        showActionbar(player, "已将刚捡起的 " + removed + " 个 " + pickup.stack().getHoverName().getString() + " 传递给假人 " + fakePlayer.getScoreboardName());
    }

    private static int countMatching(ServerPlayer player, ItemStack pickedStack) {
        int count = 0;
        for (int slot = 0; slot < 36; slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (ItemStack.isSameItemSameComponents(stack, pickedStack)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static int removePickedStack(ServerPlayer player, ItemStack pickedStack) {
        int remaining = pickedStack.getCount();
        int removed = 0;
        for (int slot = 0; slot < 36 && remaining > 0; slot++) {
            ItemStack stack = player.getInventory().getItem(slot);
            if (!ItemStack.isSameItemSameComponents(stack, pickedStack)) {
                continue;
            }
            int count = Math.min(remaining, stack.getCount());
            stack.shrink(count);
            remaining -= count;
            removed += count;
            if (stack.isEmpty()) {
                player.getInventory().setItem(slot, ItemStack.EMPTY);
            }
        }
        if (removed > 0) {
            player.getInventory().setChanged();
        }
        return removed;
    }

    private static void insertOrDropAtFake(ServerPlayer fakePlayer, ItemStack stack) {
        fakePlayer.getInventory().add(stack);
        if (!stack.isEmpty()) {
            fakePlayer.drop(stack, false, false);
        }
    }

    private static void showActionbar(ServerPlayer player, String message) {
        player.displayClientMessage(Component.literal(message), true);
    }

    private record MagnetBinding(String fakeName) {
    }

    private record PendingPickup(ItemStack stack, int beforeCount) {
    }
}
