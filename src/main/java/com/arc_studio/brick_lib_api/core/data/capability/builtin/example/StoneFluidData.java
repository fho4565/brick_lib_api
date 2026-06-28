package com.arc_studio.brick_lib_api.core.data.capability.builtin.example;

import com.arc_studio.brick_lib_api.core.data.capability.IFluidStorage;
import com.arc_studio.brick_lib_api.core.data.capability.impl.SimpleFluidStorage;
import com.arc_studio.brick_lib_api.core.data.saved_data.BrickSavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理石头方块的流体能力数据
 * <p>
 * 由于原版石头没有 BlockEntity，使用 BrickSavedData 按世界维度存储
 * 每个位置的石头可以储存最多 32 桶水。
 * </p>
 */
public class StoneFluidData extends BrickSavedData {

    private static final String DATA_NAME = "brick_lib_stone_fluid";
    private static final long CAPACITY = IFluidStorage.BUCKET * 32;

    //? if >= 1.21.5 {
    private static final com.mojang.serialization.Codec<StoneFluidData> CODEC =
        CompoundTag.CODEC.xmap(StoneFluidData::new, data -> data.saveData(new CompoundTag()));
    private static final net.minecraft.world.level.saveddata.SavedDataType<StoneFluidData> TYPE =
        new net.minecraft.world.level.saveddata.SavedDataType<>(
            DATA_NAME,
            StoneFluidData::new,
            CODEC,
            null
        );
    //?}

    private final Map<BlockPos, SimpleFluidStorage> storageMap = new HashMap<>();

    public StoneFluidData() {
    }

    public StoneFluidData(CompoundTag tag) {
        //? if >= 1.21.5 {
        ListTag list = tag.getListOrEmpty("entries");
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i).orElseThrow();
            BlockPos pos = new BlockPos(
                entry.getInt("x").orElse(0),
                entry.getInt("y").orElse(0),
                entry.getInt("z").orElse(0)
            );
            long amount = entry.getLong("amount").orElse(0L);
        //?} else {
        /*ListTag list = tag.getList("entries", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            //? if >= 1.20.6 {
            BlockPos pos = NbtUtils.readBlockPos(entry, "pos").orElse(BlockPos.ZERO);
            //?} else {
            /^BlockPos pos = NbtUtils.readBlockPos(entry.getCompound("pos"));
            ^///?}
            long amount = entry.getLong("amount");
        *///?}
            // 只存储水，所以重建时设置水
            SimpleFluidStorage storage = new SimpleFluidStorage(CAPACITY);
            if (amount > 0) {
                storage.setContent(Fluids.WATER, amount);
            }
            storageMap.put(pos.immutable(), storage);
        }
    }

    @Override
    public CompoundTag saveData(CompoundTag tag) {
        ListTag list = new ListTag();
        storageMap.forEach((pos, storage) -> {
            // 只保存有内容的
            if (!storage.isEmpty()) {
                CompoundTag entry = new CompoundTag();
                //? if >= 1.21.5 {
                entry.putInt("x", pos.getX());
                entry.putInt("y", pos.getY());
                entry.putInt("z", pos.getZ());
                //?} else {
                /*entry.put("pos", NbtUtils.writeBlockPos(pos));
                *///?}
                entry.putLong("amount", storage.getFluidAmountInTank(0));
                list.add(entry);
            }
        });
        tag.put("entries", list);
        return tag;
    }

    @Override
    public String dataName() {
        return "";
    }

    /**
     * 获取指定位置的流体存储，如果不存在则创建
     */
    public SimpleFluidStorage getOrCreate(BlockPos pos) {
        return storageMap.computeIfAbsent(pos.immutable(), p -> new SimpleFluidStorage(CAPACITY));
    }

    /**
     * 移除指定位置的数据（方块被破坏时调用）
     */
    public void remove(BlockPos pos) {
        if (storageMap.remove(pos.immutable()) != null) {
            setDirty();
        }
    }

    /**
     * 从 ServerLevel 获取当前数据实例
     */
    public static StoneFluidData get(ServerLevel level) {
        //? if >= 1.21.5 {
        return level.getDataStorage().computeIfAbsent(TYPE);
        //?} else if >= 1.20.6 {
        /*return level.getDataStorage().computeIfAbsent(
            new SavedData.Factory<>(StoneFluidData::new, (compoundTag, provider) ->
                new StoneFluidData(compoundTag), DataFixTypes.CHUNK),
            DATA_NAME
        );
        *///?} else if >= 1.20.2 {
        /*return level.getDataStorage().computeIfAbsent(
            new SavedData.Factory<>(
                StoneFluidData::new, StoneFluidData::new, DataFixTypes.CHUNK),
            DATA_NAME
        );
        *///?} else {
        /*return level.getDataStorage().computeIfAbsent(
            StoneFluidData::new, StoneFluidData::new, DATA_NAME
        );
        *///?}
    }

    /**
     * 获取指定位置的流体存储（如果存在）
     */
    @Nullable
    public SimpleFluidStorage getExisting(BlockPos pos) {
        return storageMap.get(pos.immutable());
    }

    /**
     * 遍历所有已存储的位置和对应的流体存储
     */
    public void forEachPosition(java.util.function.BiConsumer<BlockPos, SimpleFluidStorage> consumer) {
        storageMap.forEach(consumer);
    }
}



