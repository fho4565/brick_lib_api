package com.arc_studio.brick_lib_api.core.data;

import com.arc_studio.brick_lib_api.BrickLibAPI;
import com.arc_studio.brick_lib_api.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 在存档上的额外数据
 * */
public abstract class WorldAdditionalData extends BaseAdditionalData {
    private static final HashMap<String, WorldAdditionalData> map = new HashMap<>();
    public static WorldAdditionalData getData(String worldName) {
        return map.get(worldName);
    }
    public static void addData(String worldName, WorldAdditionalData data){
        map.put(worldName, data);
    }
    public static void modifyData(String worldName, Consumer<CompoundTag> consumer){
        consumer.accept(getData(worldName).data);
    }
    public static void save() throws IOException {
        String path = Constants.versionDataFolder()+File.separator+"world.dat";
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        CompoundTag root = new CompoundTag();
        ListTag list = new ListTag();
        for (String worldName : map.keySet()) {
            WorldAdditionalData extraData = map.get(worldName);
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putString("worldName",worldName);
            compoundTag.put("data",extraData.data);
            list.add(compoundTag);
        }
        root.put("data",list);
        try {
            NbtIo.write(root, file
                            //? if >= 1.20.4 {
                            /*.toPath()
                    *///?}
            );
        } catch (IOException e) {
            BrickLibAPI.LOGGER.error(e.toString());
        }
    }
    public static void load() throws IOException {
        String path = Constants.versionDataFolder()+File.separator+"world.dat";
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
            return;
        }
        CompoundTag root;
        try {
            root = Optional.ofNullable(NbtIo.read(file
                            //? if >= 1.20.4 {
                            /*.toPath()
                    *///?}
            )).orElse(new CompoundTag());
        } catch (IOException e) {
            BrickLibAPI.LOGGER.error(e.toString());
            root = new CompoundTag();
        }
        ListTag list = root.getList("data",10);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag compoundTag = list.getCompound(i);
            String worldName = compoundTag.getString("worldName");
            CompoundTag data = compoundTag.getCompound("data");
            WorldAdditionalData extraData = new WorldAdditionalData() {
                @Override
                public void onDelete() {
                }
            };
            extraData.data = data;
            addData(worldName,extraData);
        }
    }
}
