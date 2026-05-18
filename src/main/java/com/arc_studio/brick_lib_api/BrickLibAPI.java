package com.arc_studio.brick_lib_api;

import com.arc_studio.brick_lib_api.core.Version;
import com.arc_studio.brick_lib_api.core.data.ResourceID;
import com.arc_studio.brick_lib_api.core.data.capability.builtin.example.FurnaceEnergyEvents;
import com.arc_studio.brick_lib_api.core.data.capability.builtin.example.StoneFluidEvents;
import com.arc_studio.brick_lib_api.core.data.capability.compat.CapabilityCompat;
import com.arc_studio.brick_lib_api.core.event.*;
import com.arc_studio.brick_lib_api.core.network.BuiltInPacket;
import com.arc_studio.brick_lib_api.core.network.type.PacketConfig;
import com.arc_studio.brick_lib_api.core.register.BrickRegisterManager;
import com.arc_studio.brick_lib_api.config.*;
import com.arc_studio.brick_lib_api.datagen.BrickDataGenerator;
import com.arc_studio.brick_lib_api.network.ConfigSyncPacket;
import com.arc_studio.brick_lib_api.network.DemoReplyPacket;
import com.arc_studio.brick_lib_api.platform.Platform;
import com.arc_studio.brick_lib_api.register.BrickRegistries;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.network.chat.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

//? if > 1.20.4 {
/*import net.minecraft.world.item.trading.ItemCost;
*///?}
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.apache.commons.lang3.tuple.Pair;


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
    public static final Version BRICK_LIB_API_VERSION = new Version.Builder(1, 0, 0)
            .preRelease(Version.PreReleaseType.BETA,6).build();
    public static final ArrayList<Class<?>> LIST = new ArrayList<>();

    public static void init() {
        MixinExtrasBootstrap.init();
        Constants.initGeneral();
        BrickLibAPI.LOGGER.info("Brick Lib API Version : {}", BRICK_LIB_API_VERSION);
        CapabilityCompat.initBuiltinMappings();
        StoneFluidEvents.register();
        FurnaceEnergyEvents.register();
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
                                                BoolArgumentType.getBool(context,"genServer")
                                            )
                                        )
                                )
                        )
        );
        BrickRegisterManager.register(BrickRegistries.COMMAND, () -> buildContext ->
                Commands.literal("brick_lib")
                        .requires(stack -> stack.hasPermission(2))
                        .then(
                            Commands.literal("config")
                                .then(Commands.literal("list")
                                    .executes(context->{
                                        findAndOutputConfigs(context, ModConfig.Type.COMMON);
                                        findAndOutputConfigs(context, ModConfig.Type.SERVER);
                                        findAndOutputConfigs(context, ModConfig.Type.CLIENT);
                                        return 1;
                                    })
                                    .then(Commands.literal("common")
                                        .executes(context->{
                                            findAndOutputConfigs(context, ModConfig.Type.COMMON);
                                            return 1;
                                        })
                                    )
                                    .then(Commands.literal("server")
                                        .executes(context->{
                                            findAndOutputConfigs(context, ModConfig.Type.SERVER);
                                            return 1;
                                        })
                                    )
                                    .then(Commands.literal("client")
                                        .executes(context->{
                                            findAndOutputConfigs(context, ModConfig.Type.CLIENT);
                                            return 1;
                                        })
                                    )
                                )
                        )
        );
        initPackets();
    }
    private static void findAndOutputConfigs(CommandContext<CommandSourceStack> context, ModConfig.Type type){
        //? if > 1.18.2 {
        //? if <= 1.19.4 {
        /*context.getSource().sendSuccess(Component.literal("=====[ "+type.extension()+" ]"), true);
        ConfigTracker.configSets().get(type).forEach((config) -> {
            context.getSource().sendSuccess(
                Component.literal("[" + config.getModId() + "] " + config.getFileName()), true);
        });
        *///?} else {

            context.getSource().sendSuccess(() -> Component.literal("=====[" + type.extension() + "]"), true);
            ConfigTracker.configSets().get(type).forEach((config) -> {
                context.getSource().sendSuccess(() -> Component.literal("[" + config.getModId() + "] " + config.getFileName()),
                    true);
            });
        //?}
        //?} else {

            /*context.getSource().sendSuccess(new TextComponent("=====[" + type.extension() + "]"),true);
            ConfigTracker.configSets().get(type).forEach((config) -> {
                context.getSource().sendSuccess(new TextComponent("[" + config.getModId() + "] " + config.getFileName()),
                    true);
            });
         *///?}
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
                            return configData.entrySet().stream().map(e-> Pair.of("config_" + e.getKey(), new ConfigSyncPacket(e.getKey(), e.getValue()))).collect(Collectors.toList());
                        }
                )
        );
        BrickRegistries.NETWORK_PACKET.register(BrickLibAPI.ofPath("login_reply_packet"),() ->
            new PacketConfig.Login<>(
                DemoReplyPacket.class,
                DemoReplyPacket::encoder,
                DemoReplyPacket::new,
                DemoReplyPacket::new,
                DemoReplyPacket::serverHandle,
                DemoReplyPacket::clientHandle,
                isLocal -> List.of()
            )
        );
    }

    private static int genData(CommandContext<CommandSourceStack> context, boolean client, boolean server) {
        try {
            BrickDataGenerator.run(client, server);
        } catch (IOException e) {
            //? if > 1.18.2 {
            context.getSource().sendFailure(Component.literal("Error when generate data").withStyle(style -> style.withHoverEvent(
                //? if < 1.21.5 {
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, Component.literal(e.getMessage()))
                //?} else {
                /*new HoverEvent.ShowText(Component.literal(e.getMessage()))
                 *///?}
            )));
            //?} else {
            /*context.getSource().sendFailure(new TextComponent("Error when generate data").withStyle(style -> style.withHoverEvent(
                //? if < 1.21.5 {
                new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(e.getMessage()))
                //?} else {
                /^new HoverEvent.ShowText(Component.literal(e.getMessage()))
                 ^///?}
            )));
            *///?}
            return 0;
        }
        return 1;
    }

    public static ResourceID ofPath(String path) {
        return new ResourceID(MOD_ID,path);
    }

}

