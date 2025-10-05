package com.arc_studio.brick_lib_api.core.providers.blockpos;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

/**
 * 范围内随机空气方块坐标的方块坐标提供者
 * */
public class RandomAirBlockPos extends CachedRandomBlockPos {
    public RandomAirBlockPos(LevelAccessor levelAccessor, AABB aabb) {
        super(levelAccessor,aabb);
        refreshCache();
    }

    @Override
    public void refreshCache() {
        int x1 = (int) Math.round(aabb.minX);
        int y1 = (int) Math.round(aabb.minY);
        int z1 = (int) Math.round(aabb.minZ);
        int x2 = (int) Math.round(aabb.maxX);
        int y2 = (int) Math.round(aabb.maxY);
        int z2 = (int) Math.round(aabb.maxZ);
        for (int x = x1; x <= x2; x++) {
            for (int y = y1; y <= y2; y++) {
                for (int z = z1; z <= z2; z++) {
                    BlockPos blockPos = BlockPos.containing(x, y, z);
                    BlockState blockState = levelAccessor.getBlockState(blockPos);
                    if (blockState.isAir()) {
                        resultCache.add(blockPos);
                    }
                }
            }
        }
    }
}
