package com.arc_studio.brick_lib_api.mixin.common.client;



import com.arc_studio.brick_lib_api.client.command.ClientCommandInternals;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    //? if =1.18.2 {
    /*@Inject(method = "chat", at = @At("HEAD"), cancellable = true)
    private void onSendChatMessage(String message, CallbackInfo info) {
        if (ClientCommandInternals.executeCommand(message)) {
            info.cancel();
        }
    }
    *///?} elif = 1.19.2 {
    /*@Inject(method = "commandUnsigned(Ljava/lang/String;)Z", at = @At("HEAD"), cancellable = true)
    private void onSendCommand(String command, CallbackInfoReturnable<Boolean> cir) {
        if (ClientCommandInternals.executeCommand(command)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "commandSigned", at = @At("HEAD"), cancellable = true)
    private void onSendCommand(String command, Component preview, CallbackInfo info) {
        if (ClientCommandInternals.executeCommand(command)) {
            info.cancel();
        }
    }
    *///?}
}
