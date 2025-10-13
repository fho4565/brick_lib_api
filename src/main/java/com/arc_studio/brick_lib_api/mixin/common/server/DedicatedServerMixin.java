package com.arc_studio.brick_lib_api.mixin.common.server;

import com.arc_studio.brick_lib_api.config.ConfigTracker;
import com.arc_studio.brick_lib_api.config.ModConfig;
import com.arc_studio.brick_lib_api.Constants;
import net.minecraft.server.dedicated.DedicatedServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DedicatedServer.class)
public class DedicatedServerMixin {
    @Inject(method = "initServer",at = @At(value = "INVOKE", target = "Lnet/minecraft/server/dedicated/DedicatedServer;setPlayerList(Lnet/minecraft/server/players/PlayerList;)V", shift = At.Shift.AFTER))
    public void serverAboutToStart(CallbackInfoReturnable<Boolean> cir){
        ConfigTracker.loadConfigs(ModConfig.Type.SERVER, Constants.serverConfigFolder());

    }

    @Unique
    private DedicatedServer getThis(){
        return (DedicatedServer)(Object)this;
    }
}
