package top.local.carpetplayeraddition.client;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import top.local.carpetplayeraddition.network.PickBlockPayload;

public final class PickClientHandler {
    private PickClientHandler() {
    }

    public static void register() {
    }

    public static boolean tryHandlePickBlock(Minecraft client) {
        if (client.player == null || client.level == null || client.gameMode == null || !ClientPlayNetworking.canSend(PickBlockPayload.TYPE)) {
            return false;
        }
        GameType mode = client.gameMode.getPlayerMode();
        if (mode.isCreative()) {
            return false;
        }
        return sendLookedBlock(client);
    }

    private static boolean sendLookedBlock(Minecraft client) {
        if (!(client.hitResult instanceof BlockHitResult blockHitResult) || client.hitResult.getType() != HitResult.Type.BLOCK) {
            return false;
        }
        BlockState state = client.level.getBlockState(blockHitResult.getBlockPos());
        Item item = state.getBlock().asItem();
        if (item == Items.AIR) {
            return false;
        }
        ClientPlayNetworking.send(new PickBlockPayload(BuiltInRegistries.ITEM.getKey(item)));
        return true;
    }
}
