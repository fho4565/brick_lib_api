package com.arc_studio.brick_lib_api.core.providers.blockstate;

import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Random;

public class WeightedBlockState extends BlockStateProvider {
    protected ArrayList<Pair<Integer, BlockState>> list;
    protected boolean calculated = false;
    protected int total = 0;

    public WeightedBlockState() {
    }
    /**
     * 添加一个权重对象
     * @param weight 权重，必须大于0
     * @param blockState 方块
     * */
    public WeightedBlockState add(int weight, BlockState blockState) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Weight must be positive: " + weight);
        }
        this.list.add(Pair.of(weight, blockState));
        return this;
    }

    /**
     * @param source
     * @return
     */
    @Override
    public BlockState sample(RandomSource source) {
        if (!calculated) {
            calculate();
        }

        Random random = new Random();
        int target = random.nextInt(total);

        int currentSum = 0;
        for (Pair<Integer, BlockState> pair : list) {
            currentSum += pair.getLeft();
            if (target < currentSum) {
                return pair.getRight();
            }
        }

        throw new IllegalStateException("No value selected");
    }

    protected void calculate() {
        total = 0;
        for (Pair<Integer, BlockState> pair : list) {
            int weight = pair.getLeft();
            total += weight;
        }
    }
}
