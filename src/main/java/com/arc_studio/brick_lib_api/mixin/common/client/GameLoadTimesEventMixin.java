package com.arc_studio.brick_lib_api.mixin.common.client;

import com.arc_studio.brick_lib_api.register.BrickRegistries;
import com.arc_studio.brick_lib_api.update_checker.UpdateChecker;

//? if > 1.19.4 {
import net.minecraft.client.telemetry.events.GameLoadTimesEvent;
import net.minecraft.client.telemetry.TelemetryProperty;
//?} elif > 1.19.2 {
/*import com.mojang.authlib.minecraft.UserApiService;
import net.minecraft.client.User;
import net.minecraft.client.telemetry.ClientTelemetryManager;
import net.minecraft.client.telemetry.TelemetryProperty;

*///?} else {
/*import com.mojang.authlib.minecraft.UserApiService;
import net.minecraft.client.ClientTelemetryManager;
import java.util.Optional;
import java.util.UUID;
*///?}
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;



import static com.arc_studio.brick_lib_api.BrickLibAPI.LOGGER;

/**
 * @author fho4565
 */
//? if > 1.19.4 {
@Mixin(GameLoadTimesEvent.class)
//?} else {
/*@Mixin(ClientTelemetryManager.class)
*///?}
public abstract class GameLoadTimesEventMixin {
    @Unique
    boolean brick_lib_api$checked = false;
    //? if > 1.19.4 {

    @Inject(method = "beginStep(Lnet/minecraft/client/telemetry/TelemetryProperty;)V", at = @At("TAIL"))
    public void inject16(TelemetryProperty<GameLoadTimesEvent.Measurement> measurement, CallbackInfo ci) {
        if (!brick_lib_api$checked) {
            BrickRegistries.UPDATE_CHECK.forEach(entry -> {
                if (entry.isModrinth()) {
                    UpdateChecker.checkFromModrinthAsync(entry.url()).thenAccept(modrinthModInfos -> {
                        if (modrinthModInfos != null) {
                            entry.modrinth().accept(modrinthModInfos);
                        }
                    }).exceptionally(throwable -> {
                        LOGGER.error("Error processing Modrinth update check result", throwable);
                        return null;
                    });
                } else {
                    UpdateChecker.checkFromCustomAsync(entry.url()).thenAccept(s -> entry.custom().accept(s));
                }
            });
            brick_lib_api$checked = true;
        }
    }
    //?} elif > 1.19.2 {
    /*@Inject(method = "<init>", at = @At("TAIL"))
    private void inject55(Minecraft minecraft, UserApiService userApiService, User user, CallbackInfo ci) {
        if (!brick_lib_api$checked) {
            BrickRegistries.UPDATE_CHECK.forEach(entry -> {
                if (entry.isModrinth()) {
                    UpdateChecker.checkFromModrinthAsync(entry.url()).thenAccept(modrinthModInfos -> {
                        if (modrinthModInfos != null) {
                            entry.modrinth().accept(modrinthModInfos);
                        }
                    }).exceptionally(throwable -> {
                        LOGGER.error("Error processing Modrinth update check result", throwable);
                        return null;
                    });
                } else {
                    UpdateChecker.checkFromCustomAsync(entry.url()).thenAccept(s -> entry.custom().accept(s));
                }
            });
            brick_lib_api$checked = true;
        }
    }
    *///?} else {
    /*@Inject(method = "<init>", at = @At("TAIL"))
    private void inject55(Minecraft minecraft, UserApiService service, Optional userId, Optional clientId, UUID deviceSessionId, CallbackInfo ci) {
        if (!brick_lib_api$checked) {
            BrickRegistries.UPDATE_CHECK.forEach(entry -> {
                if (entry.isModrinth()) {
                    UpdateChecker.checkFromModrinthAsync(entry.url()).thenAccept(modrinthModInfos -> {
                        if (modrinthModInfos != null) {
                            entry.modrinth().accept(modrinthModInfos);
                        }
                    }).exceptionally(throwable -> {
                        LOGGER.error("Error processing Modrinth update check result", throwable);
                        return null;
                    });
                } else {
                    UpdateChecker.checkFromCustomAsync(entry.url()).thenAccept(s -> entry.custom().accept(s));
                }
            });
            brick_lib_api$checked = true;
        }
    }
    *///?}
}
