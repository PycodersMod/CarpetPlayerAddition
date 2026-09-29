package com.pycoder.carpetplayeraddition.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class TakeTransferService {
    public CommandResult takeSlot(ServerPlayer fakePlayer, ServerPlayer sourcePlayer, SlotRef slot, DropAmount amount) {
        ItemStack source = slot.get();
        int count = amount.resolve(source.getCount());
        if (count == 0) {
            return CommandResult.success("槽位为空，没有转移物品");
        }

        ItemStack transfer = source.split(count);
        insertOrDropAtFake(fakePlayer, transfer);
        if (source.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        }
        slot.markChanged();
        SyncHelper.sync(sourcePlayer);
        SyncHelper.sync(fakePlayer);
        return CommandResult.success("已将 " + count + " 个物品从 " + sourcePlayer.getScoreboardName() + " 的 " + slot.label() + " 转移给 " + fakePlayer.getScoreboardName());
    }

    public CommandResult takeRange(ServerPlayer fakePlayer, ServerPlayer sourcePlayer, List<SlotRef> slots) {
        int changedSlots = 0;
        int stacks = 0;
        for (SlotRef slot : slots) {
            ItemStack source = slot.get();
            if (source.isEmpty()) {
                continue;
            }
            ItemStack transfer = source.copy();
            slot.set(ItemStack.EMPTY);
            slot.markChanged();
            insertOrDropAtFake(fakePlayer, transfer);
            changedSlots++;
            stacks++;
        }
        SyncHelper.sync(sourcePlayer);
        SyncHelper.sync(fakePlayer);
        if (changedSlots == 0) {
            return CommandResult.success("没有可转移的物品");
        }
        return CommandResult.success("已将 " + changedSlots + " 个槽位中的 " + stacks + " 组物品从 " + sourcePlayer.getScoreboardName() + " 转移给 " + fakePlayer.getScoreboardName());
    }

    private static void insertOrDropAtFake(ServerPlayer fakePlayer, ItemStack stack) {
        fakePlayer.getInventory().add(stack);
        if (!stack.isEmpty()) {
            fakePlayer.drop(stack, false, false);
        }
    }
}
