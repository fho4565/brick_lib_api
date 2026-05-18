package com.arc_studio.brick_lib_api.core.data.capability.builtin.example;

import com.arc_studio.brick_lib_api.core.data.capability.builtin.impl.SimpleEnergyStorage;
import com.arc_studio.brick_lib_api.core.data.saved_data.SavedData;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

/**
 * 管理熔炉方块的 FE 能量能力数据。
 * <p>
 * 原版熔炉没有能量字段，这里使用 SavedData 按世界维度和方块位置持久化。
 * 每个熔炉最多储存 32,000 FE，每次交互或管道传输最多 1,000 FE。
 * </p>
 */
public class FurnaceEnergyData extends SavedData {

    private static final String DATA_NAME = "brick_lib_furnace_energy";
    public static final long CAPACITY = 32_000L;
    public static final long TRANSFER_AMOUNT = 1_000L;

    //? if >= 1.21.5 {
    /*private static final com.mojang.serialization.Codec<FurnaceEnergyData> CODEC =
        CompoundTag.CODEC.xmap(FurnaceEnergyData::new, data -> data.saveData(new CompoundTag()));
    private static final net.minecraft.world.level.saveddata.SavedDataType<FurnaceEnergyData> TYPE =
        new net.minecraft.world.level.saveddata.SavedDataType<>(
            DATA_NAME,
            FurnaceEnergyData::new,
            CODEC,
            null
        );
    *///?}

    private final Map<BlockPos, SimpleEnergyStorage> storageMap = new HashMap<>();

    public FurnaceEnergyData() {
    }

    public FurnaceEnergyData(CompoundTag tag) {
        //? if >= 1.21.5 {
        /*ListTag list = tag.getListOrEmpty("entries");
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i).orElseThrow();
            BlockPos pos = new BlockPos(
                entry.getInt("x").orElse(0),
                entry.getInt("y").orElse(0),
                entry.getInt("z").orElse(0)
            );
            long energy = entry.getLong("energy").orElse(0L);
        *///?} else {
        ListTag list = tag.getList("entries", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag entry = list.getCompound(i);
            //? if >= 1.20.6 {
            /*BlockPos pos = NbtUtils.readBlockPos(entry, "pos").orElse(BlockPos.ZERO);
            *///?} else {
            BlockPos pos = NbtUtils.readBlockPos(entry.getCompound("pos"));
            //?}
            long energy = entry.getLong("energy");
        //?}
            SimpleEnergyStorage storage = createStorage();
            storage.setEnergy(energy);
            storageMap.put(pos.immutable(), storage);
        }
    }

    @Override
    public CompoundTag saveData(CompoundTag tag) {
        ListTag list = new ListTag();
        storageMap.forEach((pos, storage) -> {
            if (storage.getEnergyStored() > 0) {
                CompoundTag entry = new CompoundTag();
                //? if >= 1.21.5 {
                /*entry.putInt("x", pos.getX());
                entry.putInt("y", pos.getY());
                entry.putInt("z", pos.getZ());
                *///?} else {
                entry.put("pos", NbtUtils.writeBlockPos(pos));
                //?}
                entry.putLong("energy", storage.getEnergyStored());
                list.add(entry);
            }
        });
        tag.put("entries", list);
        return tag;
    }

    public SimpleEnergyStorage getOrCreate(BlockPos pos) {
        return storageMap.computeIfAbsent(pos.immutable(), p -> createStorage());
    }

    @Nullable
    public SimpleEnergyStorage getExisting(BlockPos pos) {
        return storageMap.get(pos.immutable());
    }

    public void remove(BlockPos pos) {
        if (storageMap.remove(pos.immutable()) != null) {
            setDirty();
        }
    }

    public void forEachPosition(java.util.function.BiConsumer<BlockPos, SimpleEnergyStorage> consumer) {
        storageMap.forEach(consumer);
    }

    public static FurnaceEnergyData get(ServerLevel level) {
        //? if >= 1.21.5 {
        /*return level.getDataStorage().computeIfAbsent(TYPE);
        *///?} else if >= 1.20.6 {
        /*return level.getDataStorage().computeIfAbsent(
            new net.minecraft.world.level.saveddata.SavedData.Factory<>(FurnaceEnergyData::new, (compoundTag, provider) ->
                new FurnaceEnergyData(compoundTag), DataFixTypes.CHUNK),
            DATA_NAME
        );
        *///?} else if >= 1.20.2 {
        /*return level.getDataStorage().computeIfAbsent(
            new net.minecraft.world.level.saveddata.SavedData.Factory<>(
                FurnaceEnergyData::new, FurnaceEnergyData::new, DataFixTypes.CHUNK),
            DATA_NAME
        );
        *///?} else {
        return level.getDataStorage().computeIfAbsent(
            FurnaceEnergyData::new, FurnaceEnergyData::new, DATA_NAME
        );

        //?}
    }

    private static SimpleEnergyStorage createStorage() {
        return new SimpleEnergyStorage(CAPACITY, TRANSFER_AMOUNT, TRANSFER_AMOUNT);
    }
}

