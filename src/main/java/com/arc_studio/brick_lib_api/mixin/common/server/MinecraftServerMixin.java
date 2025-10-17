package com.arc_studio.brick_lib_api.mixin.common.server;

import com.arc_studio.brick_lib_api.BrickLibAPI;
import com.arc_studio.brick_lib_api.config.ConfigTracker;
import com.arc_studio.brick_lib_api.config.ModConfig;
import com.arc_studio.brick_lib_api.core.data.BlockAdditionalData;
import com.arc_studio.brick_lib_api.core.data.EntityAdditionalData;
import com.arc_studio.brick_lib_api.core.data.LevelAdditionalData;
import com.arc_studio.brick_lib_api.core.data.WorldAdditionalData;
import com.arc_studio.brick_lib_api.Constants;
import com.arc_studio.brick_lib_api.core.register.BrickRegistry;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.io.IOException;
import java.util.function.BooleanSupplier;

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

    @Inject(method = "tickServer", at = @At("HEAD"))
    public void onServerTickStart(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        BlockAdditionalData.tick();
        EntityAdditionalData.tick();
        LevelAdditionalData.tick();
    }
    @Inject(method = "saveEverything", at = @At("HEAD"))
    public void save(boolean suppressLog, boolean flush, boolean forced, CallbackInfoReturnable<Boolean> cir) {
        try {
            BrickLibAPI.LOGGER.debug("Saving Brick Lib additional data");
            BlockAdditionalData.save();
            EntityAdditionalData.save();
            LevelAdditionalData.save();
            WorldAdditionalData.save();
        } catch (IOException e) {
            BrickLibAPI.LOGGER.error("Error when saving Brick Lib additional data");
            BrickLibAPI.LOGGER.error(e.toString());
        }
    }

    @Inject(method = "loadLevel", at = @At("HEAD"))
    public void load(CallbackInfo ci) {
        try {
            Constants.installWorldVariables(getThis());
            BrickLibAPI.LOGGER.debug("Loading Brick Lib additional data");
            BlockAdditionalData.load();
            EntityAdditionalData.load();
            LevelAdditionalData.load();
            WorldAdditionalData.load();
        } catch (IOException e) {
            BrickLibAPI.LOGGER.error("Error when loading Brick Lib additional data");
            BrickLibAPI.LOGGER.error(e.toString());
        }
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
