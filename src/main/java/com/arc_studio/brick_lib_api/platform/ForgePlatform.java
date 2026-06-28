package com.arc_studio.brick_lib_api.platform;

//? if forge {
/*import com.arc_studio.brick_lib_api.BrickLibAPI;
import com.arc_studio.brick_lib_api.core.data.capability.BuiltinCapabilities;
import com.arc_studio.brick_lib_api.core.data.capability.EnergyEjectorApi;
import com.arc_studio.brick_lib_api.core.data.capability.IFluidStorage;
import com.arc_studio.brick_lib_api.core.data.capability.IItemStorage;
import com.arc_studio.brick_lib_api.core.data.capability.context.BlockCapabilityContext;
import com.arc_studio.brick_lib_api.core.data.capability.core.BlockTransferConfig;
import com.arc_studio.brick_lib_api.core.data.capability.core.CapabilityEntries;
import com.arc_studio.brick_lib_api.core.data.capability.core.CapabilityEntry;
import com.arc_studio.brick_lib_api.core.network.PacketContent;
import com.arc_studio.brick_lib_api.core.network.context.C2SNetworkContext;
import com.arc_studio.brick_lib_api.core.network.context.S2CNetworkContext;
import com.arc_studio.brick_lib_api.core.network.type.*;
import com.arc_studio.brick_lib_api.core.network.type.LoginPacket;
import com.arc_studio.brick_lib_api.network.LogInReplyPacket;
import com.arc_studio.brick_lib_api.register.BrickRegistries;
import com.arc_studio.brick_lib_api.core.data.capability.impl.SimpleFluidStorage;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.BrickTransaction;
import com.arc_studio.brick_lib_api.core.PlatformInfo;
import com.arc_studio.brick_lib_api.core.VillagerTradeEntry;
import com.arc_studio.brick_lib_api.core.register.BrickRegisterManager;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
//? if > 1.18.2 {
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.registries.RegisterEvent;
//? } else {
/^import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.registries.IForgeRegistryEntry;
^///? }
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;

import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.energy.IEnergyStorage;


import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.IItemHandler;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Registry;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraftforge.common.util.LogicalSidedProvider;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.NetworkDirection;
//? if < 1.20.1 {
/^import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.HandshakeHandler;
import net.minecraftforge.network.NetworkHooks;
^///? }
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

import org.apache.commons.lang3.tuple.Pair;

//? if < 1.20.4 {
/^import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraftforge.network.HandshakeHandler;
import net.minecraftforge.network.NetworkHooks;
^///?} else {
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;
import net.minecraftforge.network.config.SimpleConfigurationTask;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraftforge.event.network.GatherLoginConfigurationTasksEvent;

//? }


//? if >= 1.21.5 {
import net.minecraft.core.registries.BuiltInRegistries;
 
//?}

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.*;

import java.util.concurrent.atomic.AtomicInteger;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
*///?}

