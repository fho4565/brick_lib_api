package com.arc_studio.brick_lib_api.network;

import com.arc_studio.brick_lib_api.BrickLibAPI;
import com.arc_studio.brick_lib_api.core.network.PacketContent;
import com.arc_studio.brick_lib_api.core.network.context.C2SNetworkContext;
import com.arc_studio.brick_lib_api.core.network.context.S2CNetworkContext;
import com.arc_studio.brick_lib_api.core.network.type.LoginPacket;
import com.arc_studio.brick_lib_api.config.ConfigTracker;
import com.arc_studio.brick_lib_api.core.SideExecutor;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

/**
 * @author fho4565
 */
public class ConfigSyncPacket extends LoginPacket {
    private String fileName;
    private byte[] fileData;

    public ConfigSyncPacket(PacketContent content) {
        SideExecutor.runSeparately(()->()->{
            CompoundTag decompress = null;
            if (content.friendlyByteBuf().readableBytes() > 0) {
                try {
                    decompress = NbtIo.readCompressed(new ByteArrayInputStream(content.readByteArray())
                            //? if >= 1.20.4 {
                            /*, NbtAccounter.unlimitedHeap()
                            *///?}
                    );
                } catch (IOException e) {
                    BrickLibAPI.LOGGER.error(e.toString());
                    decompress = new CompoundTag();
                }
            }
            if (decompress != null) {
                //? if >= 1.21.5 {
                /*fileName = decompress.keySet().stream().findFirst().orElseThrow();
                fileData = decompress.getByteArray(fileName).orElseThrow();
                *///?} else {
                fileName = decompress.getAllKeys().stream().findFirst().orElseThrow();
                fileData = decompress.getByteArray(fileName);
                //?}
            } else {
                fileName = "";
                fileData = new byte[0];
            }
        },()->()->{
            fileName = "";
            fileData = new byte[0];
        });
    }

    public ConfigSyncPacket(String fileName, byte[] fileData) {
        this.fileName = fileName;
        this.fileData = fileData;
    }

/*    public static List<Pair<String, ConfigSyncPacket>> generatePackets(boolean isLocal) {
        Map<String, byte[]> configData = tracker.configSets().get(ModConfig.Type.SERVER).stream().collect(Collectors.toMap(ModConfig::getFileName, mc -> {
            try {
                return mc.getConfigData() == null ? new byte[0] : Files.readAllBytes(mc.getFullPath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }));
        return configData.entrySet().stream().map(e-> {
            return Pair.of("config_" + e.getKey(), new ConfigSyncPacket(e.getKey(), e.getValue()));
        }).collect(Collectors.toList());
    }*/


    @Override
    public void serverHandle(C2SNetworkContext context) {
    }

    @Override
    public void clientHandle(S2CNetworkContext context) {
        if (!Minecraft.getInstance().isLocalServer()) {
            Optional.ofNullable(ConfigTracker.fileMap().get(fileName)).ifPresent(mc -> {
                mc.acceptSyncedConfig(fileData);
            });
        }
    }

    @Override
    public void encoder(PacketContent content) {
        try {
            CompoundTag tag = new CompoundTag();
            tag.putByteArray(fileName,fileData);
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            try {
                NbtIo.writeCompressed(tag, byteArrayOutputStream);
                content.writeByteArray(byteArrayOutputStream.toByteArray());
            } catch (IOException e) {
                BrickLibAPI.LOGGER.error(e.toString());
                content.writeByteArray(new byte[0]);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
