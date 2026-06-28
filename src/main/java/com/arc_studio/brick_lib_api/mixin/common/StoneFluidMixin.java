package com.arc_studio.brick_lib_api.mixin.common;

import com.arc_studio.brick_lib_api.core.data.capability.builtin.example.CapabilityExamples;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
//? if >= 1.20.6 && < 1.21.3 {
/*import net.minecraft.world.ItemInteractionResult;
*///?}
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Mixin 拦截箱子右键交互，将桶操作路由到流体能力示例
 */
@Mixin(ChestBlock.class)
public abstract class StoneFluidMixin {


    //? if >= 1.20.6 {
    @Inject(method = "useWithoutItem", at = @At("HEAD"), cancellable = true)
    private void brickLib$onUseStone(
            BlockState state, Level level, BlockPos pos,
            Player player, BlockHitResult hitResult,
            CallbackInfoReturnable<InteractionResult> cir
    ) {
        InteractionResult result = CapabilityExamples.handleChestUse(
                state, level, pos, player, InteractionHand.MAIN_HAND, hitResult
        );
        if (result != InteractionResult.PASS) {
            cir.setReturnValue(result);
        }
    }
    //?} else {
    /*@Inject(method = "use", at = @At("HEAD"), cancellable = true)
    private void brickLib$onUseStone(
        BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand,
        BlockHitResult hit, CallbackInfoReturnable<InteractionResult> cir
    ) {
        InteractionResult result = CapabilityExamples.handleChestUse(
                state, level, pos, player, player.getUsedItemHand(), hit
        );
        if (result != InteractionResult.PASS) {
            cir.setReturnValue(InteractionResult.CONSUME);
        }
    }
    *///?}
}
