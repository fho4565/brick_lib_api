package com.arc_studio.brick_lib_api.core.data;

import com.arc_studio.brick_lib_api.BrickLibAPI;
import com.arc_studio.brick_lib_api.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelResource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 在存档上的额外数据
 * */
public class WorldAdditionalData extends BaseAdditionalData {
    private final String worldName;
    private static final HashMap<String, WorldAdditionalData> map = new HashMap<>();

    public WorldAdditionalData(String worldName) {
        this.worldName = worldName;
    }

    public static WorldAdditionalData getWorldData() {;
        String string = Constants.currentServer().getWorldPath(LevelResource.ROOT)
                .getParent().getFileName().toString();
        return map.computeIfAbsent(string, s -> new WorldAdditionalData(string));
    }
    public static WorldAdditionalData getWorldData(String worldName) {
        return map.get(worldName);
    }
    public static void addWorldData(String worldName, WorldAdditionalData data){
        map.put(worldName, data);
    }
    public static void modifyWorldData(String worldName, Consumer<CompoundTag> consumer){
        consumer.accept(getWorldData(worldName).data);
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
        ListTag list = //? if >= 1.21.5 {
            /*root.getList("data").orElse(new ListTag());
        *///?} else {
         root.getList("data", 10);
        //?}
        for (int i = 0; i < list.size(); i++) {
            CompoundTag compoundTag = list.getCompound(i)
                //? if >= 1.21.5 {
                /*.orElse(new CompoundTag())
                *///?}
                ;
            String worldName = compoundTag.getString("worldName")
                //? if >= 1.21.5 {
                /*.orElse("")
                *///?}
                ;
            CompoundTag data = compoundTag.getCompound("data")
                //? if >= 1.21.5 {
                /*.orElse(new CompoundTag())
                *///?}
                ;
            WorldAdditionalData extraData = new WorldAdditionalData(worldName);
            extraData.data = data;
            Path resolved = Constants.currentServer().getWorldPath(LevelResource.ROOT)
                    .getParent().getParent().resolve(worldName);
            if(resolved.toFile().exists()){
                addWorldData(worldName,extraData);
            } else{
                BrickLibAPI.LOGGER.warn("Cannot find world {},delete world additional data!",resolved);
            }
        }
    }

    public static void distinguish() {


    }

    public String worldName() {
        return worldName;
    }
}
