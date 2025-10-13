package com.arc_studio.brick_lib_api.core.data;

import com.arc_studio.brick_lib_api.BrickLibAPI;
import com.arc_studio.brick_lib_api.Constants;
import com.arc_studio.brick_lib_api.core.SingleBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;
import java.util.function.Consumer;

/**
 * 在方块上的额外数据
 * <p color = "red">当该位置的方块类型发生变化时，储存的数据会丢失</p>
 * */
public class BlockAdditionalData extends BaseAdditionalData {
    public static final HashMap<ResourceKey<Level>, HashMap<SingleBlock, BlockAdditionalData>> map = new HashMap<>();

    public static BlockAdditionalData getData(ResourceKey<Level> level, BlockPos blockPos) {
        HashMap<SingleBlock, BlockAdditionalData> hashMap = map.get(level);
        if (hashMap != null) {
            for (SingleBlock key : hashMap.keySet()) {
                if (key.blockPos().equals(blockPos)) {
                    return hashMap.get(key);
                }
            }
            return null;
        }
        return null;
    }

    public static void addData(ResourceKey<Level> level, BlockPos blockPos, BlockAdditionalData data) {
        HashMap<SingleBlock, BlockAdditionalData> orDefault = map.getOrDefault(level, new HashMap<>());
        ServerLevel serverLevel = Constants.currentServer().getLevel(level);
        orDefault.put(new SingleBlock(blockPos, serverLevel.getBlockState(blockPos)), data);
        map.put(level, orDefault);
    }

    public static void tick() {
        for (ResourceKey<Level> key : map.keySet()) {
            Optional.ofNullable(Constants.currentServer().getLevel(key)).ifPresent(serverLevel -> {
                HashMap<SingleBlock, BlockAdditionalData> toDelete = map.get(key);
                toDelete.keySet().removeIf(singleBlock -> {
                    BlockState blockState = singleBlock.blockState();
                    if (blockState == null) {
                        return false;
                    } else if (blockState.isAir()) {
                        return false;
                    }
                    return serverLevel.getBlockState(singleBlock.blockPos()).is(blockState.getBlock());
                });
                toDelete.forEach((singleBlock, blockAdditionalData) -> {
                    toDelete.remove(singleBlock);
                });
            });
        }
    }

    public static void modifyData(ResourceKey<Level> level, BlockPos blockPos, Consumer<CompoundTag> consumer) {
        BlockAdditionalData extraData = getData(level, blockPos);
        if (extraData != null) {
            consumer.accept(extraData.data);
        }
    }

    public static void save() {
        String path = Constants.brickLibWorldFolder() + File.separator + "block.dat";
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                BrickLibAPI.LOGGER.error(e.toString());
            }
        }
        CompoundTag result = new CompoundTag();
        ListTag levelList = new ListTag();

        for (ResourceKey<Level> levelKey : map.keySet()) {
            CompoundTag levelTag = new CompoundTag();
            levelTag.put("level", Level.RESOURCE_KEY_CODEC.encodeStart(NbtOps.INSTANCE, levelKey).result().orElseThrow());

            HashMap<SingleBlock, BlockAdditionalData> blockDataMap = map.get(levelKey);
            ListTag blockList = new ListTag();

            for (SingleBlock singleBlock : blockDataMap.keySet()) {
                CompoundTag blockTag = new CompoundTag();
                blockTag.put("single_block", singleBlock.serialize());
                BlockAdditionalData extraData = blockDataMap.get(singleBlock);
                blockTag.put("extra_data", extraData.data);
                blockList.add(blockTag);
            }

            levelTag.put("blocks", blockList);
            levelList.add(levelTag);
        }

        result.put("data", levelList);
        try {
            NbtIo.write(result, file
                            //? if >= 1.20.4 {
                            /*.toPath()
                    *///?}
            );
        } catch (IOException e) {
            BrickLibAPI.LOGGER.error(e.toString());
        }
    }

    public static void load() throws IOException {
        String path = Constants.brickLibWorldFolder() + File.separator + "block.dat";
        File file = new File(path);
        if (!file.exists()) {
            file.createNewFile();
            return;
        }

        CompoundTag result;
        try {
            result = Optional.ofNullable(NbtIo.read(file
                            //? if >= 1.20.4 {
                            /*.toPath()
                            *///?}
            )).orElse(new CompoundTag());
        } catch (IOException e) {
            BrickLibAPI.LOGGER.error(e.toString());
            result = new CompoundTag();
        }
        ListTag levelList =
            //? if >= 1.21.5 {
            /*result.getList("data").orElse(new ListTag())
        *///?} else {
        result.getList("data", 10)
            //?}
            ;
        for (int i = 0; i < levelList.size(); i++) {
            //? if >= 1.21.5 {
            /*CompoundTag tag = new CompoundTag();
            tag.putString("level","");
            CompoundTag levelTag = levelList.getCompound(i).orElse(tag);
            *///?} else {
            CompoundTag levelTag = levelList.getCompound(i);
            //?}
            Tag levelKeyString = levelTag.get("level");
            ResourceKey<Level> levelKey = Level.RESOURCE_KEY_CODEC.decode(NbtOps.INSTANCE, levelKeyString).result().orElseThrow().getFirst();
            ListTag blockList =
                //? if >= 1.21.5 {
                /*levelTag.getList("blocks").orElse(new ListTag());
            *///?} else {
            levelTag.getList("blocks", 10);
            //?}
            for (int j = 0; j < blockList.size(); j++) {
                CompoundTag blockTag = blockList.getCompound(j)
                    //? if >= 1.21.5 {
                    /*.orElse(new CompoundTag())
                    *///?}
                    ;
                SingleBlock singleBlock = SingleBlock.deserialize(blockTag.getCompound("single_block")
                        //? if >= 1.21.5 {
                        /*.orElse(new CompoundTag())
                    *///?}
                );
                CompoundTag extraData = blockTag.getCompound("extra_data")
                    //? if >= 1.21.5 {
                    /*.orElse(new CompoundTag())
                    *///?}
                    ;
                BlockAdditionalData blockAdditionalData = new BlockAdditionalData();
                blockAdditionalData.data = extraData;
                addData(levelKey, singleBlock.blockPos(), blockAdditionalData);
            }
        }
    }
}
