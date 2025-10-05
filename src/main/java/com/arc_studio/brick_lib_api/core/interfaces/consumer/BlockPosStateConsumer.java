package com.arc_studio.brick_lib_api.core.interfaces.consumer;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiConsumer;

/**
 * 接受{@link BlockPos}和{@link BlockState}的消费者接口
 *
 * @see BiConsumer
 */
public interface BlockPosStateConsumer extends BiConsumer<BlockPos, BlockState> {
}
