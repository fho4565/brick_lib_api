package com.arc_studio.brick_lib_api.core.providers.blockpos;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;

/**
 * 方块坐标提供者的基类，方块坐标提供者负责提供一个方块坐标
 * @author arc_studio
 * */
public abstract class BlockPosProvider {
    /**
     * 获取一个坐标
     * */
    public abstract BlockPos sample(RandomSource source);
    /**
     * 获取到的坐标的最小值
     * */
    public abstract BlockPos corner1();
    /**
     * 获取到的坐标的最大值
     * */
    public abstract BlockPos corner2();
}
