package com.pycoder.carpetplayeraddition.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SlotMappingsTest {
    @Test
    void mapsHotbarNumbersToInternalSlots() {
        assertEquals(0, SlotMappings.hotbarToInternal(1));
        assertEquals(8, SlotMappings.hotbarToInternal(9));
    }

    @Test
    void mapsStorageNumbersToPlayerInventoryStorageSlots() {
        assertEquals(9, SlotMappings.storageToInternal(1));
        assertEquals(35, SlotMappings.storageToInternal(27));
    }

    @Test
    void mapsEnderChestNumbersToInternalSlots() {
        assertEquals(0, SlotMappings.enderChestToInternal(1));
        assertEquals(26, SlotMappings.enderChestToInternal(27));
    }

    @Test
    void rejectsOutOfRangeSlotNumbers() {
        assertThrows(IllegalArgumentException.class, () -> SlotMappings.hotbarToInternal(0));
        assertThrows(IllegalArgumentException.class, () -> SlotMappings.hotbarToInternal(10));
        assertThrows(IllegalArgumentException.class, () -> SlotMappings.storageToInternal(0));
        assertThrows(IllegalArgumentException.class, () -> SlotMappings.storageToInternal(28));
        assertThrows(IllegalArgumentException.class, () -> SlotMappings.enderChestToInternal(0));
        assertThrows(IllegalArgumentException.class, () -> SlotMappings.enderChestToInternal(28));
    }
}
