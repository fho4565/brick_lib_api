package com.arc_studio.brick_lib_api.core.interfaces.consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Function;
/**
 * 接受{@link BlockPos}并转换成BlockState的接口
 *
 * @see Function
 */
public interface BlockPosToStateFunction extends Function<BlockPos, BlockState> {
}
