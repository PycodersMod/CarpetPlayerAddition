package com.pycoder.carpetplayeraddition.core;

public final class SlotMappings {
    public static final int HOTBAR_MIN = 1;
    public static final int HOTBAR_MAX = 9;
    public static final int STORAGE_MIN = 1;
    public static final int STORAGE_MAX = 27;

    private SlotMappings() {
    }

    public static int hotbarToInternal(int userSlot) {
        requireRange(userSlot, HOTBAR_MIN, HOTBAR_MAX, "快捷栏编号必须是 1-9");
        return userSlot - 1;
    }

    public static int storageToInternal(int userSlot) {
        requireRange(userSlot, STORAGE_MIN, STORAGE_MAX, "背包编号必须是 1-27");
        return userSlot + 8;
    }

    public static int enderChestToInternal(int userSlot) {
        requireRange(userSlot, STORAGE_MIN, STORAGE_MAX, "末影箱编号必须是 1-27");
        return userSlot - 1;
    }

    private static void requireRange(int value, int min, int max, String message) {
        if (value < min || value > max) {
            throw new IllegalArgumentException(message);
        }
    }
}
