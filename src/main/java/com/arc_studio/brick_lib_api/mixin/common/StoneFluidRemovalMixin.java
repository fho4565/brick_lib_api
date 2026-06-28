package com.arc_studio.brick_lib_api.mixin.common;

import com.arc_studio.brick_lib_api.core.data.capability.builtin.example.FurnaceEnergyData;
import com.arc_studio.brick_lib_api.core.data.capability.builtin.example.StoneFluidData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
//? if >= 1.21.5 {
import net.minecraft.world.level.block.Block;
//?}
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Clears the example chest fluid capability data as soon as the backing chest is
 * removed or replaced by another block.
 */
@Mixin(
        //? if >= 1.21.5 {
        Block.class
        //?} else {
        /*BlockBehaviour.class
        *///?}
)
public abstract class StoneFluidRemovalMixin {

    //? if < 1.21.5 {
    /*@Inject(method = "onRemove", at = @At("HEAD"))
    private void brickLib$onRemoveStoneFluidStorage(
            BlockState state, Level level, BlockPos pos, BlockState newState, boolean movedByPiston, CallbackInfo ci
    ) {
        if (state.is(Blocks.CHEST) && !state.is(newState.getBlock())) {
            brickLib$removeStoneFluidStorage(level, pos);
        }
        if (state.is(Blocks.FURNACE) && !state.is(newState.getBlock())) {
            brickLib$removeFurnaceEnergyStorage(level, pos);
        }
    }

    @Unique
    private static void brickLib$removeStoneFluidStorage(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            StoneFluidData.get(serverLevel).remove(pos);
        }
    }

    @Unique
    private static void brickLib$removeFurnaceEnergyStorage(Level level, BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            FurnaceEnergyData.get(serverLevel).remove(pos);
        }
    }
    *///?}
}




