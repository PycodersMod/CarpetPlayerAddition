package com.pycoder.carpetplayeraddition.core;

import net.minecraft.world.item.ItemStack;

public interface SlotRef {
    ItemStack get();

    void set(ItemStack stack);

    void markChanged();

    String label();
}