@SuppressWarnings({"unchecked", "rawtypes"})
public class ForgePlatform {
    //? if forge {
    /*protected static final String PROTOCOL_VERSION = "0";

    //? if < 1.20.4 {
    /^public static final SimpleChannel c2sPlayChannel = NetworkRegistry.newSimpleChannel(
            BrickLibAPI.ofPath("c2s_play"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static final SimpleChannel c2sLoginChannel = NetworkRegistry.newSimpleChannel(
            BrickLibAPI.ofPath("c2s_login"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static final SimpleChannel s2cPlayChannel = NetworkRegistry.newSimpleChannel(
            BrickLibAPI.ofPath("s2c_play"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static final SimpleChannel s2cLoginChannel = NetworkRegistry.newSimpleChannel(
            BrickLibAPI.ofPath("s2c_login"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    ^///?}

    //? if >= 1.20.4 {
    public static final SimpleChannel c2sPlayChannel = ChannelBuilder.named(BrickLibAPI.ofPath("c2s_play"))
        .optional()
        .networkProtocolVersion(1)
        .serverAcceptedVersions(Channel.VersionTest.exact(1))
        .clientAcceptedVersions(Channel.VersionTest.exact(1))
        .simpleChannel();

    public static final SimpleChannel c2sLoginChannel = ChannelBuilder.named(BrickLibAPI.ofPath("c2s_login"))
        .optional()
        .networkProtocolVersion(1)
        .serverAcceptedVersions(Channel.VersionTest.exact(1))
        .clientAcceptedVersions(Channel.VersionTest.exact(1))
        .simpleChannel();

    public static final SimpleChannel s2cPlayChannel = ChannelBuilder.named(BrickLibAPI.ofPath("s2c_play"))
        .optional()
        .networkProtocolVersion(1)
        .serverAcceptedVersions(Channel.VersionTest.exact(1))
        .clientAcceptedVersions(Channel.VersionTest.exact(1))
        .simpleChannel();

    public static final SimpleChannel s2cLoginChannel = ChannelBuilder.named(BrickLibAPI.ofPath("s2c_login"))
        .optional()
        .networkProtocolVersion(1)
        .serverAcceptedVersions(Channel.VersionTest.exact(1))
        .clientAcceptedVersions(Channel.VersionTest.exact(1))
        .simpleChannel();

    //?}

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
                    //? if < 1.20.4 {
                    /^//? if > 1.18.2 {
                    command.consumerNetworkThread((msg, contextSupplier) -> {
                        c2S.packetHandler().accept(msg, new C2SNetworkContext(
                            contextSupplier.get().getSender()
                        ));
                    });
                    //? } else {
                    /^¹command.consumer((msg, contextSupplier) -> {
                        c2S.packetHandler().accept(msg, new C2SNetworkContext(
                            contextSupplier.get().getSender()
                        ));
                    });
                    ¹^///? }

                    ^///?} else {
                    command.consumerNetworkThread((msg, contextSupplier) -> {
                        c2S.packetHandler().accept(msg, new C2SNetworkContext(
                            contextSupplier.getSender()
                        ));
                    });
                    //?}
                } else {
                    //? if < 1.20.4 {
                    /^//? if > 1.18.2 {
                    command.consumerMainThread((msg, contextSupplier) -> c2S.packetHandler().accept(msg, new C2SNetworkContext(
                        contextSupplier.get().getSender()
                    )));
                    //? } else {
                    /^¹command.consumer((msg, contextSupplier) -> {
                        c2S.packetHandler().accept(msg, new C2SNetworkContext(contextSupplier.get().getSender()));
                    });
                    ¹^///? }

                    ^///?}
                    //? if >= 1.20.4 {
                    command.consumerMainThread((msg, contextSupplier) -> c2S.packetHandler().accept(msg, new C2SNetworkContext(
                        contextSupplier.getSender()
                    )));
                    //?}
                }
                command.add();
            }
            else if (packetConfig instanceof PacketConfig.S2C s2C) {
                SimpleChannel.MessageBuilder<? extends S2CPacket> command = s2cPlayChannel
                    .messageBuilder(s2C.type(), s2cID.getAndIncrement(), NetworkDirection.PLAY_TO_CLIENT)
                    .encoder((msg, buf) -> s2C.encoder().accept(msg, new PacketContent((FriendlyByteBuf) buf)))
                    .decoder(buf -> s2C.decoder().apply(new PacketContent((FriendlyByteBuf) buf)));
                if (s2C.netHandle()) {
                    //? if < 1.20.4 {
                        /^//? if > 1.18.2 {
                        command.consumerNetworkThread((msg, contextSupplier) -> {
                            s2C.packetHandler().accept(msg, new S2CNetworkContext());
                        });
                        //? } else {
                        /^¹command.consumer((msg, contextSupplier) -> {
                            s2C.packetHandler().accept(msg, new S2CNetworkContext());
                        });
                        ¹^///? }
                    ^///?} else {
                    command.consumerNetworkThread((msg, contextSupplier) -> {
                        s2C.packetHandler().accept(msg, new S2CNetworkContext());
                    });
                    //?}
                } else {
                    //? if < 1.20.4 {
                        /^//? if > 1.18.2 {
                        command.consumerMainThread((msg, contextSupplier) -> {
                            s2C.packetHandler().accept(msg, new S2CNetworkContext());
                        });
                        //? } else {
                        /^¹command.consumer((msg, contextSupplier) -> {
                            s2C.packetHandler().accept(msg, new S2CNetworkContext());
                        });
                        ¹^///? }
                    ^///?} else {
                    command.consumerMainThread((msg, contextSupplier) -> {
                        s2C.packetHandler().accept(msg, new S2CNetworkContext());
                    });
                    //?}
                }
                command.add();
            } else if (packetConfig instanceof PacketConfig.SAC sac) {
                SimpleChannel.MessageBuilder<? extends SACPacket> s2cBuilder = s2cPlayChannel
                    .messageBuilder(sac.type(), s2cID.getAndIncrement(), NetworkDirection.PLAY_TO_CLIENT)
                    .encoder((msg, buf) -> sac.encoder().accept(msg, new PacketContent((FriendlyByteBuf) buf)))
                    .decoder(buf -> sac.decoder().apply(new PacketContent((FriendlyByteBuf) buf)));
                if (sac.netHandle()) {
                    //? if < 1.20.4 {
                        /^//? if > 1.18.2 {
                        s2cBuilder.consumerNetworkThread((msg, contextSupplier) -> {
                            sac.clientHandler().accept(msg, new S2CNetworkContext());
                        });
                        //? } else {
                        /^¹s2cBuilder.consumer((msg, contextSupplier) -> {
                            sac.clientHandler().accept(msg, new S2CNetworkContext());
                        });
                        ¹^///? }
                    ^///?} else {
                    s2cBuilder.consumerNetworkThread((msg, contextSupplier) -> {
                        sac.clientHandler().accept(msg, new S2CNetworkContext());
                    });
                    //?}
                } else {
                    //? if < 1.20.4 {
                        /^//? if > 1.18.2 {
                        s2cBuilder.consumerMainThread((msg, contextSupplier) -> {
                            sac.clientHandler().accept(msg, new S2CNetworkContext());
                        });
                        //? } else {
                        /^¹s2cBuilder.consumer((msg, contextSupplier) -> {
                            sac.clientHandler().accept(msg, new S2CNetworkContext());
                        });
                        ¹^///? }
                    ^///?} else {
                    s2cBuilder.consumerMainThread((msg, contextSupplier) -> {
                        sac.clientHandler().accept(msg, new S2CNetworkContext());
                    });
                    //?}
                }
                s2cBuilder.add();

                SimpleChannel.MessageBuilder<? extends SACPacket> c2sBuilder = c2sPlayChannel
                    .messageBuilder(sac.type(), c2sID.getAndIncrement(), NetworkDirection.PLAY_TO_SERVER)
                    .encoder((msg, buf) -> sac.encoder().accept(msg, new PacketContent((FriendlyByteBuf) buf)))
                    .decoder(buf -> sac.decoder().apply(new PacketContent((FriendlyByteBuf) buf)));
                if (sac.netHandle()) {
                    //? if < 1.20.4 {
                        /^//? if > 1.18.2 {
                            c2sBuilder.consumerNetworkThread((msg, contextSupplier) -> {
                            sac.serverHandler().accept(msg, new C2SNetworkContext(
                                contextSupplier.get().getSender()
                            ));
                        });
                        //? } else {
                        /^¹c2sBuilder.consumer((msg, contextSupplier) -> {
                            sac.serverHandler().accept(msg, new C2SNetworkContext(
                                contextSupplier.get().getSender()
                            ));
                        });
                        ¹^///? }
                    ^///?} else {
                    c2sBuilder.consumerNetworkThread((msg, contextSupplier) -> {
                        sac.serverHandler().accept(msg, new C2SNetworkContext(
                            contextSupplier.getSender()
                        ));
                    });

                    //?}
                } else {
                    //? if < 1.20.4 {
                        /^//? if > 1.18.2 {
                            c2sBuilder.consumerMainThread((msg, contextSupplier) -> sac.serverHandler().accept(msg, new C2SNetworkContext(
                            contextSupplier.get().getSender()
                        )));
                        //? } else {
                        /^¹c2sBuilder.consumer((msg, contextSupplier) -> {
                            sac.serverHandler().accept(msg, new C2SNetworkContext(
                                contextSupplier.get().getSender()));
                        });
                        ¹^///? }
                    ^///?} else {
                    c2sBuilder.consumerMainThread((msg, contextSupplier) -> sac.serverHandler().accept(msg, new C2SNetworkContext(
                        contextSupplier.getSender()
                    )));

                    //?}
                }
                c2sBuilder.add();
            }
            else if (packetConfig instanceof PacketConfig.Login login) {
                //? if < 1.20.4 {
                    /^if(LogInReplyPacket.class.isAssignableFrom(login.type())){
                        SimpleChannel.MessageBuilder<? extends LoginPacket> c2sBuilder = c2sLoginChannel
                                .messageBuilder(LogInReplyPacket.class, 999, NetworkDirection.LOGIN_TO_SERVER)
                                .encoder((o, o2) -> {})
                                .decoder(buf -> new LogInReplyPacket());
                        c2sBuilder.loginIndex(LoginPacket::getLoginIndex, LoginPacket::setLoginIndex);
                        //? if > 1.18.2 {
                            c2sBuilder.consumerNetworkThread(
                            HandshakeHandler.indexFirst((handshakeHandler, intSupplier, supplier) ->
                                supplier.get().setPacketHandled(true)));
                        //? } else {
                        /^¹c2sBuilder.consumer(
                            HandshakeHandler.indexFirst((handshakeHandler, intSupplier, supplier) ->
                                supplier.get().setPacketHandled(true)));
                        ¹^///? }
                        c2sBuilder.add();
                    }
                    SimpleChannel.MessageBuilder<? extends LoginPacket> s2cBuilder = s2cLoginChannel
                            .messageBuilder(login.type(), s2cID.getAndIncrement(), NetworkDirection.LOGIN_TO_CLIENT)
                            .encoder((msg, buf) -> login.encoder().accept(msg,new PacketContent((FriendlyByteBuf) buf)))
                            .decoder( buf -> login.s2cDecoder().apply(new PacketContent((FriendlyByteBuf) buf)));
                    s2cBuilder.loginIndex(LoginPacket::getLoginIndex, LoginPacket::setLoginIndex)
                            .buildLoginPacketList(login.packetGenerator());
                    //? if > 1.18.2 {
                        s2cBuilder.consumerMainThread((s2CLoginPacket, contextSupplier) -> {
                        login.clientHandler().accept(s2CLoginPacket, new S2CNetworkContext());
                        c2sLoginChannel.reply(new LogInReplyPacket(), contextSupplier.get());
                    })
                    //? } else {
                    /^¹s2cBuilder.consumer((s2CLoginPacket, contextSupplier) -> {
                        login.clientHandler().accept(s2CLoginPacket, new S2CNetworkContext());
                        c2sLoginChannel.reply(new LogInReplyPacket(), contextSupplier.get());
                    })
                    ¹^///? }
                    .add();

                ^///?} else {

                loginPacket(login);

                //?}
            }
        });
    }

    //? if >= 1.20.4 {
    private static<T extends LoginPacket> void loginPacket(PacketConfig.Login<T> login){
        if(LogInReplyPacket.class.isAssignableFrom(login.type())){
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

    //?}

    /^ Forge 能力提供者 — 将 Brick 能力按需包装为 Forge 原生能力 ^/
    private static class ForgeCapabilityProvider implements ICapabilityProvider {
        private final BlockEntity be;
        private final CapabilityEntry<?> entry;
        private final LazyOptional<?>[] cache = new LazyOptional[7];

        ForgeCapabilityProvider(BlockEntity be, CapabilityEntry<?> entry) {
            this.be = be;
            this.entry = entry;
        }

        @Override
        public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
            if (!matchesNativeCapability(cap)) {
                return LazyOptional.empty();
            }

            int index = side == null ? 6 : side.ordinal();
            LazyOptional<?> optional = cache[index];
            if (optional == null || !optional.isPresent()) {
                Object wrapped = wrap(side);
                if (wrapped == null) {
                    return LazyOptional.empty();
                }
                optional = LazyOptional.of(() -> wrapped);
                cache[index] = optional;
            }
            return optional.cast();
        }

        private boolean matchesNativeCapability(Capability<?> cap) {
            //? if > 1.18.2 {
            return (entry.capability() == BuiltinCapabilities.ITEM_HANDLER && cap == ForgeCapabilities.ITEM_HANDLER)
                    || (entry.capability() == BuiltinCapabilities.ENERGY && cap == ForgeCapabilities.ENERGY)
                    || (entry.capability() == BuiltinCapabilities.FLUID_HANDLER && cap == ForgeCapabilities.FLUID_HANDLER);
            //? } else {
            /^return (entry.capability() == BuiltinCapabilities.ITEM_HANDLER && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                    || (entry.capability() == BuiltinCapabilities.ENERGY && cap == CapabilityEnergy.ENERGY)
                    || (entry.capability() == BuiltinCapabilities.FLUID_HANDLER && cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
            ^///? }
        }

        @Nullable
        private Object wrap(@Nullable Direction side) {
            if (!(be.getLevel() instanceof ServerLevel serverLevel)) {
                return null;
            }

            BlockCapabilityContext context = CapabilityEntries.blockContext(entry, serverLevel, be);
            if (context == null) {
                return null;
            }
            BlockTransferConfig config = entry.transferConfig(context);
            if (side != null && config != null && (!config.isEnabled(side) || config.isLocked(side))) {
                return null;
            }

            Object storage = entry.get(context, side);
            if (storage == null) {
                return null;
            }
            Runnable dirty = () -> entry.markDirty(context);
            if (entry.capability() == BuiltinCapabilities.ITEM_HANDLER && storage instanceof IItemStorage itemStorage) {
                return new ForgeItemHandlerWrapper(serverLevel, be.getBlockPos(), be.getBlockState(), itemStorage, dirty, side);
            }
            if (entry.capability() == BuiltinCapabilities.ENERGY
                    && storage instanceof com.arc_studio.brick_lib_api.core.data.capability.IEnergyStorage energyStorage) {
                if (config != null) {
                    EnergyEjectorApi.track(serverLevel, be.getBlockPos());
                }
                return new ForgeEnergyStorageWrapper(serverLevel, be.getBlockPos(), be.getBlockState(), energyStorage, dirty, side, config);
            }
            if (entry.capability() == BuiltinCapabilities.FLUID_HANDLER && storage instanceof IFluidStorage fluidStorage) {
                return new ForgeFluidHandlerWrapper(serverLevel, be.getBlockPos(), be.getBlockState(), fluidStorage, dirty, side);
            }
            return null;
        }
    }

    /^*
     * 通用的 Forge IItemHandler 包装器。
     * <p>
     * 将 UCS IItemStorage 适配为 Forge IItemHandler，处理 simulate 模式、事务提交和 dirty 标记。
     * </p>
     ^/
    @SuppressWarnings("unused")
    protected static class ForgeItemHandlerWrapper implements IItemHandler {
        private final ServerLevel level;
        private final BlockPos pos;
        private final BlockState state;
        private final IItemStorage ucs;
        @Nullable
        private final Runnable dirtyNotifier;
        @Nullable
        private final Direction side;

        public ForgeItemHandlerWrapper(ServerLevel level, BlockPos pos, BlockState state,
                                       IItemStorage ucs,
                                       @Nullable Runnable dirtyNotifier,
                                       @Nullable Direction side) {
            this.level = level;
            this.pos = pos;
            this.state = state;
            this.ucs = ucs;
            this.dirtyNotifier = dirtyNotifier;
            this.side = side;
        }

        private boolean isValid() {
            return level.getBlockState(pos).is(state.getBlock());
        }

        private boolean isValidSlot(int slot) {
            return slot >= 0 && slot < ucs.getSlots();
        }

        @Override
        public int getSlots() {
            return isValid() ? ucs.getSlots() : 0;
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (!isValid() || !isValidSlot(slot)) return ItemStack.EMPTY;
            ItemStack stack = ucs.getStackInSlot(slot);
            long amount = ucs.getAmountInSlot(slot);
            if (stack == null || stack.isEmpty() || amount <= 0) return ItemStack.EMPTY;
            ItemStack copy = stack.copy();
            copy.setCount(IFluidStorage.clampToInt(amount));
            return copy;
        }

        @Override
        public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!isValid() || !isValidSlot(slot) || stack == null || stack.isEmpty()) return stack;
            if (!ucs.isItemValid(slot, stack, side)) return stack;

            try (BrickTransaction tx = BrickTransaction.openOuter()) {
                long inserted = ucs.insertItem(slot, stack, side, stack.getCount(), tx);
                if (inserted <= 0) return stack;
                if (!simulate) {
                    tx.commit();
                    markDirty();
                }
                return remainder(stack, inserted);
            }
        }

        @Override
        public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!isValid() || !isValidSlot(slot) || amount <= 0) return ItemStack.EMPTY;
            ItemStack current = getStackInSlot(slot);
            if (current.isEmpty()) return ItemStack.EMPTY;

            try (BrickTransaction tx = BrickTransaction.openOuter()) {
                long extracted = ucs.extractItem(slot, side, amount, tx);
                if (extracted <= 0) return ItemStack.EMPTY;
                if (!simulate) {
                    tx.commit();
                    markDirty();
                }
                ItemStack result = current.copy();
                result.setCount(IFluidStorage.clampToInt(extracted));
                return result;
            }
        }

        @Override
        public int getSlotLimit(int slot) {
            if (!isValid() || !isValidSlot(slot)) return 0;
            return IFluidStorage.clampToInt(ucs.getSlotCapacity(slot));
        }

        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            return isValid() && isValidSlot(slot) && stack != null && !stack.isEmpty() && ucs.isItemValid(slot, stack, side);
        }

        private ItemStack remainder(ItemStack original, long inserted) {
            int left = original.getCount() - IFluidStorage.clampToInt(inserted);
            if (left <= 0) return ItemStack.EMPTY;
            ItemStack result = original.copy();
            result.setCount(left);
            return result;
        }

        private void markDirty() {
            if (dirtyNotifier != null) dirtyNotifier.run();
        }
    }

    /^*
     * 通用的 Forge IEnergyStorage 包装器。
     * <p>
     * 将 UCS IEnergyStorage 适配为 Forge IEnergyStorage，处理 int/long 转换、
     * simulate 模式、dirty 标记。
     * </p>
     ^/
    @SuppressWarnings("unused")
    protected static class ForgeEnergyStorageWrapper implements IEnergyStorage {
        private final ServerLevel level;
        private final BlockPos pos;
        private final BlockState state;
        private final com.arc_studio.brick_lib_api.core.data.capability.IEnergyStorage ucs;
        @Nullable
        private final Runnable dirtyNotifier;
        @Nullable
        private final Direction side;
        @Nullable
        private final BlockTransferConfig transferConfig;

        public ForgeEnergyStorageWrapper(ServerLevel level, BlockPos pos, BlockState state,
                                         com.arc_studio.brick_lib_api.core.data.capability.IEnergyStorage ucs,
                                         @Nullable Runnable dirtyNotifier,
                                         @Nullable Direction side,
                                         @Nullable BlockTransferConfig transferConfig) {
            this.level = level;
            this.pos = pos;
            this.state = state;
            this.ucs = ucs;
            this.dirtyNotifier = dirtyNotifier;
            this.side = side;
            this.transferConfig = transferConfig;
        }

        private boolean isValid() {
            return level.getBlockState(pos).is(state.getBlock());
        }

        private boolean sideOpen() {
            return side == null || transferConfig == null || (transferConfig.isEnabled(side) && !transferConfig.isLocked(side));
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!isValid() || !sideOpen() || maxReceive <= 0 || !ucs.canReceive(side)) return 0;
            if (simulate) {
                return IFluidStorage.clampToInt(Math.min(maxReceive,
                    getExposedCapacity() - ucs.getEnergyStored()));
            }
            try (BrickTransaction tx = BrickTransaction.openOuter()) {
                long received = ucs.receiveEnergy(side, maxReceive, tx);
                if (received > 0) {
                    tx.commit();
                    markDirty();
                }
                return IFluidStorage.clampToInt(received);
            }
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (!isValid() || !sideOpen() || maxExtract <= 0 || !ucs.canExtract(side)) return 0;
            if (simulate) {
                return IFluidStorage.clampToInt(Math.min(maxExtract, ucs.getEnergyStored()));
            }
            try (BrickTransaction tx = BrickTransaction.openOuter()) {
                long extracted = ucs.extractEnergy(side, maxExtract, tx);
                if (extracted > 0) {
                    tx.commit();
                    markDirty();
                }
                return IFluidStorage.clampToInt(extracted);
            }
        }

        @Override public int getEnergyStored() { return isValid() ? IFluidStorage.clampToInt(ucs.getEnergyStored()) : 0; }
        @Override public int getMaxEnergyStored() { return isValid() ? IFluidStorage.clampToInt(getExposedCapacity()) : 0; }
        @Override public boolean canExtract() { return isValid() && sideOpen() && ucs.canExtract(side); }
        @Override public boolean canReceive() { return isValid() && sideOpen() && ucs.canReceive(side); }

        private long getExposedCapacity() {
            return transferConfig == null ? ucs.getMaxEnergyStored() : Math.min(ucs.getMaxEnergyStored(), transferConfig.capacity());
        }

        private void markDirty() {
            if (dirtyNotifier != null) dirtyNotifier.run();
        }
    }

    /^*
     * 通用的 Forge IFluidHandler 包装器。
     * <p>
     * 将 UCS IFluidStorage 适配为 Forge IFluidHandler，处理 droplets/mB 转换、
     * simulate 模式、dirty 标记。
     * </p>
     ^/
    @SuppressWarnings("unused")
    protected static class ForgeFluidHandlerWrapper implements IFluidHandler {
        private final ServerLevel level;
        private final BlockPos pos;
        private final BlockState state;
        private final IFluidStorage ucs;
        @Nullable
        private final Runnable dirtyNotifier;

        private final Direction side;

        public ForgeFluidHandlerWrapper(ServerLevel level, BlockPos pos, BlockState state,
                                        IFluidStorage ucs,
                                        @Nullable Runnable dirtyNotifier, @Nullable Direction side) {
            this.level = level;
            this.pos = pos;
            this.state = state;
            this.ucs = ucs;
            this.dirtyNotifier = dirtyNotifier;
            this.side = side;
        }

        private boolean isValid() {
            return level.getBlockState(pos).is(state.getBlock());
        }

        private SimpleFluidStorage simple() {
            return ucs instanceof SimpleFluidStorage sfs ? sfs : null;
        }

        @Override
        public int getTanks() {
            if (!isValid()) return 1;
            SimpleFluidStorage s = simple();
            return s != null ? s.getTanks() : 1;
        }

        @Override
        public FluidStack getFluidInTank(int tank) {
            if (!isValid()) return FluidStack.EMPTY;
            SimpleFluidStorage s = simple();
            if (s == null) return FluidStack.EMPTY;
            var fluid = s.getFluidInTank(tank);
            if (fluid == null) return FluidStack.EMPTY;
            return new FluidStack(fluid, IFluidStorage.dropletsToMb(s.getFluidAmountInTank(tank)));
        }

        @Override
        public int getTankCapacity(int tank) {
            if (!isValid()) return 0;
            SimpleFluidStorage s = simple();
            return s != null ? IFluidStorage.dropletsToMb(s.getTankCapacity(tank)) : 0;
        }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) {
            if (!isValid() || stack.isEmpty()) return false;
            SimpleFluidStorage s = simple();
            return s != null && s.isFluidValid(tank, stack.getFluid(),side);
        }

        @Override
        public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            if (!isValid() || resource.isEmpty()) return 0;
            SimpleFluidStorage s = simple();
            if (s == null) return 0;
            if (action.simulate()) {
                return IFluidStorage.getFillableMb(s, resource.getFluid(),side, resource.getAmount());
            }
            long droplets = IFluidStorage.mbToDroplets(resource.getAmount());
            try (BrickTransaction tx = BrickTransaction.openOuter()) {
                long filled = s.fill(resource.getFluid(),side, droplets, tx);
                if (filled > 0) {
                    tx.commit();
                    markDirty();
                }
                return IFluidStorage.dropletsToMb(filled);
            }
        }

        @Override
        public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
            if (!isValid() || resource.isEmpty()) return FluidStack.EMPTY;
            SimpleFluidStorage s = simple();
            if (s == null) return FluidStack.EMPTY;
            if (action.simulate()) {
                int drained = IFluidStorage.getDrainableMb(s, resource.getFluid(), resource.getAmount());
                return drained > 0 ? new FluidStack(resource.getFluid(), drained) : FluidStack.EMPTY;
            }
            long droplets = IFluidStorage.mbToDroplets(resource.getAmount());
            try (BrickTransaction tx = BrickTransaction.openOuter()) {
                long drained = s.drain(resource.getFluid(),side, droplets, tx);
                if (drained > 0) {
                    tx.commit();
                    markDirty();
                    return new FluidStack(resource.getFluid(), IFluidStorage.dropletsToMb(drained));
                }
                return FluidStack.EMPTY;
            }
        }

        @Override
        public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
            if (!isValid() || maxDrain <= 0) return FluidStack.EMPTY;
            SimpleFluidStorage s = simple();
            if (s == null) return FluidStack.EMPTY;
            var fluid = s.getFluidInTank(0);
            if (fluid == null) return FluidStack.EMPTY;
            if (action.simulate()) {
                int drained = IFluidStorage.getDrainableMb(s, fluid, maxDrain);
                return drained > 0 ? new FluidStack(fluid, drained) : FluidStack.EMPTY;
            }
            long droplets = IFluidStorage.mbToDroplets(maxDrain);
            try (BrickTransaction tx = BrickTransaction.openOuter()) {
                long drained = s.drain(droplets,side, tx);
                if (drained > 0) {
                    tx.commit();
                    markDirty();
                    return new FluidStack(fluid, IFluidStorage.dropletsToMb(drained));
                }
                return FluidStack.EMPTY;
            }
        }

        private void markDirty() {
            if (dirtyNotifier != null) dirtyNotifier.run();
        }
    }


    public static Path getConfigDirectory() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static Path versionPath() {
        return FMLPaths.GAMEDIR.get();
    }

    public static boolean isDev() {
        return !FMLEnvironment.production;
    }

    public static PlatformInfo platform() {
        PlatformInfo type = new PlatformInfo();
        type.setForge();
        if (isClient()) {
            type.setClient();
        } else if (isServer()) {
            type.setServer();
        }
        return type;
    }

    public static boolean isClient() {
        return FMLEnvironment.dist.isClient();
    }

    public static boolean isServer() {
        return FMLEnvironment.dist.isDedicatedServer();
    }

    public static <T extends S2CNetworkContext> void enqueueWork(T context, Runnable runnable) {
        BlockableEventLoop<?> executor = LogicalSidedProvider.WORKQUEUE.get(NetworkDirection.PLAY_TO_CLIENT.getReceptionSide());
        if (!executor.isSameThread()) {
            executor.submitAsync(runnable);
        } else {
            runnable.run();
            CompletableFuture.completedFuture(null);
        }
    }

    public static <T extends C2SNetworkContext> void enqueueWork(T context, Runnable runnable) {
        BlockableEventLoop<?> executor = LogicalSidedProvider.WORKQUEUE.get(NetworkDirection.PLAY_TO_SERVER.getReceptionSide());
        if (!executor.isSameThread()) {
            executor.submitAsync(runnable);
        } else {
            runnable.run();
            CompletableFuture.completedFuture(null);
        }
    }

    public static void sendToPlayer(ICHandlePacket packet, Iterable<ServerPlayer> serverPlayers) {
        for (ServerPlayer serverPlayer : serverPlayers) {
            //? if >= 1.20.4 {
            s2cPlayChannel.send(packet, PacketDistributor.PLAYER.with(serverPlayer));

            //?}
            //? if < 1.20.4 {
            /^s2cPlayChannel.send(PacketDistributor.PLAYER.with(() -> serverPlayer), packet);

            ^///?}
        }
    }

    public static void sendToServer(ISHandlePacket packet) {
        //? if >= 1.20.4 {
        c2sPlayChannel.send(packet, PacketDistributor.SERVER.noArg());

        //?}
        //? if < 1.20.4 {
        /^c2sPlayChannel.sendToServer(packet);

        ^///?}
    }

    public static Set<ResourceLocation> networkChannels(Connection connection, ConnectionProtocol protocol) {
        //? if >= 1.20.4 {
        return Set.of();

        //?}
        //? if < 1.20.4 {
        /^MCRegisterPacketHandler.ChannelList list = NetworkHooks.getChannelList(connection);
        if (list != null) {
            return list.getRemoteLocations();
        }
        return Set.of();

        ^///?}
    }

    @Mod.EventBusSubscriber(modid = BrickLibAPI.MOD_ID)
    static final class ForgeCommonEvent {
        @SubscribeEvent
        static void onAttachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
            System.out.println("ForgePlatform.onAttachCapabilities");
            BlockEntity be = event.getObject();

            BrickRegistries.CAPABILITY.forEach(entry -> {
                if (entry.target().kind() == com.arc_studio.brick_lib_api.core.data.capability.core.CapabilityTarget.Kind.BLOCK
                        || entry.target().kind() == com.arc_studio.brick_lib_api.core.data.capability.core.CapabilityTarget.Kind.BLOCK_ENTITY) {
                    event.addCapability(entry.id(), new ForgePlatform.ForgeCapabilityProvider(be, entry));
                }
            });
        }
    }

    @Mod.EventBusSubscriber(modid = BrickLibAPI.MOD_ID)
    static final class ForgeModEvent {
        //? if > 1.18.2 {
        @SubscribeEvent
        public static <T> void onRegister(RegisterEvent event) {
            ResourceKey<? extends Registry<T>> registeringKey = (ResourceKey<? extends Registry<T>>) event.getRegistryKey();
            for (Map.Entry<Registry<?>, Map<ResourceLocation, Supplier<?>>> entry : BrickRegisterManager.getVanillaEntries().entrySet()) {
                if (entry.getKey().key().equals(registeringKey)) {
                    entry.getValue().forEach((resourceLocation, supplier) -> {
                        event.register(registeringKey, resourceLocation, () -> (T) supplier.get());
                    });
                    return;
                }
            }
            HashMap<Pair<VillagerProfession, Integer>, ArrayList<VillagerTrades.ItemListing>> map = new HashMap<>();
            for (VillagerTradeEntry entry : BrickRegistries.VILLAGER_TRADE) {
                Pair<VillagerProfession, Integer> key = Pair.of(entry.profession(), entry.level());
                ArrayList<VillagerTrades.ItemListing> list = map.getOrDefault(key, new ArrayList<>());
                list.add(entry.trade());
                map.put(key, list);
            }
            map.forEach((pair, itemListings) -> Platform.registerVillagerOffers(pair.getKey(), pair.getValue(), itemListings));
            HashMap<Integer, ArrayList<VillagerTrades.ItemListing>> map1 = new HashMap<>();
            for (VillagerTradeEntry entry : BrickRegistries.WANDERING_TRADE) {
                ArrayList<VillagerTrades.ItemListing> list = map1.getOrDefault(entry.level(), new ArrayList<>());
                list.add(entry.trade());
                map1.put(entry.level(), list);
            }
            map1.forEach(Platform::registerWanderingOffers);
        }

        //?}

        //? if <= 1.18.2 {
        /^@SubscribeEvent
    public static <T extends IForgeRegistryEntry<T>> void onRegister(RegistryEvent.Register event) {
        ResourceKey<? extends Registry<T>> registeringKey = event.getRegistry().getRegistryKey();
        for (Map.Entry<Registry<?>, Map<ResourceLocation, Supplier<?>>> entry : BrickRegisterManager.getVanillaEntries().entrySet()) {
            if (entry.getKey().key().equals(registeringKey)) {
                entry.getValue().forEach((resourceLocation, supplier) -> {
                    T value = (T) supplier.get();
                    value.setRegistryName(resourceLocation);
                    event.getRegistry().register(value);
                });
                return;
            }
        }
    }
    ^///?}
    }

    *///?}
}
