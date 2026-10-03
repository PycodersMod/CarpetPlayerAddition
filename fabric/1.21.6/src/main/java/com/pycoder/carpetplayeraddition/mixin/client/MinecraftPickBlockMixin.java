package com.pycoder.carpetplayeraddition.mixin.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import com.pycoder.carpetplayeraddition.client.PickClientHandler;

@Mixin(Minecraft.class)
public abstract class MinecraftPickBlockMixin {
    @Inject(method = "pickBlock", at = @At("HEAD"), cancellable = true)
    private void carpetPlayerAddition$pickBlock(CallbackInfo callbackInfo) {
        if (PickClientHandler.tryHandlePickBlock((Minecraft) (Object) this)) {
            callbackInfo.cancel();
        }
    }
}
