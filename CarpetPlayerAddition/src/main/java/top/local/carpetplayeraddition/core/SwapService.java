package top.local.carpetplayeraddition.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

public final class SwapService {
    public CommandResult swap(ServerPlayer fakePlayer, SlotRef first, SlotRef second) {
        ItemStack firstStack = first.get().copy();
        ItemStack secondStack = second.get().copy();

        first.set(secondStack);
        second.set(firstStack);
        first.markChanged();
        second.markChanged();
        SyncHelper.sync(fakePlayer);

        if (firstStack.isEmpty() && secondStack.isEmpty()) {
            return CommandResult.success("两个槽位都是空的，没有交换物品");
        }
        return CommandResult.success("已交换 " + first.label() + " 与 " + second.label());
    }
}
