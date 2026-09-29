package com.pycoder.carpetplayeraddition.mixin;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.pycoder.carpetplayeraddition.core.MagnetStateService;

@Mixin(ItemEntity.class)
public abstract class ItemEntityPickupMixin {
    @Inject(method = "playerTouch", at = @At("HEAD"))
    private void carpetPlayerAddition$recordPickupAttempt(Player player, CallbackInfo ci) {
        ItemEntity itemEntity = (ItemEntity) (Object) this;
        if (itemEntity.level().isClientSide() || !(player instanceof ServerPlayer)) {
            return;
        }
        MagnetStateService.recordPickupAttempt((ServerPlayer) player, itemEntity.getItem());
    }
}
