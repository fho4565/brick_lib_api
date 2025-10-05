package com.arc_studio.brick_lib_api.mixin.common.server;

import com.arc_studio.brick_lib_api.config.ConfigTracker;
import com.arc_studio.brick_lib_api.config.ModConfig;
import com.arc_studio.brick_lib_api.Constants;
import net.minecraft.gametest.framework.GameTestServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(GameTestServer.class)
public class GameTestServerMixin {
    @Inject(method = "initServer",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerLevel;setWeatherParameters(IIZZ)V"))
    public void serverAboutToStart(CallbackInfoReturnable<Boolean> cir){
        Constants.installWorldVariables(getThis());
        ConfigTracker.loadConfigs(ModConfig.Type.SERVER, Constants.serverConfigFolder());
    }
    @Unique
    private GameTestServer getThis() {
        return (GameTestServer) (Object) this;
    }
}
