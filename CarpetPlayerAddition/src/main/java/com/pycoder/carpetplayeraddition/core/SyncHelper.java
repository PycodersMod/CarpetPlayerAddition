package com.pycoder.carpetplayeraddition.core;

import net.minecraft.server.level.ServerPlayer;

public final class SyncHelper {
    private SyncHelper() {
    }

    public static void sync(ServerPlayer player) {
        player.getInventory().setChanged();
        player.getEnderChestInventory().setChanged();
        player.containerMenu.broadcastChanges();
        player.inventoryMenu.broadcastChanges();
    }
}
