package com.arc_studio.brick_lib_api;

import com.arc_studio.brick_lib_api.core.Version;
import com.arc_studio.brick_lib_api.core.event.BrickEventBus;
import com.arc_studio.brick_lib_api.core.event.EventListener;
import com.arc_studio.brick_lib_api.core.network.BrickNetwork;
import com.arc_studio.brick_lib_api.core.providers.blockpos.RandomSurfaceBlockPos;
import com.arc_studio.brick_lib_api.core.network.BuiltInPacket;
import com.arc_studio.brick_lib_api.core.network.type.PacketConfig;
import com.arc_studio.brick_lib_api.core.register.BrickRegisterManager;
import com.arc_studio.brick_lib_api.config.*;
import com.arc_studio.brick_lib_api.datagen.BrickDataGenerator;
import com.arc_studio.brick_lib_api.events.NetworkMessageEvent;
import com.arc_studio.brick_lib_api.network.ConfigSyncPacket;
import com.arc_studio.brick_lib_api.platform.Platform;
import com.arc_studio.brick_lib_api.register.BrickRegistries;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.logging.LogUtils;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.function.Function;
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
        test();
        brickLibApiFinalize();
    }

    /**
     * 执行Brick Lib API后置工作，在不同平台上此方法会执行不同操作
     * <p color = "red">此方法必须被调用，且必须在模组入口点或者构造方法的最后调用！</p>
     * */
    public static void brickLibApiFinalize(){
        Platform.brickFinalizeRegistry();
    }

    private static void test() {
        BrickRegisterManager.register(BuiltInRegistries.ITEM,ofPath("man_item"),()->new Item(new Item.Properties().rarity(Rarity.EPIC)));
        BrickRegisterManager.register(BrickRegistries.COMMAND,() ->
                 commandBuildContext -> Commands.literal("surface")
                        .then(Commands.argument("pos1", BlockPosArgument.blockPos())
                                .then(Commands.argument("pos2",BlockPosArgument.blockPos())
                                        .executes(context -> {
                                            BlockPos pos1 = BlockPosArgument.getBlockPos(context, "pos1");
                                            BlockPos pos2 = BlockPosArgument.getBlockPos(context, "pos2");
                                            System.out.println("pos1 = " + pos1);
                                            System.out.println("pos2 = " + pos2);
                                            ServerLevel level = context.getSource().getLevel();
                                            AABB aabb =
                                                    //? if >= 1.20.4 {
                                                    /*AABB.encapsulatingFullBlocks(pos1, pos2)
                                                    *///?} else {
                                                    new AABB(pos1,pos2)
                                                    //?}
                                                    ;
                                            RandomSurfaceBlockPos pos = new RandomSurfaceBlockPos(level, aabb);
                                            level.setBlockAndUpdate(pos.sample(RandomSource.create()), Blocks.DIAMOND_BLOCK.defaultBlockState());
                                            pos.foreach(blockPos -> {
                                                System.out.println("blockPos = " + blockPos);
                                                level.setBlockAndUpdate(blockPos, Blocks.DIAMOND_BLOCK.defaultBlockState());
                                            });
                                            return 1;
                                        })
                                )
                        )
        );
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
                                    if (Platform.isClient() || mc.getConfigData() == null) {
                                        System.out.println("ConfigSyncPacket.generatePackets = NULL");
                                    }else{
                                        System.out.println("ConfigSyncPacket.generatePackets = YES");
                                    }
                                    return Platform.isClient() || mc.getConfigData() == null ? new byte[0] : Files.readAllBytes(mc.getFullPath());
                                } catch (IOException e) {
                                    throw new RuntimeException(e);
                                }
                            }));
                            return configData.entrySet().stream().map(e-> {
                                System.out.println("ConfigSyncPacket.generatePackets Name = config_" + e.getKey());
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

