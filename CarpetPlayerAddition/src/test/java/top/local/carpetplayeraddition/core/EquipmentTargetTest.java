package top.local.carpetplayeraddition.core;

import net.minecraft.world.entity.EquipmentSlot;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class EquipmentTargetTest {
    @Test
    void resolvesShortPublicLiterals() {
        assertEquals(EquipmentSlot.OFFHAND, EquipmentTarget.byLiteral("offhand").slot());
        assertEquals(EquipmentSlot.HEAD, EquipmentTarget.byLiteral("head").slot());
        assertEquals(EquipmentSlot.CHEST, EquipmentTarget.byLiteral("chest").slot());
        assertEquals(EquipmentSlot.LEGS, EquipmentTarget.byLiteral("legs").slot());
        assertEquals(EquipmentSlot.FEET, EquipmentTarget.byLiteral("feet").slot());
    }

    @Test
    void rejectsRemovedLongArmorLiterals() {
        assertThrows(IllegalArgumentException.class, () -> EquipmentTarget.byLiteral("helmet"));
        assertThrows(IllegalArgumentException.class, () -> EquipmentTarget.byLiteral("chestplate"));
        assertThrows(IllegalArgumentException.class, () -> EquipmentTarget.byLiteral("leggings"));
        assertThrows(IllegalArgumentException.class, () -> EquipmentTarget.byLiteral("boots"));
    }
}
