package com.arc_studio.brick_lib_api.core.providers.blockstate;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BlockStateProvider {
    public abstract BlockState sample(RandomSource source);
}
