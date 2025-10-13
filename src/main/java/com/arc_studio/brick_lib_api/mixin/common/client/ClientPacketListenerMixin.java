package com.arc_studio.brick_lib_api.mixin.common.client;

import com.arc_studio.brick_lib_api.client.command.ClientCommandInternals;

import com.arc_studio.brick_lib_api.misc.HolderLookup;
import com.arc_studio.brick_lib_api.register.BrickRegistries;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.multiplayer.ClientPacketListener;

import net.minecraft.client.multiplayer.ClientSuggestionProvider;

import net.minecraft.commands.SharedSuggestionProvider;

import net.minecraft.core.RegistryAccess;
import net.minecraft.network.protocol.game.ClientboundCommandsPacket;
import net.minecraft.network.protocol.game.ClientboundLoginPacket;
//? if > 1.19.2 {
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.client.multiplayer.ClientRegistryLayer;
import net.minecraft.core.LayeredRegistryAccess;
//?} else {

//?}
//? if = 1.18.2 {
/*import com.arc_studio.brick_lib_api.misc.CommandBuildContext;*/
//?} else {
import net.minecraft.commands.CommandBuildContext;
//?}
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.stream.Stream;
//? if >1.20.1 {
//?}

@Mixin(ClientPacketListener.class)
public abstract class ClientPacketListenerMixin {
//? if >1.19.2 {

    //? if > 1.20.6 {
    /*@Final
     *///?}
    @Shadow
    private FeatureFlagSet enabledFeatures;

    //? if > 1.20.6 && fabric {
    /*    @Shadow
        private CommandDispatcher<SharedSuggestionProvider> commands;*/
    //?} elif > 1.20.1 {
        /*@Shadow
        public CommandDispatcher<SharedSuggestionProvider> commands;
        *///?}
    @Shadow
    public CommandDispatcher<SharedSuggestionProvider> commands;
    @Shadow
    @Final
    private ClientSuggestionProvider suggestionsProvider;

    //? if <= 1.20.1 {
    @Shadow
    private LayeredRegistryAccess<ClientRegistryLayer> registryAccess;
    //?} else {
    /*@Shadow @Final private RegistryAccess.Frozen registryAccess;
     *///?}

    @Inject(method = "handleLogin", at = @At("RETURN"))
    private void onGameJoin(ClientboundLoginPacket packet, CallbackInfo info) {
        CommandDispatcher<ClientSuggestionProvider> dispatcher = new CommandDispatcher<>();
        ClientCommandInternals.setActiveDispatcher(dispatcher);
        BrickRegistries.CLIENT_COMMAND.foreachRegisteredValue(function -> {
            //? if >= 1.20.6 {
            /*dispatcher.register(function.apply(CommandBuildContext.simple(this.registryAccess, this.enabledFeatures)));
             *///?} else {
            dispatcher.register(function.apply(CommandBuildContext
                .configurable(
                    //? if <= 1.20.1 {
                    this.registryAccess.compositeAccess()
                    //?} else {
                    /*this.registryAccess
                     *///?}
                    , this.enabledFeatures)));
            //?}
        });
        ClientCommandInternals.finalizeInit();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "handleCommands", at = @At("RETURN"))
    private void onOnCommandTree(ClientboundCommandsPacket packet, CallbackInfo info) {
        ClientCommandInternals.addCommands((CommandDispatcher) commands, suggestionsProvider);
    }

    @Inject(method = "sendUnsignedCommand", at = @At("HEAD"), cancellable = true)
    private void onSendCommand(String command, CallbackInfoReturnable<Boolean> cir) {
        if (ClientCommandInternals.executeCommand(command)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "sendCommand", at = @At("HEAD"), cancellable = true)
    private void onSendCommand(String command, CallbackInfo info) {
            /*PlayerEvent.Chat.Send.Pre event = new PlayerEvent.Chat.Send.Pre(Minecraft.getInstance().player, command, command, true);
            clientPreSendCmd.set(event);
            if(!BrickEventBus.postEventClient(event)) {
                if (ClientCommandInternals.executeCommand(command)) {
                    info.cancel();
                }
            }else{
                info.cancel();
            }*/
        if (ClientCommandInternals.executeCommand(command)) {
            info.cancel();
        }
    }
//?} else {

    /*@Shadow
    private CommandDispatcher<SharedSuggestionProvider> commands;

    @Shadow
    @Final
    private ClientSuggestionProvider suggestionsProvider;

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Inject(method = "handleCommands", at = @At("RETURN"))
    private void onOnCommandTree(ClientboundCommandsPacket packet, CallbackInfo info) {
        ClientCommandInternals.addCommands((CommandDispatcher) commands, suggestionsProvider);
    }

    @Inject(method = "handleLogin", at = @At("RETURN"))
    public void Inject126(ClientboundLoginPacket packet, CallbackInfo ci) {
        CommandDispatcher<ClientSuggestionProvider> dispatcher = new CommandDispatcher<>();
        ClientCommandInternals.setActiveDispatcher(dispatcher);
        BrickRegistries.CLIENT_COMMAND.foreachRegisteredValue(function -> {
            //? if =1.18.2 {
            /^dispatcher.register(function.apply(CommandBuildContext.simple(HolderLookup.Provider.create(Stream.empty()))));^/
            //?} else {
            dispatcher.register(function.apply(new CommandBuildContext(RegistryAccess.builtinCopy())));
            //?}
        });
        ClientCommandInternals.finalizeInit();
    }
*///?}
}
