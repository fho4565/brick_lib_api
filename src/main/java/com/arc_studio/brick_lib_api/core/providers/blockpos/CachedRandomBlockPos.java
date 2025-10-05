package com.arc_studio.brick_lib_api.core.providers.blockpos;

import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
/**
 * <p>缓存结果的随机方块坐标提供者，提供者会预先计算结果并缓存，每次执行{@link #sample(RandomSource)}方法时都会从缓存中提取结果</p>
 * <p>如果缓存中没有任何结果，那么会调用{@link #ifEmpty(RandomSource)}来获取一个坐标</p>
 * <p style = "color:red">执行{@link #sample(RandomSource)}方法并不会自动刷新缓存，你需要执行{@link #refreshCache()}方法来手动刷新结果缓存</p>
 * */
public abstract class CachedRandomBlockPos extends RandomBlockPos {
    ArrayList<BlockPos> resultCache = new ArrayList<>();
    final LevelAccessor levelAccessor;

    public CachedRandomBlockPos(LevelAccessor levelAccessor,AABB aabb) {
        super(aabb);
        this.levelAccessor = levelAccessor;
    }
    /**
     * 重新计算结果并刷新结果缓存
     * */
    public abstract void refreshCache();
    /**
     * 当结果缓存为空时，调用此方法。此方法默认调用{@link RandomBlockPos#sample(RandomSource)}方法
     * */
    public BlockPos ifEmpty(RandomSource randomSource){
        return super.sample(randomSource);
    }

    protected BlockPos random(RandomSource randomSource){
        if(resultCache.isEmpty()){
            return ifEmpty(randomSource);
        }
        return resultCache.get(randomSource.nextInt(resultCache.size()));
    }
    @Override
    public BlockPos sample(RandomSource source) {
        return random(source);
    }
}
