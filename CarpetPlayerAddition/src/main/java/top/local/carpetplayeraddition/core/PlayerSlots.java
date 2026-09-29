package top.local.carpetplayeraddition.core;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public final class PlayerSlots {
    private PlayerSlots() {
    }

    public static SlotRef hotbar(ServerPlayer player, int userSlot) {
        int internal = SlotMappings.hotbarToInternal(userSlot);
        return inventory(player, internal, "快捷栏 " + userSlot);
    }

    public static List<SlotRef> hotbarAll(ServerPlayer player) {
        List<SlotRef> slots = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            slots.add(hotbar(player, i));
        }
        return slots;
    }

    public static SlotRef storage(ServerPlayer player, int userSlot) {
        int internal = SlotMappings.storageToInternal(userSlot);
        return inventory(player, internal, "背包 " + userSlot);
    }

    public static SlotRef enderChest(ServerPlayer player, int userSlot) {
        int internal = SlotMappings.enderChestToInternal(userSlot);
        return new SlotRef() {
            @Override
            public ItemStack get() {
                return player.getEnderChestInventory().getItem(internal);
            }

            @Override
            public void set(ItemStack stack) {
                player.getEnderChestInventory().setItem(internal, stack);
            }

            @Override
            public void markChanged() {
                player.getEnderChestInventory().setChanged();
            }

            @Override
            public String label() {
                return "末影箱 " + userSlot;
            }
        };
    }

    public static SlotRef equipment(ServerPlayer player, EquipmentTarget target) {
        return new SlotRef() {
            @Override
            public ItemStack get() {
                return player.getItemBySlot(target.slot());
            }

            @Override
            public void set(ItemStack stack) {
                player.setItemSlot(target.slot(), stack);
            }

            @Override
            public void markChanged() {
                player.getInventory().setChanged();
            }

            @Override
            public String label() {
                return target.displayName();
            }
        };
    }

    public static List<SlotRef> inventoryAll(ServerPlayer player) {
        List<SlotRef> slots = new ArrayList<>();
        for (int i = 1; i <= 27; i++) {
            slots.add(storage(player, i));
        }
        slots.addAll(hotbarAll(player));
        for (EquipmentTarget target : EquipmentTarget.values()) {
            slots.add(equipment(player, target));
        }
        return slots;
    }

    public static List<SlotRef> enderChestAll(ServerPlayer player) {
        List<SlotRef> slots = new ArrayList<>();
        for (int i = 1; i <= 27; i++) {
            slots.add(enderChest(player, i));
        }
        return slots;
    }

    public static List<SlotRef> all(ServerPlayer player) {
        List<SlotRef> slots = inventoryAll(player);
        slots.addAll(enderChestAll(player));
        return slots;
    }

    private static SlotRef inventory(ServerPlayer player, int internalSlot, String label) {
        return new SlotRef() {
            @Override
            public ItemStack get() {
                return player.getInventory().getItem(internalSlot);
            }

            @Override
            public void set(ItemStack stack) {
                player.getInventory().setItem(internalSlot, stack);
            }

            @Override
            public void markChanged() {
                player.getInventory().setChanged();
            }

            @Override
            public String label() {
                return label;
            }
        };
    }
}
