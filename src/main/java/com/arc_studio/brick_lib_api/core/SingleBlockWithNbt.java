package com.arc_studio.brick_lib_api.core;


import com.arc_studio.brick_lib_api.BrickLibAPI;
import com.arc_studio.brick_lib_api.Constants;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * 单个坐标和方块的保存对象，并带有NBT
 */
public record SingleBlockWithNbt(BlockPos blockPos, BlockState blockState, CompoundTag nbt) {

    public static SingleBlockWithNbt of(BlockPos blockPos, BlockState blockState, CompoundTag nbt) {
        return new SingleBlockWithNbt(blockPos, blockState, nbt);
    }

    /**
     * 将CompoundTag对象反序列化成SingleBlockWithNbt对象
     */
    public static SingleBlockWithNbt deserialize(CompoundTag compoundTag) {
        long pos = compoundTag.getLong("pos");
        BlockState state = null;
        if (compoundTag.contains("state")) {
            state = BlockState.CODEC.decode(NbtOps.INSTANCE, compoundTag.get("state")).result().orElseThrow().getFirst();
        }
        return SingleBlockWithNbt.of(BlockPos.of(pos), state, compoundTag.getCompound("nbt"));
    }

    @Nullable
    public BlockEntity blockEntity() {
        boolean b1 = blockPos == null;
        boolean b2 = blockState == null;
        boolean b3 = false;
        if (blockState != null) {
            b3 = !blockState.hasBlockEntity();
        }
        if (b1 || b2 || b3) {
            return null;
        }
        BlockEntity blockEntity = ((EntityBlock) blockState.getBlock()).newBlockEntity(blockPos, blockState);
        if (blockEntity == null) {
            return null;
        }
        if(!nbt.isEmpty()){
            try {
                //? if >= 1.20.6 {
                /*blockEntity.loadWithComponents(nbt, Constants.currentServer().registryAccess());
                *///?} else {
                blockEntity.load(nbt);
                //?}
            } catch (Exception e) {
                BrickLibAPI.LOGGER.error(e.toString());
                return null;
            }
        }
        return blockEntity;
    }


    public CompoundTag serialize() {
        CompoundTag compoundTag = new CompoundTag();
        long pos = blockPos.asLong();
        compoundTag.putLong("pos", pos);
        if (this.blockState != null) {
            Tag blockState = BlockState.CODEC.encodeStart(NbtOps.INSTANCE, this.blockState)/*? >=1.20.6 {*/ /*.getOrThrow() *//*?} else {*/.get().orThrow()/*?}*/;
            compoundTag.put("state", blockState);
        }
        compoundTag.put("nbt", nbt);
        return compoundTag;
    }
}
