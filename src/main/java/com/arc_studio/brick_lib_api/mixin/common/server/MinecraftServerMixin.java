package com.arc_studio.brick_lib_api.mixin.common.server;

import com.arc_studio.brick_lib_api.config.ConfigTracker;
import com.arc_studio.brick_lib_api.config.ModConfig;
import com.arc_studio.brick_lib_api.Constants;
import com.arc_studio.brick_lib_api.core.register.BrickRegistry;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author fho4565
 */
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;onServerExit()V"))
    public void onServerStopped(CallbackInfo ci) {
        ConfigTracker.unloadConfigs(ModConfig.Type.SERVER, Constants.serverConfigFolder());
        Constants.uninstallWorldVariables();
    }

    @Inject(method = "loadLevel", at = @At("HEAD"))
    public void load(CallbackInfo ci) {
        Constants.installWorldVariables(getThis());
    }

    //? if <= 1.19.2 {
    /*@Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;updateStatusIcon(Lnet/minecraft/network/protocol/status/ServerStatus;)V"))
    *///?} else {
    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;buildServerStatus()Lnet/minecraft/network/protocol/status/ServerStatus;"))
    //?}
    public void Inject74(CallbackInfo ci) {
        if(BrickRegistry.TO_CLEAN_BRICK_REGISTRIES != null){
            for (BrickRegistry<?> registry : BrickRegistry.TO_CLEAN_BRICK_REGISTRIES) {
                registry.onClean();
                registry.clean();
            }
            BrickRegistry.TO_CLEAN_BRICK_REGISTRIES = null;
        }
    }
    @Unique
    private MinecraftServer getThis(){
        return (MinecraftServer) (Object) this;
    }
}
