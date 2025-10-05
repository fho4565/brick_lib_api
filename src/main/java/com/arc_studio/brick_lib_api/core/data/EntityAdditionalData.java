package com.arc_studio.brick_lib_api.core.data;

import com.arc_studio.brick_lib_api.BrickLibAPI;
import com.arc_studio.brick_lib_api.Constants;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * 在实体上的额外数据
 * <p color = "red">当UUID对应的实体丢失时，储存的数据会丢失</p>
 * */
public abstract class EntityAdditionalData extends BaseAdditionalData {
    private static final HashMap<UUID, EntityAdditionalData> map = new HashMap<>();

    public static EntityAdditionalData getData(UUID uuid) {
        return map.get(uuid);
    }
    public static void addData(UUID uuid, EntityAdditionalData data){
        map.put(uuid, data);
    }

    public static void tick() {
        HashSet<UUID> notFounded = new HashSet<>(map.keySet());
        Constants.currentServer().getAllLevels().forEach(serverLevel ->
                notFounded.removeIf(uuid ->
                        serverLevel.getEntity(uuid) != null));
        notFounded.forEach(map::remove);
    }
    public static void modifyData(UUID uuid, Consumer<CompoundTag> consumer){
        EntityAdditionalData extraData = getData(uuid);
        if(extraData != null){
            consumer.accept(extraData.data);
        }
    }
    public static void save() throws IOException {
        String path = Constants.brickLibWorldFolder() + File.separator + "entity.dat";
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
        }
        CompoundTag root = new CompoundTag();
        ListTag list = new ListTag();
        for (UUID uuid : map.keySet()) {
            EntityAdditionalData extraData = map.get(uuid);
            CompoundTag compoundTag = new CompoundTag();
            compoundTag.putUUID("uuid",uuid);
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
        String path = Constants.brickLibWorldFolder() + File.separator + "entity.dat";
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
        ListTag list = root.getList("data", 10);
        for (int i = 0; i < list.size(); i++) {
            CompoundTag compoundTag = list.getCompound(i);
            UUID uuid = compoundTag.getUUID("uuid");
            CompoundTag data = compoundTag.getCompound("data");
            EntityAdditionalData extraData = new EntityAdditionalData() {
                @Override
                public void onDelete() {
                }
            };
            extraData.data = data;
            map.put(uuid,extraData);
        }
    }
}
