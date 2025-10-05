package com.arc_studio.brick_lib_api.core.providers.blockstate;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;

import java.util.ArrayList;
import java.util.Arrays;

public class RandomBlockState extends BlockStateProvider{
    protected ArrayList<BlockState> list;

    public RandomBlockState(BlockState ...blockStates) {
        list = new ArrayList<>(Arrays.asList(blockStates));
    }

    /**
     * @param source
     * @return
     */
    @Override
    public BlockState sample(RandomSource source) {
        return list.get(source.nextInt(list.size()));
    }
}
