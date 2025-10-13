package com.arc_studio.brick_lib_api.core.data;

import com.arc_studio.brick_lib_api.BrickLibAPI;
import com.arc_studio.brick_lib_api.Constants;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 在维度上的额外数据
 * */
public class LevelAdditionalData extends BaseAdditionalData {
    private static final HashMap<ResourceKey<Level>, LevelAdditionalData> map = new HashMap<>();
    public static LevelAdditionalData getData(ResourceKey<Level> level) {
        return map.get(level);
    }
    public static void addData(ResourceKey<Level> level, LevelAdditionalData data){
        map.put(level, data);
    }
    public static void tick() {
        HashSet<ResourceKey<Level>> notFounded = new HashSet<>(map.keySet());
        MinecraftServer server = Constants.currentServer();
        server.getAllLevels().forEach(serverLevel ->
                notFounded.removeIf(levelResourceKey -> server.getLevel(levelResourceKey) != null));
        notFounded.forEach(map::remove);
    }
    public static void modifyData(ResourceKey<Level> level, Consumer<CompoundTag> consumer){
        LevelAdditionalData extraData = getData(level);
        if(extraData != null){
            consumer.accept(extraData.data);
        }
    }
    public static void save() throws IOException {
        String path = Constants.brickLibWorldFolder() + File.separator + "level.dat";
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        CompoundTag root = new CompoundTag();
        ListTag list = new ListTag();
        for (ResourceKey<Level> levelResourceKey : map.keySet()) {
            LevelAdditionalData extraData = map.get(levelResourceKey);
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.put("level",Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE,levelResourceKey).result().orElseThrow());
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
        String path = Constants.brickLibWorldFolder() + File.separator + "level.dat";
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
            Tag level = compoundTag.get("level");
            CompoundTag data = compoundTag.getCompound("data")
                //? if >= 1.21.5 {
                /*.orElse(new CompoundTag())
                *///?}
                ;
            LevelAdditionalData levelExtraData = new LevelAdditionalData();
            levelExtraData.data = data;
            addData(Level.RESOURCE_KEY_CODEC.decode(NbtOps.INSTANCE,level).result().orElseThrow().getFirst(),levelExtraData);
        }
    }
}
