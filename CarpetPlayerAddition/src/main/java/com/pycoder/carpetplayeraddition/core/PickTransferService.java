package com.pycoder.carpetplayeraddition.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class PickTransferService {
    public CommandResult pick(ServerPlayer fakePlayer, ServerPlayer targetPlayer, Item item) {
        int halfStack = Math.max(1, new ItemStack(item).getMaxStackSize() / 2);
        LocalItemOrganizer.Result organized = LocalItemOrganizer.organize(targetPlayer, item, targetPlayer.getInventory().getSelectedSlot());
        if (!organized.hasTarget()) {
            return CommandResult.success("玩家背包已满且没有对应物品，未从假人取物");
        }
        if (organized.targetCount() >= halfStack) {
            SyncHelper.sync(targetPlayer);
            return CommandResult.success("已将玩家身上的 " + item.getName().getString() + " 规整到 " + organized.targetSlot().label());
        }

        int available = countAvailable(PlayerSlots.all(fakePlayer), item);
        if (available == 0) {
            return CommandResult.success("假人 " + fakePlayer.getScoreboardName() + " 没有对应物品");
        }

        int needed = halfStack - organized.targetCount();
        int moved = moveFromFakeToSlot(fakePlayer, organized.targetSlot(), item, Math.min(available, needed));
        SyncHelper.sync(fakePlayer);
        SyncHelper.sync(targetPlayer);
        if (moved < needed) {
            return CommandResult.success("已规整玩家物品，并从假人 " + fakePlayer.getScoreboardName() + " 补充 " + moved + " 个 " + item.getName().getString() + "，假人物品不足");
        }
        return CommandResult.success("已规整玩家物品，并从假人 " + fakePlayer.getScoreboardName() + " 补充 " + moved + " 个 " + item.getName().getString());
    }

    private static int countAvailable(List<SlotRef> slots, Item item) {
        int count = 0;
        for (SlotRef slot : slots) {
            ItemStack stack = slot.get();
            if (matches(stack, item)) {
                count += stack.getCount();
            }
        }
        return count;
    }

    private static boolean matches(ItemStack stack, Item item) {
        return !stack.isEmpty() && stack.is(item);
    }

    private static int moveFromFakeToSlot(ServerPlayer fakePlayer, SlotRef targetSlot, Item item, int requested) {
        int remaining = requested;
        int moved = 0;
        for (SlotRef sourceSlot : PlayerSlots.all(fakePlayer)) {
            if (remaining == 0) {
                break;
            }
            ItemStack source = sourceSlot.get();
            if (!matches(source, item)) {
                continue;
            }
            int count = Math.min(remaining, source.getCount());
            ItemStack transfer = source.split(count);
            insertIntoSlot(targetSlot, transfer);
            int accepted = count - transfer.getCount();
            moved += accepted;
            remaining -= accepted;
            if (!transfer.isEmpty()) {
                source.grow(transfer.getCount());
                sourceSlot.markChanged();
                break;
            }
            if (source.isEmpty()) {
                sourceSlot.set(ItemStack.EMPTY);
            }
            sourceSlot.markChanged();
        }
        return moved;
    }

    private static void insertIntoSlot(SlotRef slot, ItemStack stack) {
        ItemStack existing = slot.get();
        if (existing.isEmpty()) {
            slot.set(stack.copyAndClear());
            slot.markChanged();
            return;
        }
        if (!ItemStack.isSameItemSameComponents(existing, stack) || existing.getCount() >= existing.getMaxStackSize()) {
            return;
        }
        int moved = Math.min(stack.getCount(), existing.getMaxStackSize() - existing.getCount());
        existing.grow(moved);
        stack.shrink(moved);
        slot.markChanged();
    }
}
