package top.local.carpetplayeraddition.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public final class GiveTransferService {
    public CommandResult giveSlot(ServerPlayer fakePlayer, ServerPlayer targetPlayer, SlotRef slot, DropAmount amount) {
        ItemStack source = slot.get();
        int count = amount.resolve(source.getCount());
        if (count == 0) {
            return CommandResult.success("槽位为空，没有转移物品");
        }

        ItemStack transfer = source.split(count);
        insertOrDrop(targetPlayer, transfer);
        if (source.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        }
        slot.markChanged();
        SyncHelper.sync(fakePlayer);
        SyncHelper.sync(targetPlayer);
        return CommandResult.success("已将 " + count + " 个物品从 " + fakePlayer.getScoreboardName() + " 的 " + slot.label() + " 转移给 " + targetPlayer.getScoreboardName());
    }

    public CommandResult giveRange(ServerPlayer fakePlayer, ServerPlayer targetPlayer, List<SlotRef> slots) {
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
            insertOrDrop(targetPlayer, transfer);
            changedSlots++;
            stacks++;
        }
        SyncHelper.sync(fakePlayer);
        SyncHelper.sync(targetPlayer);
        if (changedSlots == 0) {
            return CommandResult.success("没有可转移的物品");
        }
        return CommandResult.success("已将 " + changedSlots + " 个槽位中的 " + stacks + " 组物品转移给 " + targetPlayer.getScoreboardName());
    }

    private static void insertOrDrop(ServerPlayer targetPlayer, ItemStack stack) {
        targetPlayer.getInventory().add(stack);
        if (!stack.isEmpty()) {
            targetPlayer.drop(stack, false, false);
        }
    }
}
