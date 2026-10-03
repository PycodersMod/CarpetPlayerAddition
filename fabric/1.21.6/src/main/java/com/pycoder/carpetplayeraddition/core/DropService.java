package com.pycoder.carpetplayeraddition.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class DropService {
    public CommandResult dropSlot(ServerPlayer fakePlayer, SlotRef slot, DropAmount amount) {
        ItemStack source = slot.get();
        int count = amount.resolve(source.getCount());
        if (count == 0) {
            return CommandResult.success("槽位为空，没有丢弃物品");
        }

        ItemStack dropped = source.split(count);
        if (source.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        }
        slot.markChanged();
        fakePlayer.drop(dropped, false, false);
        SyncHelper.sync(fakePlayer);
        return CommandResult.success("已从 " + fakePlayer.getScoreboardName() + " 的 " + slot.label() + " 丢弃 " + count + " 个物品");
    }

    public CommandResult dropRange(ServerPlayer fakePlayer, List<SlotRef> slots) {
        int changedSlots = 0;
        int stacks = 0;
        for (SlotRef slot : slots) {
            ItemStack source = slot.get();
            if (source.isEmpty()) {
                continue;
            }
            ItemStack dropped = source.copy();
            slot.set(ItemStack.EMPTY);
            slot.markChanged();
            fakePlayer.drop(dropped, false, false);
            changedSlots++;
            stacks++;
        }
        SyncHelper.sync(fakePlayer);
        if (changedSlots == 0) {
            return CommandResult.success("没有可丢弃的物品");
        }
        return CommandResult.success("已丢弃 " + changedSlots + " 个槽位中的 " + stacks + " 组物品");
    }
}
