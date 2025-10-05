package com.arc_studio.brick_lib_api.core.interfaces.consumer;

import net.minecraft.core.BlockPos;

import java.util.function.Consumer;

/**
 * 接受{@link BlockPos}的消费者接口
 *
 * @see Consumer
 */
public interface BlockPosConsumer extends Consumer<BlockPos> {
}
