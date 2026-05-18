package com.arc_studio.brick_lib_api.mixin.common;

import com.arc_studio.brick_lib_api.core.data.capability.builtin.example.FurnaceEnergyData;
import com.arc_studio.brick_lib_api.core.data.capability.builtin.example.StoneFluidData;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Clears stale example chest fluid capability data when a new chest is placed at
 * the same position.
 */
@Mixin(Block.class)
public abstract class StoneFluidLifecycleMixin {

    @Inject(method = "setPlacedBy", at = @At("HEAD"))
    private void brickLib$onSetPlacedByStoneFluidStorage(
            Level level, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack, CallbackInfo ci
    ) {
        if (state.is(Blocks.CHEST)) {
            brickLib$removeStoneFluidStorage(level, pos);
        }
        if (state.is(Blocks.FURNACE)) {
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
}


