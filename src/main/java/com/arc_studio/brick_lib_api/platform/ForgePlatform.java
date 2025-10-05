package com.arc_studio.brick_lib_api.platform;



//? if (forge) || oldnf {
import com.arc_studio.brick_lib_api.BrickLibAPI;
import com.arc_studio.brick_lib_api.core.network.PacketContent;
import com.arc_studio.brick_lib_api.core.network.context.C2SNetworkContext;
import com.arc_studio.brick_lib_api.core.network.context.S2CNetworkContext;
import com.arc_studio.brick_lib_api.core.network.type.*;
import com.arc_studio.brick_lib_api.network.DemoReplyPacket;
import com.arc_studio.brick_lib_api.register.BrickRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.*;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
//? if forge && < 1.20.4 {
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.simple.SimpleChannel;
//?}

//? if forge && >=1.20.4 {
/*import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.network.*;
import net.minecraftforge.network.config.ConfigurationTaskContext;
import net.minecraftforge.network.config.SimpleConfigurationTask;
import net.minecraftforge.network.packets.LoginWrapper;
import org.apache.commons.lang3.function.TriConsumer;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.ApiStatus;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.event.network.GatherLoginConfigurationTasksEvent;
*///?}

//? if neoforge {
/*import net.neoforged.bus.core.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.events.lifecycle.FMLCommonSetupEvent;
*///?}

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)

//?}
@SuppressWarnings({"unchecked", "rawtypes"})
public class ForgePlatform {
    //? if (forge) || oldnf {
    protected static final String PROTOCOL_VERSION = "0";
    public static final SimpleChannel c2sPlayChannel = //? if < 1.20.4 {
    NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BrickLibAPI.MOD_ID, "c2s_play"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    //?} else {
    /*ChannelBuilder.named(BrickLibAPI.ofPath("c2s_play"))
            .optional()
            .networkProtocolVersion(1)
            .serverAcceptedVersions(Channel.VersionTest.exact(1))
            .clientAcceptedVersions(Channel.VersionTest.exact(1))
            .simpleChannel();
    *///?}

    public static final SimpleChannel c2sLoginChannel = //? if < 1.20.4 {
            NetworkRegistry.newSimpleChannel(
                    new ResourceLocation(BrickLibAPI.MOD_ID, "c2s_login"),
                    () -> PROTOCOL_VERSION,
                    PROTOCOL_VERSION::equals,
                    PROTOCOL_VERSION::equals
            );
    //?} else {
    /*ChannelBuilder.named(BrickLibAPI.ofPath("c2s_login"))
            .optional()
            .networkProtocolVersion(1)
            .serverAcceptedVersions(Channel.VersionTest.exact(1))
            .clientAcceptedVersions(Channel.VersionTest.exact(1))
            .simpleChannel();
    *///?}

