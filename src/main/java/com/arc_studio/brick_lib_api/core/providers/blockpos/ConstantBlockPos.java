package com.arc_studio.brick_lib_api.core.providers.blockpos;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

/**
 * 提供一个恒定坐标的方块坐标提供者
 * */
public class ConstantBlockPos extends BlockPosProvider{
    final BlockPos blockPos;
    public ConstantBlockPos(BlockPos blockPos) {
        this.blockPos = blockPos;
    }
    public static ConstantBlockPos of(BlockPos blockPos){
        return new ConstantBlockPos(blockPos);
    }

    @Override
    public BlockPos sample(RandomSource source) {
        return blockPos;
    }

    @Override
    public BlockPos corner1() {
        return blockPos;
    }

    @Override
    public BlockPos corner2() {
        return blockPos;
    }
}
