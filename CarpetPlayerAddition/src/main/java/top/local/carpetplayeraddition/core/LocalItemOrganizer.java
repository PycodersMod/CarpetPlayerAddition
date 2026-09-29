package top.local.carpetplayeraddition.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class LocalItemOrganizer {
    private LocalItemOrganizer() {
    }

    public static Result organize(ServerPlayer player, Item item, int preferredHotbarSlot) {
        SlotRef preferred = PlayerSlots.hotbar(player, preferredHotbarSlot + 1);
        boolean changed = false;
        SlotRef target;
        SlotRef matchingOtherHotbar = firstMatchingOtherHotbarSlot(player, item, preferredHotbarSlot, preferred.label());
        if (!matches(preferred.get(), item) && matchingOtherHotbar != null) {
            swap(preferred, matchingOtherHotbar);
            changed = true;
            target = preferred;
        } else {
            target = chooseTarget(player, item, preferredHotbarSlot, preferred);
        }
        if (target == null) {
            return new Result(null, false, 0);
        }

        for (SlotRef source : localItemSlots(player, preferredHotbarSlot)) {
            if (source.label().equals(target.label())) {
                continue;
            }
            ItemStack stack = source.get();
            if (!matches(stack, item)) {
                continue;
            }
            int before = stack.getCount();
            insertIntoSlot(target, stack);
            if (stack.getCount() != before) {
                if (stack.isEmpty()) {
                    source.set(ItemStack.EMPTY);
                }
                source.markChanged();
                changed = true;
            }
        }
        return new Result(target, changed, target.get().getCount());
    }

    private static SlotRef chooseTarget(ServerPlayer player, Item item, int preferredHotbarSlot, SlotRef preferred) {
        if (matches(preferred.get(), item)) {
            return preferred;
        }
        SlotRef matchingHotbar = firstMatchingHotbarSlot(player, item, preferredHotbarSlot);
        if (matchingHotbar != null) {
            return matchingHotbar;
        }
        SlotRef offhand = PlayerSlots.equipment(player, EquipmentTarget.OFFHAND);
        if (matches(offhand.get(), item)) {
            return offhand;
        }
        if (preferred.get().isEmpty()) {
            return preferred;
        }
        for (SlotRef slot : hotbarSlots(player, preferredHotbarSlot)) {
            if (slot.get().isEmpty()) {
                return slot;
            }
        }
        if (offhand.get().isEmpty()) {
            return offhand;
        }
        return null;
    }

    private static SlotRef firstMatchingOtherHotbarSlot(ServerPlayer player, Item item, int preferredHotbarSlot, String preferredLabel) {
        for (SlotRef slot : hotbarSlots(player, preferredHotbarSlot)) {
            if (!slot.label().equals(preferredLabel) && matches(slot.get(), item)) {
                return slot;
            }
        }
        return null;
    }

    private static SlotRef firstMatchingHotbarSlot(ServerPlayer player, Item item, int preferredHotbarSlot) {
        for (SlotRef slot : hotbarSlots(player, preferredHotbarSlot)) {
            if (matches(slot.get(), item)) {
                return slot;
            }
        }
        return null;
    }

    private static List<SlotRef> localItemSlots(ServerPlayer player, int preferredHotbarSlot) {
        List<SlotRef> slots = new ArrayList<>();
        slots.addAll(hotbarSlots(player, preferredHotbarSlot));
        for (int i = 1; i <= 27; i++) {
            slots.add(PlayerSlots.storage(player, i));
        }
        slots.add(PlayerSlots.equipment(player, EquipmentTarget.OFFHAND));
        return slots;
    }

    private static List<SlotRef> hotbarSlots(ServerPlayer player, int preferredHotbarSlot) {
        List<SlotRef> slots = new ArrayList<>();
        for (int offset = 0; offset < 9; offset++) {
            int internal = (preferredHotbarSlot + offset) % 9;
            slots.add(PlayerSlots.hotbar(player, internal + 1));
        }
        return slots;
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

    private static void swap(SlotRef first, SlotRef second) {
        ItemStack firstStack = first.get().copy();
        ItemStack secondStack = second.get().copy();
        first.set(secondStack);
        second.set(firstStack);
        first.markChanged();
        second.markChanged();
    }

    private static boolean matches(ItemStack stack, Item item) {
        return !stack.isEmpty() && stack.is(item);
    }

    public record Result(SlotRef targetSlot, boolean changed, int targetCount) {
        public boolean hasTarget() {
            return targetSlot != null;
        }
    }
}
