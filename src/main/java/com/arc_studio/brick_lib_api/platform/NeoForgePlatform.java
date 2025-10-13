package com.arc_studio.brick_lib_api.platform;


//? if neoforge {
/*import com.arc_studio.brick_lib_api.BrickLibAPI;
import com.arc_studio.brick_lib_api.core.network.PacketContent;
import com.arc_studio.brick_lib_api.core.network.context.C2SNetworkContext;
import com.arc_studio.brick_lib_api.core.network.context.S2CNetworkContext;
import com.arc_studio.brick_lib_api.core.network.type.*;
import com.arc_studio.brick_lib_api.network.DemoReplyPacket;
import com.arc_studio.brick_lib_api.register.BrickRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;

//? if < 1.20.4 {
//?} else if <1.20.6 {
/^import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.OnGameConfigurationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlerEvent;
import net.neoforged.neoforge.network.registration.IPayloadRegistrar;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Consumer;

^///?} else {
/^import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import net.neoforged.neoforge.network.event.RegisterConfigurationTasksEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.fml.common.EventBusSubscriber;
import net.minecraft.network.codec.StreamCodec;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.network.registration.HandlerThread;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;
import java.util.function.Consumer;

^///?}
//? if < 1.20.6 {
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
//?} else {
/^@EventBusSubscriber(
        //? if < 1.21.1 {
        bus = EventBusSubscriber.Bus.MOD
        //?}
)
^///?}
@SuppressWarnings({"unchecked", "rawtypes"})
public class NeoForgePlatform {
    //? if neoforge {
    /^@SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCommonSetup(
            /^¹? >1.20.4 {¹^/ /^¹RegisterPayloadHandlersEvent ¹^//^¹?} else {¹^/RegisterPayloadHandlerEvent/^¹?}¹^/
                    events) {
        /^¹? >1.20.4 {¹^/ /^¹PayloadRegistrar ¹^//^¹?} else {¹^/
        IPayloadRegistrar/^¹?}¹^/ registrar = events.registrar(BrickLibAPI.MOD_ID);
        BrickRegistries.NETWORK_PACKET.foreachRegisteredValue(packetConfig -> {
            if (packetConfig instanceof PacketConfig.C2S c2S) {
                c2s(registrar, c2S);
            } else if (packetConfig instanceof PacketConfig.S2C s2C) {
                s2c(registrar, s2C);
            } else if (packetConfig instanceof PacketConfig.SAC sac) {
                sac(registrar, sac);
            } else if (packetConfig instanceof PacketConfig.Login s2C) {
                login(registrar, s2C);
            }
        });

    }

    private static <T extends C2SPacket> void c2s(/^¹? >=1.20.6 {¹^/ /^¹PayloadRegistrar ¹^//^¹?} else {¹^/IPayloadRegistrar/^¹?}¹^/ registrar, PacketConfig.C2S<T> c2S) {
        //? if > 1.20.4 {
        /^¹StreamCodec<RegistryFriendlyByteBuf, T> codec = StreamCodec.of(
                (buf, packet) -> c2S.encoder().accept(packet, new PacketContent(buf)),
                buf -> c2S.decoder().apply(new PacketContent(buf)));
        CustomPacketPayload.Type<T> type = new CustomPacketPayload.Type<>(c2S.id());
        registrar.executesOn(c2S.netHandle() ? HandlerThread.NETWORK : HandlerThread.MAIN).playToServer(
                type,
                codec,
                (packet, context) -> {
                    c2S.packetHandler().accept(packet, new C2SNetworkContext((ServerPlayer) context.player()));
                }
        );
        ¹^///?} else {
        registrar.play(c2S.id(),
                buf -> c2S.decoder().apply(new PacketContent(buf)),
                (arg, playPayloadContext) -> {
                    if (c2S.netHandle()) {
                        c2S.packetHandler().accept(arg, new C2SNetworkContext((ServerPlayer) playPayloadContext.player().get()));
                    }else {
                        playPayloadContext.workHandler().submitAsync(() -> c2S.packetHandler().accept(arg, new C2SNetworkContext((ServerPlayer) playPayloadContext.player().get()))).exceptionally(throwable -> {
                            BrickLibAPI.LOGGER.error(throwable.getMessage());
                            return null;
                        });
                    }
                });
        //?}
    }

    private static <T extends S2CPacket> void s2c(/^¹? >=1.20.6 {¹^/ /^¹PayloadRegistrar ¹^//^¹?} else {¹^/IPayloadRegistrar/^¹?}¹^/ registrar, PacketConfig.S2C<T> s2C) {
        //? if > 1.20.4 {
        /^¹StreamCodec<RegistryFriendlyByteBuf, T> codec = StreamCodec.of(
                (buf, packet) -> s2C.encoder().accept(packet, new PacketContent(buf)),
                buf -> s2C.decoder().apply(new PacketContent(buf)));
        CustomPacketPayload.Type<T> type = new CustomPacketPayload.Type<>(s2C.id());
        registrar.executesOn(s2C.netHandle() ? HandlerThread.NETWORK : HandlerThread.MAIN).playToClient(
                type,
                codec,
                (packet, context) -> {
                    s2C.packetHandler().accept(packet, new S2CNetworkContext());
                }
        );
        ¹^///?} else {
        registrar.play(s2C.id(),
                buf -> s2C.decoder().apply(new PacketContent(buf)),
                (arg, playPayloadContext) -> {
                    if (s2C.netHandle()) {
                        s2C.packetHandler().accept(arg, new S2CNetworkContext());
                    }else {
                        playPayloadContext.workHandler().submitAsync(()-> s2C.packetHandler().accept(arg, new S2CNetworkContext())).exceptionally(throwable -> {
                            BrickLibAPI.LOGGER.error(throwable.getMessage());
                            return null;
                        });
                    }
                });
        //?}
    }

    private static <T extends SACPacket> void sac(/^¹? >=1.20.6 {¹^/ /^¹PayloadRegistrar ¹^//^¹?} else {¹^/IPayloadRegistrar/^¹?}¹^/ registrar, PacketConfig.SAC<T> sAC) {
        //? if > 1.20.4 {
        /^¹StreamCodec<RegistryFriendlyByteBuf, T> codec = StreamCodec.of(
                (buf, packet) -> sAC.encoder().accept(packet, new PacketContent(buf)),
                buf -> sAC.decoder().apply(new PacketContent(buf)));
        CustomPacketPayload.Type<T> sacT = new CustomPacketPayload.Type<>(sAC.s2cID());
        registrar.executesOn(sAC.clientNetHandle() ? HandlerThread.NETWORK : HandlerThread.MAIN).playBidirectional(
                sacT,
                codec,
                new DirectionalPayloadHandler<>(
                        (packet, context) -> sAC.clientHandler().accept(packet, new S2CNetworkContext()),
                        (packet, context) -> sAC.serverHandler().accept(packet, new C2SNetworkContext((ServerPlayer) context.player()))
                )
        );
        ¹^///?} else {
            registrar.play(sAC.id(),
                    buf -> sAC.decoder().apply(new PacketContent(buf)),
                    handler -> handler
                            .client((arg, playPayloadContext) -> {
                                if (sAC.clientNetHandle()) {
                                    sAC.clientHandler().accept(arg,new S2CNetworkContext());
                                }else {
                                    playPayloadContext.workHandler().submitAsync(() -> sAC.clientHandler().accept(arg,new S2CNetworkContext()))
                                            .exceptionally(throwable -> {
                                        BrickLibAPI.LOGGER.error(throwable.getMessage());
                                        return null;
                                    });
                                }
                            })
                            .server((arg, playPayloadContext) -> {
                                if (sAC.serverNetHandle()) {
                                    sAC.serverHandler().accept(arg,new C2SNetworkContext((ServerPlayer) playPayloadContext.player().get()));
                                }else {
                                    playPayloadContext.workHandler().submitAsync(() -> sAC.serverHandler().accept(arg,new C2SNetworkContext((ServerPlayer) playPayloadContext.player().get())))
                                            .exceptionally(throwable -> {
                                        BrickLibAPI.LOGGER.error(throwable.getMessage());
                                        return null;
                                    });
                                }
                            })
            );
            //?}
    }

    private static <T extends LoginPacket> void login(/^¹? >=1.20.6 {¹^/ /^¹PayloadRegistrar ¹^//^¹?} else {¹^/IPayloadRegistrar/^¹?}¹^/ registrar, PacketConfig.Login<T> login) {
        //? if > 1.20.4 {
        /^¹StreamCodec<FriendlyByteBuf, T> codec = StreamCodec.of(
                (buf, packet) -> login.encoder().accept(packet, new PacketContent(buf)),
                buf -> login.decoder().apply(new PacketContent(buf)));
        CustomPacketPayload.Type<T> type = new CustomPacketPayload.Type<>(login.id());
        registrar.configurationToClient(
                type,
                codec,
                (arg, iPayloadContext) -> login.clientHandler().accept(arg, new S2CNetworkContext())
        );
        ¹^///?} else {
        registrar.common(login.id(),
                buf -> login.decoder().apply(new PacketContent(buf)),
                (arg, playPayloadContext) -> playPayloadContext.workHandler().submitAsync(()-> login.clientHandler().accept(arg, new S2CNetworkContext())).exceptionally(throwable -> {
                    BrickLibAPI.LOGGER.error(throwable.getMessage());
                    return null;
                }));
        
        //?}
    }

    //? if < 1.20.6 {
    @SubscribeEvent
    public static void onOnGameConfiguration(OnGameConfigurationEvent event) {
        for (ResourceLocation resourceLocation : BrickRegistries.NETWORK_PACKET.keySet()) {
            PacketConfig config = BrickRegistries.NETWORK_PACKET.get(resourceLocation);
            if(config != null){
                if (event.getListener().isConnected(config.id())) {
                    if (config instanceof PacketConfig.Login login) {
                        if(!DemoReplyPacket.class.isAssignableFrom(login.type())) {
                            event.register(new MyICustomConfigurationTask(login, event));
                        }
                    }
                }
            }
        }
    }
    //?} else {
    /^¹@SubscribeEvent
    public static void onRegisterConfigurationTasks(RegisterConfigurationTasksEvent event) {
        for (ResourceLocation resourceLocation : BrickRegistries.NETWORK_PACKET.keySet()) {
            PacketConfig config = BrickRegistries.NETWORK_PACKET.get(resourceLocation);
            if(config != null){
                if (event.getListener().hasChannel(config.id())) {
                    if (config instanceof PacketConfig.Login login) {
                        if(!DemoReplyPacket.class.isAssignableFrom(login.type())) {
                            event.register(new MyICustomConfigurationTask(login, event));
                        }
                    }
                }
            }
        }
    }

    ¹^///?}

    private static class MyICustomConfigurationTask implements ICustomConfigurationTask {
        private final PacketConfig.Login config;
        private final
        //? if < 1.20.6 {
        OnGameConfigurationEvent
        //?} else {
                /^¹RegisterConfigurationTasksEvent
                ¹^///?}
                event;

        public MyICustomConfigurationTask(PacketConfig.Login config,
                                          //? if < 1.20.6 {
                OnGameConfigurationEvent
                                          //?} else {
                                          /^¹RegisterConfigurationTasksEvent
                                                  ¹^///?}
                                          event) {
            this.config = config;
            this.event = event;
        }

        @Override
        public void run(Consumer<CustomPacketPayload> consumer) {
            List<Pair<String, ? extends LoginPacket>> list =
                    (List<Pair<String, ? extends LoginPacket>>) config.packetGenerator().apply(false);
            list.forEach(stringPair -> consumer.accept(stringPair.getRight()));
            event.getListener().finishCurrentTask(type());
        }

        @Override
        public Type type() {
            return new Type(BrickLibAPI.ofPath("brick_login_packet"));
        }
    }


    ^///?}
}
*/