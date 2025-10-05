package com.arc_studio.brick_lib_api.core;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 单个坐标和方块的保存对象
 * */
public record SingleBlock(BlockPos blockPos,@Nullable BlockState blockState) {
    public static SingleBlock of(BlockPos blockPos,@Nullable BlockState blockState) {
        return new SingleBlock(blockPos, blockState);
    }

    /**
     * 将此对象序列化成CompoundTag对象
     */
    public CompoundTag serialize() {
        CompoundTag compoundTag = new CompoundTag();
        long pos = blockPos.asLong();
        compoundTag.putLong("pos", pos);
        if (this.blockState != null) {
            Tag blockState = BlockState.CODEC.encodeStart(NbtOps.INSTANCE, this.blockState)/*? >=1.20.6 {*/ /*.getOrThrow() *//*?} else {*/.get().orThrow()/*?}*/;
            compoundTag.put("state", blockState);
        }
        return compoundTag;
    }

    /**
     * 将CompoundTag对象反序列化成SingleBlock对象
     */
    public static SingleBlock deserialize(CompoundTag compoundTag) {
        long pos = compoundTag.getLong("pos");
        BlockState state = null;
        if (compoundTag.contains("state")) {
            state = BlockState.CODEC.decode(NbtOps.INSTANCE, compoundTag.get("state")).result().orElseThrow().getFirst();
        }
        return SingleBlock.of(BlockPos.of(pos), state);
    }
}
