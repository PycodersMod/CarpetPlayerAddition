package com.pycoder.carpetplayeraddition.core;

import net.minecraft.world.entity.EquipmentSlot;

import java.util.Arrays;

public enum EquipmentTarget {
    OFFHAND("offhand", EquipmentSlot.OFFHAND, "副手"),
    HEAD("head", EquipmentSlot.HEAD, "头部"),
    CHEST("chest", EquipmentSlot.CHEST, "胸部"),
    LEGS("legs", EquipmentSlot.LEGS, "腿部"),
    FEET("feet", EquipmentSlot.FEET, "脚部");

    private final String literal;
    private final EquipmentSlot slot;
    private final String displayName;

    EquipmentTarget(String literal, EquipmentSlot slot, String displayName) {
        this.literal = literal;
        this.slot = slot;
        this.displayName = displayName;
    }

    public String literal() {
        return literal;
    }

    public EquipmentSlot slot() {
        return slot;
    }

    public String displayName() {
        return displayName;
    }

    public static EquipmentTarget byLiteral(String literal) {
        return Arrays.stream(values())
                .filter(target -> target.literal.equals(literal))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未知装备槽位: " + literal));
    }
}