    public static final SimpleChannel s2cPlayChannel = //? if <1.20.4 {
    NetworkRegistry.newSimpleChannel(
            new ResourceLocation(BrickLibAPI.MOD_ID, "s2c_play"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );
    //?} else {
            /*ChannelBuilder.named(BrickLibAPI.ofPath("s2c_play"))
                    .optional()
                    .networkProtocolVersion(1)
                    .serverAcceptedVersions(Channel.VersionTest.exact(1))
                    .clientAcceptedVersions(Channel.VersionTest.exact(1))
                    .simpleChannel();
    *///?}

    public static final SimpleChannel s2cLoginChannel = //? if <1.20.4 {
            NetworkRegistry.newSimpleChannel(
                    new ResourceLocation(BrickLibAPI.MOD_ID, "s2c_login"),
                    () -> PROTOCOL_VERSION,
                    PROTOCOL_VERSION::equals,
                    PROTOCOL_VERSION::equals
            );
    //?} else {
            /*ChannelBuilder.named(BrickLibAPI.ofPath("s2c_login"))
                    .optional()
                    .networkProtocolVersion(1)
                    .serverAcceptedVersions(Channel.VersionTest.exact(1))
                    .clientAcceptedVersions(Channel.VersionTest.exact(1))
                    .simpleChannel();
    *///?}

    protected static final AtomicInteger c2sID = new AtomicInteger(0);
    protected static final AtomicInteger s2cID = new AtomicInteger(0);

    @SubscribeEvent
    public static void onNetwork(FMLCommonSetupEvent event) {
        BrickRegistries.NETWORK_PACKET.foreachRegisteredValue(packetConfig -> {
            if (packetConfig instanceof PacketConfig.C2S c2S) {
                SimpleChannel.MessageBuilder<? extends C2SPacket> command = c2sPlayChannel
                        .messageBuilder(c2S.type(), c2sID.getAndIncrement(), NetworkDirection.PLAY_TO_SERVER)
                        .encoder((msg, buf) -> c2S.encoder().accept(msg, new PacketContent((FriendlyByteBuf) buf)))
                        .decoder(buf -> c2S.decoder().apply(new PacketContent((FriendlyByteBuf) buf)));
                if (c2S.netHandle()) {
                    command.consumerNetworkThread((msg, contextSupplier) -> {
                        c2S.packetHandler().accept(msg, new C2SNetworkContext(/*? <1.20.4 {*/ contextSupplier.get().getSender() /*?} else {*//*contextSupplier.getSender()*//*?}*/));
                    });
                } else {
                    command.consumerMainThread((msg, contextSupplier) -> c2S.packetHandler().accept(msg, new C2SNetworkContext(/*? <1.20.4 {*/ contextSupplier.get().getSender() /*?} else {*//*contextSupplier.getSender()*//*?}*/)));
                }
                command.add();
            }
            else if (packetConfig instanceof PacketConfig.S2C s2C) {
                SimpleChannel.MessageBuilder<? extends S2CPacket> command = s2cPlayChannel
                        .messageBuilder(s2C.type(), s2cID.getAndIncrement(), NetworkDirection.PLAY_TO_CLIENT)
                        .encoder((msg, buf) -> s2C.encoder().accept(msg, new PacketContent((FriendlyByteBuf) buf)))
                        .decoder(buf -> s2C.decoder().apply(new PacketContent((FriendlyByteBuf) buf)));
                if (s2C.netHandle()) {
                    command.consumerNetworkThread((msg, contextSupplier) -> {
                        s2C.packetHandler().accept(msg, new S2CNetworkContext());
                    });
                } else {
                    command.consumerMainThread((msg, contextSupplier) -> {
                        s2C.packetHandler().accept(msg, new S2CNetworkContext());
                    });
                }
                command.add();
            }
            else if (packetConfig instanceof PacketConfig.SAC sac) {
                SimpleChannel.MessageBuilder<? extends SACPacket> s2cBuilder = s2cPlayChannel
                        .messageBuilder(sac.type(), s2cID.getAndIncrement(), NetworkDirection.PLAY_TO_CLIENT)
                        .encoder((msg, buf) -> sac.encoder().accept(msg, new PacketContent((FriendlyByteBuf) buf)))
                        .decoder(buf -> sac.decoder().apply(new PacketContent((FriendlyByteBuf) buf)));
                if (sac.netHandle()) {
                    s2cBuilder.consumerNetworkThread((msg, contextSupplier) -> {
                        sac.clientHandler().accept(msg, new S2CNetworkContext());
                    });
                } else {
                    s2cBuilder.consumerMainThread((msg, contextSupplier) -> {
                        sac.clientHandler().accept(msg, new S2CNetworkContext());
                    });
                }
                s2cBuilder.add();
                SimpleChannel.MessageBuilder<? extends SACPacket> c2sBuilder = c2sPlayChannel
                        .messageBuilder(sac.type(), c2sID.getAndIncrement(), NetworkDirection.PLAY_TO_SERVER)
                        .encoder((msg, buf) -> sac.encoder().accept(msg, new PacketContent((FriendlyByteBuf) buf)))
                        .decoder(buf -> sac.decoder().apply(new PacketContent((FriendlyByteBuf) buf)));
                if (sac.netHandle()) {
                    c2sBuilder.consumerNetworkThread((msg, contextSupplier) -> {
                        sac.serverHandler().accept(msg, new C2SNetworkContext(/*? <1.20.4 {*/ contextSupplier.get().getSender() /*?} else {*//*contextSupplier.getSender()*//*?}*/));
                    });
                } else {
                    c2sBuilder.consumerMainThread((msg, contextSupplier) -> sac.serverHandler().accept(msg, new C2SNetworkContext(/*? <1.20.4 {*/ contextSupplier.get().getSender() /*?} else {*//*contextSupplier.getSender()*//*?}*/)));
                }
                c2sBuilder.add();
            }
            else if (packetConfig instanceof PacketConfig.Login login) {
                //? if < 1.20.4 {
                if(DemoReplyPacket.class.isAssignableFrom(login.type())){
                    SimpleChannel.MessageBuilder<? extends LoginPacket> c2sBuilder = c2sLoginChannel
                            .messageBuilder(login.type(), c2sID.getAndIncrement(), NetworkDirection.LOGIN_TO_SERVER)
                            .encoder((o, o2) -> login.encoder().accept(o,new PacketContent((FriendlyByteBuf) o2)))
                            .decoder(buf -> login.c2sDecoder().apply(new PacketContent((FriendlyByteBuf) buf)));
                    c2sBuilder.loginIndex(LoginPacket::getLoginIndex, LoginPacket::setLoginIndex)
                            .buildLoginPacketList(login.packetGenerator());
                    c2sBuilder.consumerNetworkThread(HandshakeHandler.indexFirst((handshakeHandler, intSupplier, supplier) -> {
                        supplier.get().setPacketHandled(true);
                    })).add();
                }
                SimpleChannel.MessageBuilder<? extends LoginPacket> s2cBuilder = s2cLoginChannel
                        .messageBuilder(login.type(), s2cID.getAndIncrement(), NetworkDirection.LOGIN_TO_CLIENT)
                        .encoder((msg, buf) -> login.encoder().accept(msg,new PacketContent((FriendlyByteBuf) buf)))
                        .decoder( buf -> login.s2cDecoder().apply(new PacketContent((FriendlyByteBuf) buf)));
                s2cBuilder.loginIndex(LoginPacket::getLoginIndex, LoginPacket::setLoginIndex)
                        .buildLoginPacketList(login.packetGenerator());
                s2cBuilder.consumerMainThread((s2CLoginPacket, contextSupplier) -> {
                    login.clientHandler().accept(s2CLoginPacket,new S2CNetworkContext());
                    c2sLoginChannel.reply(new DemoReplyPacket(),contextSupplier.get());
                }).add();
                //?} else {
                /*loginPacket(login);
                *///?}
            }

        });
    }

    //? if >= 1.20.4 {
    /*private static<T extends LoginPacket> void loginPacket(PacketConfig.Login<T> login){
        if(DemoReplyPacket.class.isAssignableFrom(login.type())){
            c2sLoginChannel.messageBuilder(login.type(),NetworkDirection.PLAY_TO_SERVER)
                    .encoder((t, buf) -> login.encoder().accept(t,new PacketContent(buf)))
                    .decoder(buf -> login.c2sDecoder().apply(new PacketContent(buf)))
                    .consumerNetworkThread((v, s) -> {
                        login.serverHandler().accept(v,new C2SNetworkContext(s.getSender()));
                    })
                    .add();
        }
        s2cLoginChannel.messageBuilder(login.type(),NetworkDirection.PLAY_TO_CLIENT)
                .encoder((t, buf) -> login.encoder().accept(t,new PacketContent(buf)))
                .decoder(buf -> login.s2cDecoder().apply(new PacketContent(buf)))
                .consumerNetworkThread(( v, s) -> {
                    login.clientHandler().accept(v,new S2CNetworkContext());
                })
                .add();
    }

    @ApiStatus.Internal
    public static class InternalEventClass {
        @SubscribeEvent
        public void onGatherLoginConfigurationTasks(GatherLoginConfigurationTasksEvent event) {
            event.addTask(new SimpleConfigurationTask(new ConfigurationTask.Type("brick_login_packet"), cts ->
                    BrickRegistries.NETWORK_PACKET.forEach(packetConfig -> {
                if(packetConfig instanceof PacketConfig.Login login){
                    List<Pair<String, ? extends LoginPacket>> list = (List<Pair<String, ? extends LoginPacket>>) login.packetGenerator().apply(false);
                    list.forEach(stringPair -> {
                        s2cLoginChannel.send(stringPair.getRight(), cts.getConnection());
                    });
                }
            })));
        }
    }

    *///?}
    //?}
}
