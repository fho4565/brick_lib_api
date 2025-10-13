package com.arc_studio.brick_lib_api;

import com.arc_studio.brick_lib_api.client.command.ClientCommands;
import com.arc_studio.brick_lib_api.core.PlatformInfo;
import com.arc_studio.brick_lib_api.core.Version;
import com.arc_studio.brick_lib_api.core.data.WorldAdditionalData;
import com.arc_studio.brick_lib_api.core.network.BuiltInPacket;
import com.arc_studio.brick_lib_api.core.network.type.PacketConfig;
import com.arc_studio.brick_lib_api.core.register.BrickRegisterManager;
import com.arc_studio.brick_lib_api.config.*;
import com.arc_studio.brick_lib_api.datagen.BrickDataGenerator;
import com.arc_studio.brick_lib_api.datagen.BrickDataProvider;
import com.arc_studio.brick_lib_api.datagen.DataGenerateEntry;
import com.arc_studio.brick_lib_api.datagen.PathProvider;
import com.arc_studio.brick_lib_api.network.ConfigSyncPacket;
import com.arc_studio.brick_lib_api.network.DemoReplyPacket;
import com.arc_studio.brick_lib_api.network.LoginPacketDemo;
import com.arc_studio.brick_lib_api.platform.Platform;
import com.arc_studio.brick_lib_api.register.BrickRegistries;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.llamalad7.mixinextras.MixinExtrasBootstrap;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
//? if <=1.18.2 {
/*import net.minecraft.network.chat.TextComponent;
*///?}
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.apache.commons.lang3.tuple.Pair;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author fho4565
 */
public final class BrickLibAPI {
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MOD_ID = "brick_lib_api";
    public static final Version BRICK_LIB_API_VERSION = new Version.Builder(1, 0, 0)
            .preRelease(Version.PreReleaseType.BETA).build();

    public static void init() {
        MixinExtrasBootstrap.init();
        Constants.initGeneral();
        BrickLibAPI.LOGGER.info("You are using Brick Lib API version {}", BRICK_LIB_API_VERSION);
        preLoad();
        test();
        brickLibApiFinalize();
    }

    private static void test() {
        BrickRegisterManager.register(BrickRegistries.COMMAND,()->buildContext->Commands.literal("dataTest")
            .then(Commands.argument("str", StringArgumentType.greedyString())
                .executes(context -> {
                    String str = StringArgumentType.getString(context, "str");
                    WorldAdditionalData.getWorldData().getData().putString("str",str);
                    return 1;
                })
            )
            .executes(context -> {
                context.getSource().sendSuccess(
                    //? if > 1.19.4 {
                    () ->
                    //?}
                    //? if > 1.18.2 {
                    Component.literal(WorldAdditionalData.getWorldData().getData().toString()),
                    //?} else {
                    /*new TextComponent(WorldAdditionalData.getWorldData().getData().toString()),
                    *///?}
                    true);
                return 1;
            })
        );
        BrickRegisterManager.register(BrickRegistries.CLIENT_COMMAND,()->context-> ClientCommands.literal("cl").executes(context1 -> {
            ClientCommands.sendFeedback(Component.nullToEmpty("yes!!!"));
            ClientCommands.sendError(Component.nullToEmpty("no!!!"));
            return 1;
        }));
        BrickRegisterManager.register(BrickRegistries.ITEM,ofPath("man_item"), ()->new Item(new Item.Properties()));
        BrickRegisterManager.register(BrickRegistries.ITEM,ofPath("very_man_item"), ()->new Item(new Item.Properties().rarity(Rarity.RARE)));
        //? if <1.20.4 {
        BrickRegisterManager.register(BrickRegistries.BLOCK, ofPath("man_block"), () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIRT)));
        BrickRegisterManager.register(BrickRegistries.BLOCK, ofPath("very_man_block"), () -> new Block(BlockBehaviour.Properties.copy(Blocks.DIAMOND_BLOCK)));
        //?} else {
        /*BrickRegisterManager.register(BrickRegistries.BLOCK, ofPath("man_block"), () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DIRT)));
        BrickRegisterManager.register(BrickRegistries.BLOCK, ofPath("very_man_block"), () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.DIAMOND_BLOCK)));*/
        //?}

        BrickConfigSpec.Builder builder = new BrickConfigSpec.Builder();
        builder.define("is_man",false);
        builder.define("man_message", "I'm man!");
        BrickRegisterManager.register(BrickRegistries.CONFIG,()->new ModConfig(ModConfig.Type.SERVER,builder.build(),ModConfig.defaultConfigName(ModConfig.Type.SERVER,MOD_ID),MOD_ID));

        BrickRegisterManager.register(BrickRegistries.NETWORK_PACKET, () -> new PacketConfig.Login<>(
            LoginPacketDemo.class,
            LoginPacketDemo::encoder,
            LoginPacketDemo::new,
            LoginPacketDemo::new,
            LoginPacketDemo::serverHandle,
            LoginPacketDemo::clientHandle,
            aBoolean -> {
                ArrayList<Pair<String, LoginPacketDemo>> list = new ArrayList<>();
                for (int i = 0; i < 30; i++) {
                    list.add(Pair.of("p"+i,new LoginPacketDemo("message"+i)));
                }
                return list;
            }
        ));

        BrickRegisterManager.register(BrickRegistries.DATA_GENERATE, () -> new DataGenerateEntry(
            PlatformInfo.of().setClient().setServer(),
            paths -> output -> new BrickDataProvider(output) {
                @Override
                public List<Pair<JsonElement, Path>> contents(PathProvider output) {
                    JsonObject object = new JsonObject();
                    object.add("man",new JsonPrimitive("out"));
                    return List.of(Pair.of(object,output.json(ofPath("man_file"))));
                }

                @Override
                public BrickDataGenerator.TargetType type() {
                    return BrickDataGenerator.TargetType.DATA_PACK;
                }

                @Override
                public String registerName() {
                    return "abc";
                }
            }
        ));
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

    public static ResourceLocation ofPath(String path) {
        //? if >= 1.21 {
        /*return ResourceLocation.fromNamespaceAndPath(MOD_ID,path);
         *///?} else {
        return new ResourceLocation(MOD_ID,path);
        //?}
    }
}

