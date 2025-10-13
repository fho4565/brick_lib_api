package com.arc_studio.brick_lib_api.mixin.common.server;

import com.arc_studio.brick_lib_api.config.ConfigTracker;
import com.arc_studio.brick_lib_api.config.ModConfig;
import com.arc_studio.brick_lib_api.Constants;
import net.minecraft.client.server.IntegratedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(IntegratedServer.class)
public class IntegratedServerMixin {
    @Inject(method = "initServer",at = @At(value = "INVOKE", target = "Lnet/minecraft/client/server/IntegratedServer;loadLevel()V", shift = At.Shift.AFTER))
    public void serverAboutToStart(CallbackInfoReturnable<Boolean> cir){
        ConfigTracker.loadConfigs(ModConfig.Type.SERVER, Constants.serverConfigFolder());
    }

    @Unique
    private IntegratedServer getThis() {
        return (IntegratedServer) (Object) this;
    }
}
