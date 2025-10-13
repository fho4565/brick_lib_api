package com.arc_studio.brick_lib_api.mixin.common.client;

import com.arc_studio.brick_lib_api.config.ConfigTracker;
import net.minecraft.client.multiplayer.ClientHandshakePacketListenerImpl;
import net.minecraft.network.Connection;
//? if <1.21.2 {
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
//?} else {
/*import net.minecraft.network.protocol.login.ClientboundLoginFinishedPacket;
*///?}
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author fho4565
 */
@Mixin(ClientHandshakePacketListenerImpl.class)
public class ClientHandshakePacketListenerImplMixin {
    @Shadow @Final private Connection connection;

    //? if >= 1.20.6 {
    /*//? if >= 1.21.2 {
    /^@Inject(method = "handleLoginFinished", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;setupOutboundProtocol(Lnet/minecraft/network/ProtocolInfo;)V"))
    public void defaultConfig(ClientboundLoginFinishedPacket packet, CallbackInfo ci) {
        ConfigTracker.loadDefaultServerConfigs();
    }

    ^///?} else {
    @Inject(method = "handleGameProfile", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;setupOutboundProtocol(Lnet/minecraft/network/ProtocolInfo;)V"))
    public void defaultConfig(ClientboundGameProfilePacket packet, CallbackInfo ci) {
        ConfigTracker.loadDefaultServerConfigs();
    }
    //?}
    *///?} else if = 1.20.4 {
    /*@Inject(method = "handleGameProfile", at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;setListener(Lnet/minecraft/network/PacketListener;)V"))
    public void defaultConfig(ClientboundGameProfilePacket packet, CallbackInfo ci) {
        ConfigTracker.loadDefaultServerConfigs();
    }
    *///?} else {
    @Inject(method = "handleGameProfile",at = @At(value = "INVOKE", target = "Lnet/minecraft/network/Connection;setProtocol(Lnet/minecraft/network/ConnectionProtocol;)V"))
    public void defaultConfig1(ClientboundGameProfilePacket packet, CallbackInfo ci) {
        ConfigTracker.loadDefaultServerConfigs();
    }
    //?}
}
