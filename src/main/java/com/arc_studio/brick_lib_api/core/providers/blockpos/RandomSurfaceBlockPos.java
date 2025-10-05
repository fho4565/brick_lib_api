package com.arc_studio.brick_lib_api.core.providers.blockpos;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.function.Consumer;

/**
 * 范围内随机表面方块坐标的方块坐标提供者
 * */
public class RandomSurfaceBlockPos extends CachedRandomBlockPos {
    public RandomSurfaceBlockPos(LevelAccessor levelAccessor, AABB aabb) {
        super(levelAccessor,aabb);
        refreshCache();
    }

    public void foreach(Consumer<BlockPos> consumer){
        this.resultCache.forEach(consumer);
    }

    @Override
    public void refreshCache() {
        int minX = (int) Math.round(aabb.minX);
        int minY = Math.max((int) Math.round(aabb.minY), levelAccessor.getMinBuildHeight());
        int minZ = (int) Math.round(aabb.minZ);
        int maxX = (int) Math.round(aabb.maxX);
        int maxY = Math.min((int) Math.round(aabb.maxY), levelAccessor.getMaxBuildHeight());
        int maxZ = (int) Math.round(aabb.maxZ);

        int estimatedSize = (maxX - minX + 1) * (maxZ - minZ + 1);
        resultCache = new ArrayList<>(estimatedSize);

        BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();

        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                for (int y = maxY - 1; y >= minY; y--) {
                    mutablePos.set(x, y, z);
                    if (!levelAccessor.getBlockState(mutablePos).isAir()) {
                        resultCache.add(mutablePos.above().immutable());
                        break;
                    }
                }
            }
        }
    }
}
