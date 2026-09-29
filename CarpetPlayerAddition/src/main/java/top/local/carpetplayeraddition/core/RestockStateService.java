package top.local.carpetplayeraddition.core;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class RestockStateService {
    private static final Map<UUID, RestockBinding> BINDINGS = new HashMap<>();
    private static final Map<PendingKey, PendingRestock> PENDING = new LinkedHashMap<>();

    private RestockStateService() {
    }

    public static void register() {
        UseItemCallback.EVENT.register((player, world, hand) -> {
            schedule(player, world, hand);
            return InteractionResult.PASS;
        });
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            schedule(player, world, hand);
            return InteractionResult.PASS;
        });
        ServerTickEvents.END_SERVER_TICK.register(RestockStateService::processPending);
    }

    public static CommandResult configure(ServerPlayer player, String fakeName, boolean enabled) {
        RestockBinding binding = BINDINGS.get(player.getUUID());
        if (!enabled) {
            if (binding == null || !binding.fakeName().equals(fakeName)) {
                return CommandResult.success("假人 " + fakeName + " 不是当前自动补货绑定目标，当前设置未改变");
            }
            BINDINGS.remove(player.getUUID());
            return CommandResult.success("已关闭与假人 " + fakeName + " 的右键自动补货绑定");
        }

        FakePlayerResolver.requireFake(player.getServer(), fakeName);
        BINDINGS.put(player.getUUID(), new RestockBinding(fakeName, enabled));
        return CommandResult.success("已将右键自动补货绑定到假人 " + fakeName + "，功能：" + (enabled ? "开启" : "关闭"));
    }

    private static void schedule(Player player, Level world, InteractionHand hand) {
        if (world.isClientSide() || !(player instanceof ServerPlayer serverPlayer)) {
            return;
        }
        RestockBinding binding = BINDINGS.get(serverPlayer.getUUID());
        if (binding == null || !binding.enabled()) {
            return;
        }
        ItemStack beforeUse = serverPlayer.getItemInHand(hand);
        if (beforeUse.isEmpty() || beforeUse.is(Items.AIR)) {
            return;
        }
        PENDING.put(new PendingKey(serverPlayer.getUUID(), hand), new PendingRestock(serverPlayer.getUUID(), hand, beforeUse.getItem(), serverPlayer.getInventory().getSelectedSlot(), 1));
    }

    private static void processPending(MinecraftServer server) {
        List<PendingRestock> pendingChecks = new ArrayList<>(PENDING.values());
        PENDING.clear();
        for (PendingRestock pending : pendingChecks) {
            if (pending.remainingTicks() > 0) {
                PENDING.put(new PendingKey(pending.playerId(), pending.hand()), pending.nextTick());
                continue;
            }
            ServerPlayer player = server.getPlayerList().getPlayer(pending.playerId());
            if (player != null) {
                restock(player, pending.hand(), pending.item(), pending.selectedHotbarSlot());
            }
        }
    }

    private static void restock(ServerPlayer player, InteractionHand hand, Item item, int selectedHotbarSlot) {
        RestockBinding binding = BINDINGS.get(player.getUUID());
        if (binding == null || !binding.enabled()) {
            return;
        }
        int halfStack = halfStack(item);
        LocalItemOrganizer.Result organized = LocalItemOrganizer.organize(player, item, selectedHotbarSlot);
        if (!organized.hasTarget()) {
            return;
        }
        if (organized.targetCount() >= halfStack) {
            SyncHelper.sync(player);
            return;
        }

        ServerPlayer fakePlayer;
        try {
            fakePlayer = FakePlayerResolver.requireFake(player.getServer(), binding.fakeName());
        } catch (IllegalArgumentException exception) {
            showActionbar(player, exception.getMessage());
            return;
        }

        int needed = halfStack - organized.targetCount();
        int moved = moveFromFakeToSlot(fakePlayer, organized.targetSlot(), item, needed);
        SyncHelper.sync(fakePlayer);
        SyncHelper.sync(player);
        if (moved < needed) {
            showActionbar(player, "假人 " + fakePlayer.getScoreboardName() + " 的 " + item.getName().getString() + " 不足，仅补充 " + moved + " 个");
            return;
        }
        showActionbar(player, "已从假人 " + fakePlayer.getScoreboardName() + " 自动补货 " + moved + " 个 " + item.getName().getString());
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

    private static boolean matches(ItemStack stack, Item item) {
        return !stack.isEmpty() && stack.is(item);
    }

    private static int halfStack(Item item) {
        return Math.max(1, new ItemStack(item).getMaxStackSize() / 2);
    }

    private static void showActionbar(ServerPlayer player, String message) {
        player.displayClientMessage(Component.literal(message), true);
    }

    private record RestockBinding(String fakeName, boolean enabled) {
    }

    private record PendingKey(UUID playerId, InteractionHand hand) {
    }

    private record PendingRestock(UUID playerId, InteractionHand hand, Item item, int selectedHotbarSlot, int remainingTicks) {
        private PendingRestock nextTick() {
            return new PendingRestock(playerId, hand, item, selectedHotbarSlot, remainingTicks - 1);
        }
    }
}
