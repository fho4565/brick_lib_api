package com.arc_studio.brick_lib_api.platform;

//? if fabric {
/*import com.arc_studio.brick_lib_api.BrickLibAPI;
import com.arc_studio.brick_lib_api.Constants;
import com.arc_studio.brick_lib_api.core.PlatformInfo;
import com.arc_studio.brick_lib_api.core.SideExecutor;
import com.arc_studio.brick_lib_api.core.VillagerTradeEntry;
import com.arc_studio.brick_lib_api.core.data.capability.compat.CapabilityRegistration;
import com.arc_studio.brick_lib_api.core.data.capability.compat.FabricTransferAdapter;
import com.arc_studio.brick_lib_api.core.network.PacketContent;
import com.arc_studio.brick_lib_api.core.network.context.C2SNetworkContext;
import com.arc_studio.brick_lib_api.core.network.context.S2CNetworkContext;
import com.arc_studio.brick_lib_api.core.network.type.*;
import com.arc_studio.brick_lib_api.core.register.BrickRegisterManager;
import com.arc_studio.brick_lib_api.register.BrickRegistries;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientLoginNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.*;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
//? if >= 1.20.6 {
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
//?}
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
*///?}

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FabricPlatform {
    private static final Logger LOGGER = LoggerFactory.getLogger(FabricPlatform.class);

    //? if fabric {
    /*// ===========================
    //  Capability 注册
    // ===========================

    /^*
     * 注册 Fabric 能力查找（lookup）。
     * <p>
     * 由 {@link CapabilityRegistration#init()} 自动调用。
     * </p>
     ^/
    public static void registerCapabilityLookups() {
        for (var entry : CapabilityRegistration.getEnergyBlocks().values()) {
            team.reborn.energy.api.EnergyStorage.SIDED.registerForBlocks(
                    (world, pos, state, blockEntity, direction) -> {
                        if (!(world instanceof ServerLevel serverLevel)) {
                            return null;
                        }
                        var ucs = entry.provider().getEnergy(serverLevel, pos, state, blockEntity, direction);
                        if (ucs != null) {
                            return FabricTransferAdapter.wrapAsEnergyStorage(ucs,
                                    () -> { if (entry.dirtyNotifier() != null) entry.dirtyNotifier().accept(serverLevel, pos); });
                        }
                        return null;
                    },
                    entry.block()
            );
        }
        for (var entry : CapabilityRegistration.getFluidBlocks().values()) {
            net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage.SIDED.registerForBlocks(
                    (world, pos, state, blockEntity, direction) -> {
                        if (!(world instanceof ServerLevel serverLevel)) return null;
                        var ucs = entry.provider().getFluid(serverLevel, pos, state, blockEntity, direction);
                        if (ucs != null) {
                            return FabricTransferAdapter.wrapAsFluidStorage(ucs);
                        }
                        return null;
                    },
                    entry.block()
            );
        }
    }

    // ===========================
    //  平台信息
    // ===========================

    public static Path getConfigDirectory() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static Path versionPath() {
        return FabricLoader.getInstance().getGameDir();
    }

    public static boolean isDev() {
        return FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    public static String[] launchArgs() {
        return FabricLoader.getInstance().getLaunchArguments(true);
    }

    public static PlatformInfo platform() {
        PlatformInfo type = new PlatformInfo();
        type.setFabric();
        if (isClient()) {
            type.setClient();
        } else if (isServer()) {
            type.setServer();
        }
        return type;
    }

    public static boolean isClient() {
        return FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.CLIENT;
    }

    public static boolean isServer() {
        return FabricLoader.getInstance().getEnvironmentType() == net.fabricmc.api.EnvType.SERVER;
    }

    // ===========================
    //  网络
    // ===========================

    public static <T extends S2CNetworkContext> void enqueueWork(T context, Runnable runnable) {
        Minecraft.getInstance().execute(runnable);
    }

    public static <T extends C2SNetworkContext> void enqueueWork(T context, Runnable runnable) {
        MinecraftServer server = context.getSender().getServer();
        if (server != null) {
            server.execute(runnable);
        }
    }

    public static void sendToPlayer(ICHandlePacket packet, Iterable<ServerPlayer> serverPlayers) {
        ResourceLocation id = Optional.ofNullable(packet.id()).orElseGet(() -> BrickLibAPI.ofPath(packet.getClass().getName().replace(".", "_").toLowerCase() + "_s2c"));
        for (ServerPlayer serverPlayer : serverPlayers) {
            //? if < 1.20.6 {
            /^ServerPlayNetworking.send(serverPlayer, id, packet.getEncodedPacketContent(new PacketContent()).friendlyByteBuf());
            ^///?} else {
            ServerPlayNetworking.send(serverPlayer, packet);
            //?}
        }
    }

    public static void sendToServer(ISHandlePacket packet) {
        //? if < 1.20.6 {
        /^ResourceLocation id = Optional.ofNullable(packet.id()).orElseGet(() -> new ResourceLocation(BrickLibAPI.MOD_ID, packet.getClass().getName().replace(".", "_").toLowerCase() + "_c2s"));
        ClientPlayNetworking.send(id, packet.getEncodedPacketContent(new PacketContent()).friendlyByteBuf());
        ^///?} else {
        ClientPlayNetworking.send(packet);
        //?}
    }

    public static Set<ResourceLocation> networkChannels(Connection connection, ConnectionProtocol protocol) {
        return Set.of();
    }

    // ===========================
    //  ItemStack 比较
    // ===========================

    public static boolean itemEqual(ItemStack first, ItemStack second, boolean compareDamageValue) {
        if (first.isEmpty()) {
            return second.isEmpty();
        } else {
            if (!compareDamageValue) {
                ItemStack i1 = first.copy();
                ItemStack i2 = second.copy();
                i1.setDamageValue(0);
                i2.setDamageValue(0);
                return ItemStack.matches(i1, i2);
            } else {
                return ItemStack.matches(first, second);
            }
        }
    }

    // ===========================
    //  后置注册
    // ===========================

    public static void brickFinalizeRegistryPost() {
        //? if >= 1.20.6 {
        BrickRegistries.NETWORK_PACKET.foreachRegisteredValue(packetConfig -> {
            if (packetConfig instanceof PacketConfig.C2S c2S) {
                c2s(c2S);
            } else if (packetConfig instanceof PacketConfig.S2C s2C) {
                s2c(s2C);
            } else if (packetConfig instanceof PacketConfig.SAC sac) {
                sac(sac);
            } else if (packetConfig instanceof PacketConfig.Login login) {
                login(login);
            }
        });
        //?} else {
        /^BrickRegistries.NETWORK_PACKET.foreachRegisteredValue(packetConfig -> {
            if (packetConfig instanceof PacketConfig.C2S c2SPlay) {
                SideExecutor.runOnServer(() -> () -> ServerPlayNetworking.registerGlobalReceiver(c2SPlay.id(),
                        (server, player, handler, buf, responseSender) -> {
                    Object applied = c2SPlay.decoder().apply(new PacketContent(buf));
                    if (c2SPlay.netHandle()) {
                        c2SPlay.packetHandler().accept(applied, new C2SNetworkContext(player));
                    } else {
                        server.execute(() -> c2SPlay.packetHandler().accept(applied, new C2SNetworkContext(player)));
                    }
                }));
            }
            else if (packetConfig instanceof PacketConfig.S2C s2CPlay) {
                SideExecutor.runOnClient(() -> () -> ClientPlayNetworking.registerGlobalReceiver(s2CPlay.id(), (render, handler, buf, responseSender) -> {
                    Object applied = s2CPlay.decoder().apply(new PacketContent(buf));
                    if (s2CPlay.netHandle()) {
                        s2CPlay.packetHandler().accept(applied, new S2CNetworkContext());
                    } else {
                        render.execute(() -> s2CPlay.packetHandler().accept(applied, new S2CNetworkContext()));
                    }
                }));
            }
            else if (packetConfig instanceof PacketConfig.SAC sacPlay) {
                SideExecutor.runOnClient(() -> () -> {
                    ClientPlayNetworking.registerGlobalReceiver(sacPlay.s2cID(), (client, handler, buf, responseSender) -> {
                        Object applied = sacPlay.decoder().apply(new PacketContent(buf));
                        if (sacPlay.netHandle()) {
                            sacPlay.clientHandler().accept(applied, new S2CNetworkContext());
                        } else {
                            client.execute(() -> sacPlay.clientHandler().accept(applied, new S2CNetworkContext()));
                        }
                    });
                });
                ServerPlayNetworking.registerGlobalReceiver(sacPlay.c2sID(), (server, player, handler, buf, responseSender) -> {
                    try {
                        Object applied = sacPlay.decoder().apply(new PacketContent(buf));
                        if (sacPlay.netHandle()) {
                            sacPlay.serverHandler().accept(applied, new C2SNetworkContext(player));
                        } else {
                            server.execute(() -> sacPlay.serverHandler().accept(applied, new C2SNetworkContext(player)));
                        }
                    } catch (Exception e) {
                        LOGGER.error(e.toString());
                    }
                });
            }
            else if (packetConfig instanceof PacketConfig.Login s2CLogin) {
                List<Pair<String, ? extends LoginPacket>> apply0 = (List<Pair<String, ? extends LoginPacket>>) s2CLogin.packetGenerator().apply(false);
                apply0.forEach(stringPair -> {
                    final String path = stringPair.getLeft();
                    SideExecutor.runOnClient(()->()->{
                        ClientLoginNetworking.registerGlobalReceiver(BrickLibAPI.ofPath(path),
                                (client, handler1, buf, listenerAdder) -> {
                                    Object applied = s2CLogin.s2cDecoder().apply(new PacketContent(buf));
                                    client.execute(() -> s2CLogin.clientHandler().accept(applied, new S2CNetworkContext()));
                                    return CompletableFuture.completedFuture(PacketByteBufs.create());
                                });
                    });
                    ServerLoginNetworking.registerGlobalReceiver(BrickLibAPI.ofPath(path),
                            (server, handler, understood, buf, synchronizer, responseSender) -> {
                                if (!understood) {
                                    return;
                                }
                                Object applied = s2CLogin.c2sDecoder().apply(new PacketContent(buf));
                                server.execute(() -> s2CLogin.serverHandler().accept(applied, new C2SNetworkContext(null)));
                            });
                });
                ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
                    List<Pair<String, ? extends LoginPacket>> apply = (List<Pair<String, ? extends LoginPacket>>) s2CLogin.packetGenerator().apply(false);
                    apply.forEach(stringPair -> {
                        sender.sendPacket(BrickLibAPI.ofPath(stringPair.getLeft()), stringPair.getRight().getEncodedPacketContent(new PacketContent()).friendlyByteBuf());
                    });
                });
            }
        });
        ^///?}
        BrickRegisterManager.getVanillaEntries().forEach((registry, map2) ->
                map2.forEach((resourceLocation, supplier) -> {
            if (!registry.containsKey(resourceLocation)) {
                Registry.register((Registry<Object>) registry, resourceLocation, supplier.get());
            }
        }));
        SideExecutor.runOnClient(() -> () -> {
            BrickRegistries.KEY_MAPPING.foreachRegisteredValue(KeyBindingHelper::registerKeyBinding);
            BrickRegistries.KEY_MAPPING.clean();
        });
    }

    //? if >= 1.20.6 {
    private static <T extends C2SPacket> void c2s(PacketConfig.C2S<T> c2S) {
        CustomPacketPayload.Type<T> type = new CustomPacketPayload.Type<>(c2S.id());
        StreamCodec<RegistryFriendlyByteBuf, T> codec = new StreamCodec<>() {
            @Override
            public T decode(RegistryFriendlyByteBuf buf) {
                return c2S.decoder().apply(new PacketContent(buf));
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, T packet) {
                c2S.encoder().accept(packet, new PacketContent(buf));
            }
        };
        PayloadTypeRegistry.playC2S().register(type, codec);
        ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
            if (c2S.netHandle()) {
                c2S.packetHandler().accept(payload, new C2SNetworkContext(context.player()));
            } else {
                context.player().getServer().execute(() -> c2S.packetHandler().accept(payload, new C2SNetworkContext(context.player())));
            }
        });
    }

    private static <T extends S2CPacket> void s2c(PacketConfig.S2C<T> s2C) {
        CustomPacketPayload.Type<T> type = new CustomPacketPayload.Type<>(s2C.id());
        StreamCodec<RegistryFriendlyByteBuf, T> codec = new StreamCodec<>() {
            @Override
            public T decode(RegistryFriendlyByteBuf buf) {
                return s2C.decoder().apply(new PacketContent(buf));
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, T packet) {
                s2C.encoder().accept(packet, new PacketContent(buf));
            }
        };
        PayloadTypeRegistry.playS2C().register(type, codec);
        ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
            if (s2C.netHandle()) {
                s2C.packetHandler().accept(payload, new S2CNetworkContext());
            } else {
                context.client().execute(() -> s2C.packetHandler().accept(payload, new S2CNetworkContext()));
            }
        });
    }

    private static <T extends LoginPacket> void login(PacketConfig.Login<T> s2CLogin) {
        List<Pair<String, T>> apply0 = s2CLogin.packetGenerator().apply(false);
        apply0.forEach(stringPair -> {
            SideExecutor.runOnClient(()->()->{
                ClientLoginNetworking.registerGlobalReceiver(s2CLogin.s2cID(),
                        (client, handler1, buf, listenerAdder) -> {
                            T applied = s2CLogin.s2cDecoder().apply(new PacketContent(buf));
                            client.execute(() -> s2CLogin.clientHandler().accept(applied, new S2CNetworkContext()));
                            return CompletableFuture.completedFuture(PacketByteBufs.create());
                        });
            });
            ServerLoginNetworking.registerGlobalReceiver(s2CLogin.c2sID(),
                    (server, handler, understood, buf, synchronizer, responseSender) -> {
                        if (!understood) {
                            return;
                        }
                        T applied = s2CLogin.c2sDecoder().apply(new PacketContent(buf));
                        server.execute(() -> s2CLogin.serverHandler().accept(applied, new C2SNetworkContext(null)));
                    });
        });
        ServerLoginConnectionEvents.QUERY_START.register((handler, server, sender, synchronizer) -> {
            List<Pair<String, T>> apply = s2CLogin.packetGenerator().apply(false);
            apply.forEach(stringPair -> {
                sender.sendPacket(s2CLogin.s2cID(), stringPair.getRight().getEncodedPacketContent(new PacketContent()).friendlyByteBuf());
            });
        });
    }

    private static <T extends SACPacket> void sac(PacketConfig.SAC<T> sAC) {
        CustomPacketPayload.Type<T> s2cT = new CustomPacketPayload.Type<>(sAC.s2cID());
        CustomPacketPayload.Type<T> c2sT = new CustomPacketPayload.Type<>(sAC.c2sID());
        StreamCodec<RegistryFriendlyByteBuf, T> codec = new StreamCodec<>() {
            @Override
            public T decode(RegistryFriendlyByteBuf buf) {
                return sAC.decoder().apply(new PacketContent(buf));
            }

            @Override
            public void encode(RegistryFriendlyByteBuf buf, T packet) {
                sAC.encoder().accept(packet, new PacketContent(buf));
            }
        };
        PayloadTypeRegistry.playC2S().register(c2sT, codec);
        PayloadTypeRegistry.playS2C().register(s2cT, codec);
        SideExecutor.runOnClient(()->()->{
            ClientPlayNetworking.registerGlobalReceiver(s2cT, (payload, context) -> {
                if (sAC.clientNetHandle()) {
                    sAC.clientHandler().accept(payload, new S2CNetworkContext());
                } else {
                    context.client().execute(() -> sAC.clientHandler().accept(payload, new S2CNetworkContext()));
                }
            });
        });
        ServerPlayNetworking.registerGlobalReceiver(c2sT, (payload, context) -> {
            if (sAC.serverNetHandle()) {
                sAC.serverHandler().accept(payload, new C2SNetworkContext(context.player()));
            } else {
                context.player().getServer().execute(() -> sAC.serverHandler().accept(payload, new C2SNetworkContext(context.player())));
            }
        });
    }
    //?}
    *///?}
}
