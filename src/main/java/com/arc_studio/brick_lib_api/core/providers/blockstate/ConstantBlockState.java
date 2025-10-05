package com.arc_studio.brick_lib_api.core.providers.blockstate;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

public class ConstantBlockState extends BlockStateProvider {
    final BlockState blockState;

    public ConstantBlockState(BlockState blockState) {
        this.blockState = blockState;
    }
    public static ConstantBlockState of(BlockState blockState){
        return new ConstantBlockState(blockState);
    }

    /**
     * @param source
     * @return
     */
    @Override
    public BlockState sample(RandomSource source) {
        return blockState;
    }
}
