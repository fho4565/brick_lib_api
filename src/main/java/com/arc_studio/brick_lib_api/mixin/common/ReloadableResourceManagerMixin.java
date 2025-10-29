package com.arc_studio.brick_lib_api.mixin.common;

import com.arc_studio.brick_lib_api.config.ConfigTracker;
import com.arc_studio.brick_lib_api.config.ModConfig;
import com.arc_studio.brick_lib_api.Constants;
import com.arc_studio.brick_lib_api.core.SideExecutor;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author fho4565
 */
@Mixin(ReloadableResourceManager.class)
public abstract class ReloadableResourceManagerMixin {

    @Inject(method = "<init>", at = @At("RETURN"))
    public void initMixin(PackType type, CallbackInfo ci) {
        SideExecutor.runOnClient(() -> () -> ConfigTracker.loadConfigs(ModConfig.Type.CLIENT, Constants.globalConfigFolderPath()));
        ConfigTracker.loadConfigs(ModConfig.Type.COMMON, Constants.globalConfigFolderPath());
    }
}
