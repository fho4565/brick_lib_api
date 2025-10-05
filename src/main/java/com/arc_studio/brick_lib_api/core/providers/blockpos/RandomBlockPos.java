package com.arc_studio.brick_lib_api.core.providers.blockpos;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.phys.AABB;
/**
 * 随机方块坐标提供者，提供一个范围内随机的方块坐标
 * */
public class RandomBlockPos extends BlockPosProvider{
    final AABB aabb;
    public RandomBlockPos(AABB aabb){
        this.aabb = aabb;
    }

    @Override
    public BlockPos sample(RandomSource source) {
        int x = source.nextInt((int) aabb.minX, (int) (aabb.maxX+1));
        int y = source.nextInt((int) aabb.minY, (int) (aabb.maxY+1));
        int z = source.nextInt((int) aabb.minZ, (int) (aabb.maxZ+1));
        return BlockPos.containing(x,y,z);
    }

    @Override
    public BlockPos corner1() {
        return BlockPos.containing(aabb.minX,aabb.minY,aabb.minZ);
    }

    @Override
    public BlockPos corner2() {
        return BlockPos.containing(aabb.maxX,aabb.maxY,aabb.maxZ);
    }
}
