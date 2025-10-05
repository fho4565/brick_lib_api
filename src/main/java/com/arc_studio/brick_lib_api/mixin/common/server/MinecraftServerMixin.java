package com.arc_studio.brick_lib_api.mixin.common.server;

import com.arc_studio.brick_lib_api.BrickLibAPI;
import com.arc_studio.brick_lib_api.core.task.TaskExecutor;
import com.arc_studio.brick_lib_api.core.data.BlockAdditionalData;
import com.arc_studio.brick_lib_api.core.data.EntityAdditionalData;
import com.arc_studio.brick_lib_api.core.data.LevelAdditionalData;
import com.arc_studio.brick_lib_api.core.data.WorldAdditionalData;
import com.arc_studio.brick_lib_api.Constants;
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
@SuppressWarnings({"rawtypes"})
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Inject(method = "runServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/MinecraftServer;onServerExit()V"))
    public void onServerStopped(CallbackInfo ci) {
        Constants.uninstallWorldVariables();
    }

    @Inject(method = "tickServer", at = @At("HEAD"))
    public void onServerTickStart(BooleanSupplier hasTimeLeft, CallbackInfo ci) {
        BlockAdditionalData.tick();
        EntityAdditionalData.tick();
        LevelAdditionalData.tick();
        TaskExecutor.tick();
    }

    @Inject(method = "tickServer", at = @At("TAIL"))
    public void onServerTickEnd(BooleanSupplier hasTimeLeft, CallbackInfo ci) {

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
    @Unique
    private MinecraftServer getThis(){
        return (MinecraftServer) (Object) this;
    }
}
