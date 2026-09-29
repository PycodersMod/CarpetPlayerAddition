package top.local.carpetplayeraddition.core;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DropAmountTest {
    @Test
    void defaultsToOneItem() {
        assertEquals(1, DropAmount.one().resolve(64));
        assertEquals(0, DropAmount.one().resolve(0));
    }

    @Test
    void clampsRequestedAmountToCurrentStackCount() {
        assertEquals(3, DropAmount.of(3).resolve(10));
        assertEquals(2, DropAmount.of(64).resolve(2));
    }

    @Test
    void allResolvesToCurrentStackCount() {
        assertEquals(64, DropAmount.all().resolve(64));
        assertEquals(7, DropAmount.all().resolve(7));
        assertEquals(0, DropAmount.all().resolve(0));
    }

    @Test
    void rejectsInvalidExplicitAmounts() {
        assertThrows(IllegalArgumentException.class, () -> DropAmount.of(0));
        assertThrows(IllegalArgumentException.class, () -> DropAmount.of(65));
    }
}
