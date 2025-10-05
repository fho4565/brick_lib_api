package com.arc_studio.brick_lib_api.mixin.common.client;

import com.arc_studio.brick_lib_api.register.BrickRegistries;
import com.arc_studio.brick_lib_api.update_checker.UpdateChecker;
import net.minecraft.client.telemetry.TelemetryProperty;
import net.minecraft.client.telemetry.events.GameLoadTimesEvent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.arc_studio.brick_lib_api.BrickLibAPI.LOGGER;

/**
 * @author fho4565
 */
@Mixin(GameLoadTimesEvent.class)
public abstract class GameLoadTimesEventMixin {
    @Unique
    boolean brick_lib$checked = false;

    @Inject(method = "beginStep(Lnet/minecraft/client/telemetry/TelemetryProperty;)V", at = @At("TAIL"))
    public void inject16(TelemetryProperty<GameLoadTimesEvent.Measurement> measurement, CallbackInfo ci) {
        if(!brick_lib$checked){
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
            brick_lib$checked = true;
        }
    }
    @Unique
    private GameLoadTimesEvent getThis() {
        return (GameLoadTimesEvent) (Object) this;
    }
}