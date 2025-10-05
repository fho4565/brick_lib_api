package com.arc_studio.brick_lib_api;

import com.arc_studio.brick_lib_api.core.Version;
import com.arc_studio.brick_lib_api.core.network.BuiltInPacket;
import com.arc_studio.brick_lib_api.core.network.type.PacketConfig;
import com.arc_studio.brick_lib_api.core.register.BrickRegisterManager;
import com.arc_studio.brick_lib_api.config.*;
import com.arc_studio.brick_lib_api.datagen.BrickDataGenerator;
import com.arc_studio.brick_lib_api.network.ConfigSyncPacket;
import com.arc_studio.brick_lib_api.platform.Platform;
import com.arc_studio.brick_lib_api.register.BrickRegistries;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fho4565
 */
public final class BrickLibAPI {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "brick_lib_api";
    public static final Version BRICK_LIB_API_VERSION = new Version.Builder(1, 0, 0).preRelease(Version.PreReleaseType.ALPHA,1).build();

    public static void init() {
        MixinExtrasBootstrap.init();
        Constants.initGeneral();
        BrickLibAPI.LOGGER.info("You are using Brick Lib version {}", BRICK_LIB_API_VERSION);
        preLoad();
        brickLibApiFinalize();
    }

    /**
     * 执行Brick Lib API后置工作，在不同平台上此方法会执行不同操作
     * <p color = "red">此方法必须被调用，且必须在模组入口点或者构造方法的最后调用！</p>
     * */
    public static void brickLibApiFinalize(){
        Platform.brickFinalizeRegistry();
    }

    private static void preLoad() {
        BrickRegisterManager.register(BrickRegistries.COMMAND, () -> buildContext ->
                Commands.literal("datagen")
                        .requires(stack -> Constants.isInDevelopEnvironment())
                        .executes(context -> genData(context, true, true))
                        .then(Commands.argument("genClient", BoolArgumentType.bool())
                                .executes(context -> genData(context, BoolArgumentType.getBool(context,"genClient"), true))
                                .then(Commands.argument("genServer", BoolArgumentType.bool())
                                        .executes(context -> genData(context,
                                                BoolArgumentType.getBool(context,"genClient"),
                                                BoolArgumentType.getBool(context,"genServer")))
                                )
                        )
        );
        initPackets();
    }

    private static void initPackets() {
        BrickRegisterManager.register(BrickRegistries.NETWORK_PACKET,
                BrickLibAPI.ofPath("built_in_packet"),
                () -> new PacketConfig.SAC<>(BuiltInPacket.class,
                        BuiltInPacket::encoder,
                        BuiltInPacket::new,
                        BuiltInPacket::serverHandle,
                        BuiltInPacket::clientHandle,
                        false,
                        false
                )
        );
        BrickRegistries.NETWORK_PACKET.register(BrickLibAPI.ofPath("config_sync_packet"),() ->
                new PacketConfig.Login<>(
                        ConfigSyncPacket.class,
                        ConfigSyncPacket::encoder,
                        ConfigSyncPacket::new,
                        ConfigSyncPacket::new,
                        ConfigSyncPacket::serverHandle,
                        ConfigSyncPacket::clientHandle,
                        isLocal -> {
                            Map<String, byte[]> configData = ConfigTracker.configSets().get(ModConfig.Type.SERVER).stream().collect(Collectors.toMap(ModConfig::getFileName, mc -> {
                                try {
                                    return Platform.isClient() || mc.getConfigData() == null ? new byte[0] : Files.readAllBytes(mc.getFullPath());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }));
                            return configData.entrySet().stream().map(e-> {
                                return Pair.of("config_" + e.getKey(), new ConfigSyncPacket(e.getKey(), e.getValue()));
                            }).collect(Collectors.toList());
                        }
                )
        );
    }

    private static int genData(CommandContext<CommandSourceStack> context, boolean client, boolean server) {
        try {
            BrickDataGenerator.run(client, server);
        } catch (IOException e) {
            context.getSource().sendFailure(Component.literal("Error when generate data").withStyle(style -> style.withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,Component.literal(e.getMessage())))));
            return 0;
        }
        return 1;
    }

    public static ResourceLocation ofPath(String path) {
        //? if >= 1.21 {
        /*return ResourceLocation.fromNamespaceAndPath(MOD_ID,path);
         *///?} else {
        return new ResourceLocation(MOD_ID,path);
        //?}
    }
}

