package com.arc_studio.brick_lib_api.mixin.common;

import com.arc_studio.brick_lib_api.misc.CommandBuildContext;
import com.arc_studio.brick_lib_api.register.BrickRegistries;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * @author fho4565
 */
@Mixin(Commands.class)
public class CommandsMixin {

    @Shadow
    @Final
    private CommandDispatcher<CommandSourceStack> dispatcher;

    //? if > 1.18.2 {
    @Inject(method = "<init>", at = @At(value = "TAIL"))
    public void onInit(Commands.CommandSelection selection, net.minecraft.commands.CommandBuildContext context, CallbackInfo ci) {
        CommandDispatcher<CommandSourceStack> commandDispatcher = this.dispatcher;
        BrickRegistries.COMMAND.forEach(commandRegistration -> commandDispatcher.register(commandRegistration.apply(context)));
    }

    //?} else {
    /*@Inject(method = "<init>", at = @At(value = "TAIL"))
    public void onInit(Commands.CommandSelection selection, CallbackInfo ci) {
        CommandDispatcher<CommandSourceStack> commandDispatcher = this.dispatcher;
        BrickRegistries.COMMAND.forEach(commandRegistration -> commandDispatcher.register(commandRegistration.apply(new CommandBuildContext() {
        })));
    }
    *///?}
}
