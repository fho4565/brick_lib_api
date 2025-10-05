package com.arc_studio.brick_lib_api.core.interfaces.consumer;

import net.minecraft.world.level.block.state.BlockState;

import java.util.function.Consumer;

/**
 * 接受{@link BlockState}的消费者接口
 *
 * @see Consumer
 */
public interface BlockStateConsumer extends Consumer<BlockState> {
}
