package com.pycoder.carpetplayeraddition.core;

public record DropAmount(Integer value, boolean allItems) {
    public static DropAmount one() {
        return new DropAmount(1, false);
    }

    public static DropAmount of(int value) {
        if (value < 1 || value > 64) {
            throw new IllegalArgumentException("数量必须是 1-64 或 all");
        }
        return new DropAmount(value, false);
    }

    public static DropAmount all() {
        return new DropAmount(null, true);
    }

    public int resolve(int currentCount) {
        if (currentCount <= 0) {
            return 0;
        }
        if (allItems) {
            return currentCount;
        }
        return Math.min(value, currentCount);
    }
}
