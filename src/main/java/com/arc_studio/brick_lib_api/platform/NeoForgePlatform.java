package com.arc_studio.brick_lib_api.platform;


//? if neoforge {
/*import com.arc_studio.brick_lib_api.BrickLibAPI;
import com.arc_studio.brick_lib_api.core.data.capability.IItemStorage;
import com.arc_studio.brick_lib_api.core.data.capability.IFluidStorage;
import com.arc_studio.brick_lib_api.core.data.capability.impl.SimpleFluidStorage;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.BrickTransaction;
import com.arc_studio.brick_lib_api.core.network.PacketContent;
import com.arc_studio.brick_lib_api.core.network.context.C2SNetworkContext;
import com.arc_studio.brick_lib_api.core.network.context.S2CNetworkContext;
import com.arc_studio.brick_lib_api.core.network.type.*;
import com.arc_studio.brick_lib_api.network.LogInReplyPacket;
import com.arc_studio.brick_lib_api.register.BrickRegistries;
import io.netty.util.AttributeKey;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.network.registration.NetworkPayloadSetup;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.jetbrains.annotations.Nullable;
import com.arc_studio.brick_lib_api.Constants;
import com.arc_studio.brick_lib_api.core.PlatformInfo;
import com.arc_studio.brick_lib_api.core.VillagerTradeEntry;
import com.arc_studio.brick_lib_api.core.register.BrickRegisterManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.level.storage.LevelResource;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.PacketDistributor;
import net.minecraft.server.MinecraftServer;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

//? if >= 1.21.5 {
import net.minecraft.core.registries.BuiltInRegistries;
//?}

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
import net.neoforged.bus.api.SubscribeEvent;
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

//?}
//? if < 1.20.6 {
/^@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
^///?} else {
@EventBusSubscriber(
        //? if < 1.21.1 {
        /^bus = EventBusSubscriber.Bus.MOD
        ^///?}
)
//?}
@SuppressWarnings({"unchecked", "rawtypes"})

*///? }
public class NeoForgePlatform {
    //? if neoforge {
    /*@SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCommonSetup(
            /^? >1.20.4 {^/ RegisterPayloadHandlersEvent /^?} else {^//^RegisterPayloadHandlerEvent^//^?}^/
                    events) {
        /^? >1.20.4 {^/ PayloadRegistrar /^?} else {^/
        /^IPayloadRegistrar^//^?}^/ registrar = events.registrar(BrickLibAPI.MOD_ID);
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

    private static <T extends C2SPacket> void c2s(/^? >=1.20.6 {^/ PayloadRegistrar /^?} else {^//^IPayloadRegistrar^//^?}^/ registrar, PacketConfig.C2S<T> c2S) {
        //? if > 1.20.4 {
        StreamCodec<RegistryFriendlyByteBuf, T> codec = StreamCodec.of(
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
        //?} else {
        /^registrar.play(c2S.id(),
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
        ^///?}
    }

    private static <T extends S2CPacket> void s2c(/^? >=1.20.6 {^/ PayloadRegistrar /^?} else {^//^IPayloadRegistrar^//^?}^/ registrar, PacketConfig.S2C<T> s2C) {
        //? if > 1.20.4 {
        StreamCodec<RegistryFriendlyByteBuf, T> codec = StreamCodec.of(
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
        //?} else {
        /^registrar.play(s2C.id(),
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
        ^///?}
    }

    private static <T extends SACPacket> void sac(/^? >=1.20.6 {^/ PayloadRegistrar /^?} else {^//^IPayloadRegistrar^//^?}^/ registrar, PacketConfig.SAC<T> sAC) {
        //? if > 1.20.4 {
        StreamCodec<RegistryFriendlyByteBuf, T> codec = StreamCodec.of(
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
        //?} else {
            /^registrar.play(sAC.id(),
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
            ^///?}
    }

    private static <T extends LoginPacket> void login(/^? >=1.20.6 {^/ PayloadRegistrar /^?} else {^//^IPayloadRegistrar^//^?}^/ registrar, PacketConfig.Login<T> login) {
        //? if > 1.20.4 {
        StreamCodec<FriendlyByteBuf, T> codec = StreamCodec.of(
                (buf, packet) -> login.encoder().accept(packet, new PacketContent(buf)),
                buf -> login.decoder().apply(new PacketContent(buf)));
        CustomPacketPayload.Type<T> type = new CustomPacketPayload.Type<>(login.id());
        registrar.configurationToClient(
                type,
                codec,
                (arg, iPayloadContext) -> login.clientHandler().accept(arg, new S2CNetworkContext())
        );
        //?} else {
        /^registrar.common(login.id(),
                buf -> login.decoder().apply(new PacketContent(buf)),
                (arg, playPayloadContext) -> playPayloadContext.workHandler().submitAsync(()-> login.clientHandler().accept(arg, new S2CNetworkContext())).exceptionally(throwable -> {
                    BrickLibAPI.LOGGER.error(throwable.getMessage());
                    return null;
                }));

        ^///?}
    }

    //? if < 1.20.6 {
    /^@SubscribeEvent
    public static void onOnGameConfiguration(OnGameConfigurationEvent event) {
        for (ResourceLocation resourceLocation : BrickRegistries.NETWORK_PACKET.keySet()) {
            PacketConfig config = BrickRegistries.NETWORK_PACKET.get(resourceLocation);
            if(config != null){
                if (event.getListener().isConnected(config.id())) {
                    if (config instanceof PacketConfig.Login login) {
                        if(!LogInReplyPacket.class.isAssignableFrom(login.type())) {
                            event.register(new MyICustomConfigurationTask(login, event));
                        }
                    }
                }
            }
        }
    }
    ^///?} else {
    @SubscribeEvent
    public static void onRegisterConfigurationTasks(RegisterConfigurationTasksEvent event) {
        for (ResourceLocation resourceLocation : BrickRegistries.NETWORK_PACKET.keySet()) {
            PacketConfig config = BrickRegistries.NETWORK_PACKET.get(resourceLocation);
            if(config != null){
                if (event.getListener().hasChannel(config.id())) {
                    if (config instanceof PacketConfig.Login login) {
                        if(!LogInReplyPacket.class.isAssignableFrom(login.type())) {
                            event.register(new MyICustomConfigurationTask(login, event));
                        }
                    }
                }
            }
        }
    }

    //?}

    private static class MyICustomConfigurationTask implements ICustomConfigurationTask {
        private final PacketConfig.Login config;
        private final
        //? if < 1.20.6 {
        /^OnGameConfigurationEvent
        ^///?} else {
                RegisterConfigurationTasksEvent
                //?}
                event;

        public MyICustomConfigurationTask(PacketConfig.Login config,
                                          //? if < 1.20.6 {
                /^OnGameConfigurationEvent
                                          ^///?} else {
                                          RegisterConfigurationTasksEvent
                                                  //?}
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

    // ========================
    // Capability Registration (NeoForge)
    // ========================

    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        BrickRegistries.CAPABILITY_ITEM.forEach(entry -> {
            var block = entry.block();
            var provider = entry.provider();
            var dirtyNotifier = entry.dirtyNotifier();
            event.registerBlock(
                    Capabilities.ItemHandler.BLOCK,
                    (level, pos, state, be, side) -> {
                        if (!(level instanceof ServerLevel serverLevel)) return null;
                        var ucs = provider.getItem(serverLevel, pos, state, be, side);
                        if (ucs != null) {
                            return new NeoForgeItemHandlerWrapper(serverLevel, pos, state, ucs, dirtyNotifier);
                        }
                        return null;
                    },
                    block
            );
        });
        BrickRegistries.CAPABILITY_ENERGY.forEach(entry -> {
            var block = entry.block();
            var provider = entry.provider();
            var dirtyNotifier = entry.dirtyNotifier();
            event.registerBlock(
                    Capabilities.EnergyStorage.BLOCK,
                    (level, pos, state, be, side) -> {
                        if (!(level instanceof ServerLevel serverLevel)) return null;
                        var ucs = provider.getEnergy(serverLevel, pos, state, be, side);
                        if (ucs != null) {
                            return new NeoForgeEnergyStorageWrapper(serverLevel, pos, state, ucs, dirtyNotifier);
                        }
                        return null;
                    },
                    block
            );
        });
        BrickRegistries.CAPABILITY_FLUID.forEach(entry -> {
            var block = entry.block();
            var provider = entry.provider();
            var dirtyNotifier = entry.dirtyNotifier();
            event.registerBlock(
                    Capabilities.FluidHandler.BLOCK,
                    (level, pos, state, be, side) -> {
                        if (!(level instanceof ServerLevel serverLevel)) return null;
                        var ucs = provider.getFluid(serverLevel, pos, state, be, side);
                        if (ucs != null) {
                            return new NeoForgeFluidHandlerWrapper(serverLevel, pos, state, ucs, dirtyNotifier);
                        }
                        return null;
                    },
                    block
            );
        });
    }

    // ========================
    // NeoForge Wrapper — 物品
    // ========================

    @SuppressWarnings("unused")
    public static class NeoForgeItemHandlerWrapper implements IItemHandler {
        private final ServerLevel level;
        private final BlockPos pos;
        private final BlockState state;
        private final IItemStorage ucs;
        @Nullable
        private final java.util.function.BiConsumer<ServerLevel, BlockPos> dirtyNotifier;

        public NeoForgeItemHandlerWrapper(ServerLevel level, BlockPos pos, BlockState state,
                                          IItemStorage ucs,
                                          @Nullable java.util.function.BiConsumer<ServerLevel, BlockPos> dirtyNotifier) {
            this.level = level;
            this.pos = pos;
            this.state = state;
            this.ucs = ucs;
            this.dirtyNotifier = dirtyNotifier;
        }

        private boolean isValid() {
            return level.getBlockState(pos).is(state.getBlock());
        }

        private boolean isValidSlot(int slot) {
            return slot >= 0 && slot < ucs.getSlots();
        }

        @Override public int getSlots() {
            return isValid() ? ucs.getSlots() : 0;
        }

        @Override public ItemStack getStackInSlot(int slot) {
            if (!isValid() || !isValidSlot(slot)) return ItemStack.EMPTY;
            ItemStack stack = ucs.getStackInSlot(slot);
            long amount = ucs.getAmountInSlot(slot);
            if (stack == null || stack.isEmpty() || amount <= 0) return ItemStack.EMPTY;
            ItemStack copy = stack.copy();
            copy.setCount(IFluidStorage.clampToInt(amount));
            return copy;
        }

        @Override public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
            if (!isValid() || !isValidSlot(slot) || stack == null || stack.isEmpty()) return stack;
            if (!ucs.isItemValid(slot, stack)) return stack;
            try (BrickTransaction tx = BrickTransaction.openOuter()) {
                long inserted = ucs.insertItem(slot, stack, stack.getCount(), tx);
                if (inserted <= 0) return stack;
                if (!simulate) {
                    tx.commit();
                    markDirty();
                }
                return remainder(stack, inserted);
            }
        }

        @Override public ItemStack extractItem(int slot, int amount, boolean simulate) {
            if (!isValid() || !isValidSlot(slot) || amount <= 0) return ItemStack.EMPTY;
            ItemStack current = getStackInSlot(slot);
            if (current.isEmpty()) return ItemStack.EMPTY;
            try (BrickTransaction tx = BrickTransaction.openOuter()) {
                long extracted = ucs.extractItem(slot, amount, tx);
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

        @Override public int getSlotLimit(int slot) {
            if (!isValid() || !isValidSlot(slot)) return 0;
            return IFluidStorage.clampToInt(ucs.getSlotCapacity(slot));
        }

        @Override public boolean isItemValid(int slot, ItemStack stack) {
            return isValid() && isValidSlot(slot) && stack != null && !stack.isEmpty() && ucs.isItemValid(slot, stack);
        }

        private ItemStack remainder(ItemStack original, long inserted) {
            int left = original.getCount() - IFluidStorage.clampToInt(inserted);
            if (left <= 0) return ItemStack.EMPTY;
            ItemStack result = original.copy();
            result.setCount(left);
            return result;
        }

        private void markDirty() {
            if (dirtyNotifier != null) dirtyNotifier.accept(level, pos);
        }
    }

    // ========================
    // NeoForge Wrapper — 能量
    // ========================

    @SuppressWarnings("unused")
    public static class NeoForgeEnergyStorageWrapper implements net.neoforged.neoforge.energy.IEnergyStorage {
        private final ServerLevel level;
        private final BlockPos pos;
        private final BlockState state;
        private final com.arc_studio.brick_lib_api.core.data.capability.IEnergyStorage ucs;
        @Nullable
        private final java.util.function.BiConsumer<ServerLevel, BlockPos> dirtyNotifier;

        public NeoForgeEnergyStorageWrapper(ServerLevel level, BlockPos pos, BlockState state,
                                             com.arc_studio.brick_lib_api.core.data.capability.IEnergyStorage ucs,
                                             @Nullable java.util.function.BiConsumer<ServerLevel, BlockPos> dirtyNotifier) {
            this.level = level;
            this.pos = pos;
            this.state = state;
            this.ucs = ucs;
            this.dirtyNotifier = dirtyNotifier;
        }

        private boolean isValid() {
            return level.getBlockState(pos).is(state.getBlock());
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!isValid() || maxReceive <= 0 || !ucs.canReceive()) return 0;
            if (simulate) {
                return IFluidStorage.clampToInt(Math.min(maxReceive,
                        ucs.getMaxEnergyStored() - ucs.getEnergyStored()));
            }
            try (BrickTransaction tx = BrickTransaction.openOuter()) {
                long received = ucs.receiveEnergy(maxReceive, tx);
                if (received > 0) {
                    tx.commit();
                    markDirty();
                }
                return IFluidStorage.clampToInt(received);
            }
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (!isValid() || maxExtract <= 0 || !ucs.canExtract()) return 0;
            if (simulate) {
                return IFluidStorage.clampToInt(Math.min(maxExtract, ucs.getEnergyStored()));
            }
            try (BrickTransaction tx = BrickTransaction.openOuter()) {
                long extracted = ucs.extractEnergy(maxExtract, tx);
                if (extracted > 0) {
                    tx.commit();
                    markDirty();
                }
                return IFluidStorage.clampToInt(extracted);
            }
        }

        @Override public int getEnergyStored() { return isValid() ? IFluidStorage.clampToInt(ucs.getEnergyStored()) : 0; }
        @Override public int getMaxEnergyStored() { return isValid() ? IFluidStorage.clampToInt(ucs.getMaxEnergyStored()) : 0; }
        @Override public boolean canExtract() { return isValid() && ucs.canExtract(); }
        @Override public boolean canReceive() { return isValid() && ucs.canReceive(); }

        private void markDirty() {
            if (dirtyNotifier != null) dirtyNotifier.accept(level, pos);
        }
    }

    @SuppressWarnings("unused")
    public static class NeoForgeFluidHandlerWrapper implements IFluidHandler {
        private final ServerLevel level;
        private final BlockPos pos;
        private final BlockState state;
        private final com.arc_studio.brick_lib_api.core.data.capability.IFluidStorage ucs;
        @Nullable
        private final java.util.function.BiConsumer<ServerLevel, BlockPos> dirtyNotifier;

        public NeoForgeFluidHandlerWrapper(ServerLevel level, BlockPos pos, BlockState state,
                                            com.arc_studio.brick_lib_api.core.data.capability.IFluidStorage ucs,
                                            @Nullable java.util.function.BiConsumer<ServerLevel, BlockPos> dirtyNotifier) {
            this.level = level;
            this.pos = pos;
            this.state = state;
            this.ucs = ucs;
            this.dirtyNotifier = dirtyNotifier;
        }

        private boolean isValid() { return level.getBlockState(pos).is(state.getBlock()); }
        private SimpleFluidStorage simple() { return ucs instanceof SimpleFluidStorage sfs ? sfs : null; }

        @Override public int getTanks() {
            if (!isValid()) return 1;
            SimpleFluidStorage s = simple();
            return s != null ? s.getTanks() : 1;
        }

        @Override public FluidStack getFluidInTank(int tank) {
            if (!isValid()) return FluidStack.EMPTY;
            SimpleFluidStorage s = simple();
            if (s == null) return FluidStack.EMPTY;
            var fluid = s.getFluidInTank(tank);
            if (fluid == null) return FluidStack.EMPTY;
            return new FluidStack(fluid, IFluidStorage.dropletsToMb(s.getFluidAmountInTank(tank)));
        }

        @Override public int getTankCapacity(int tank) {
            if (!isValid()) return 0;
            SimpleFluidStorage s = simple();
            return s != null ? IFluidStorage.dropletsToMb(s.getTankCapacity(tank)) : 0;
        }

        @Override public boolean isFluidValid(int tank, FluidStack stack) {
            if (!isValid() || stack.isEmpty()) return false;
            SimpleFluidStorage s = simple();
            return s != null && s.isFluidValid(tank, stack.getFluid());
        }

        @Override public int fill(FluidStack resource, IFluidHandler.FluidAction action) {
            if (!isValid() || resource.isEmpty()) return 0;
            SimpleFluidStorage s = simple();
            if (s == null) return 0;
            if (action.simulate()) {
                return IFluidStorage.getFillableMb(s, resource.getFluid(), resource.getAmount());
            }
            long droplets = IFluidStorage.mbToDroplets(resource.getAmount());
            try (BrickTransaction tx = BrickTransaction.openOuter()) {
                long filled = s.fill(resource.getFluid(), droplets, tx);
                if (filled > 0) {
                    tx.commit();
                    markDirty();
                }
                return IFluidStorage.dropletsToMb(filled);
            }
        }

        @Override public FluidStack drain(FluidStack resource, IFluidHandler.FluidAction action) {
            if (!isValid() || resource.isEmpty()) return FluidStack.EMPTY;
            SimpleFluidStorage s = simple();
            if (s == null) return FluidStack.EMPTY;
            if (action.simulate()) {
                int drained = IFluidStorage.getDrainableMb(s, resource.getFluid(), resource.getAmount());
                return drained > 0 ? new FluidStack(resource.getFluid(), drained) : FluidStack.EMPTY;
            }
            long droplets = IFluidStorage.mbToDroplets(resource.getAmount());
            try (BrickTransaction tx = BrickTransaction.openOuter()) {
                long drained = s.drain(resource.getFluid(), droplets, tx);
                if (drained > 0) {
                    tx.commit();
                    markDirty();
                    return new FluidStack(resource.getFluid(), IFluidStorage.dropletsToMb(drained));
                }
                return FluidStack.EMPTY;
            }
        }

        @Override public FluidStack drain(int maxDrain, IFluidHandler.FluidAction action) {
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
                long drained = s.drain(droplets, tx);
                if (drained > 0) {
                    tx.commit();
                    markDirty();
                    return new FluidStack(fluid, IFluidStorage.dropletsToMb(drained));
                }
                return FluidStack.EMPTY;
            }
        }

        private void markDirty() {
            if (dirtyNotifier != null) dirtyNotifier.accept(level, pos);
        }
    }

    // ===========================
    //  Platform 委托实现 (NeoForge)
    // ===========================

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
        type.setNeoForge();
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
        Minecraft instance = Minecraft.getInstance();
        if (instance.isSameThread()) {
            CompletableFuture.completedFuture(null);
        }
        instance.submit(runnable).exceptionally(
                ex -> {
                    BrickLibAPI.LOGGER.error("Failed to process a synchronized task of the payload: %s".formatted(context.direction()), ex);
                    return null;
                }
        );
    }

    public static <T extends C2SNetworkContext> void enqueueWork(T context, Runnable runnable) {
        MinecraftServer instance = Constants.currentServer();
        if (instance.isSameThread()) {
            CompletableFuture.completedFuture(null);
        }
        instance.submit(runnable).exceptionally(
                ex -> {
                    BrickLibAPI.LOGGER.error("Failed to process a synchronized task of the payload: %s".formatted(context.direction()), ex);
                    return null;
                }
        );
    }

    public static void sendToPlayer(ICHandlePacket packet, Iterable<ServerPlayer> serverPlayers) {
        for (ServerPlayer serverPlayer : serverPlayers) {
            //? if <=1.20.4 {
            /^PacketDistributor.PLAYER.with(serverPlayer).send(packet);
            ^///?} else {
            PacketDistributor.sendToPlayer(serverPlayer, packet);
            //?}
        }
    }

    public static void sendToServer(ISHandlePacket packet) {
        //? if <=1.20.4 {
        /^PacketDistributor.SERVER.noArg().send(packet);
        ^///?} else {
        PacketDistributor.sendToServer(packet);
        //?}
    }

    public static Set<ResourceLocation> networkChannels(Connection connection, ConnectionProtocol protocol) {
        NetworkPayloadSetup payloadSetup = (NetworkPayloadSetup) connection.channel().attr(AttributeKey.valueOf("neoforge:payload_setup")).get();
        if (payloadSetup == null) {
            return getKnownAdHocChannelsOfOtherEnd(connection);
        }
        if (protocol != null) {
            //? if =1.20.4 {
            /^HashSet<ResourceLocation> set = new HashSet<>();
            set.addAll(payloadSetup.play().keySet());
            set.addAll(payloadSetup.configuration().keySet());
            return set;
            ^///?} else {
            return payloadSetup.getChannels(protocol).keySet();
            //?}
        } else {
            HashSet<ResourceLocation> set = new HashSet<>();
            //? if =1.20.4 {
            /^set.addAll(payloadSetup.play().keySet());
            set.addAll(payloadSetup.configuration().keySet());
            ^///?} else {
            payloadSetup.channels().values().stream().map(Map::keySet).forEach(set::addAll);
            //?}
            return set;
        }
    }

    private static Set<ResourceLocation> getKnownAdHocChannelsOfOtherEnd(Connection connection) {
        var map = connection.channel().attr(AttributeKey.valueOf("neoforge:adhoc_channels")).get();
        if (map == null) {
            map = new HashSet<>();
            connection.channel().attr(AttributeKey.valueOf("neoforge:adhoc_channels")).set(map);
        }
        return (Set<ResourceLocation>) map;
    }

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
        HashMap<Pair<VillagerProfession,Integer>,ArrayList<VillagerTrades.ItemListing>> map = new HashMap<>();
        for (VillagerTradeEntry entry : BrickRegistries.VILLAGER_TRADE) {
            //? if >= 1.21.5 {
            Pair<VillagerProfession, Integer> key = Pair.of(BuiltInRegistries.VILLAGER_PROFESSION.getValueOrThrow(entry.profession()), entry.level());
            //?} else {
            /^Pair<VillagerProfession, Integer> key = Pair.of(entry.profession(), entry.level());
            ^///?}
            ArrayList<VillagerTrades.ItemListing> list = map.getOrDefault(key, new ArrayList<>());
            list.add(entry.trade());
            map.put(key, list);
        }
        map.forEach((pair, itemListings) -> Platform.registerVillagerOffers(pair.getKey(), pair.getValue(), itemListings));
        HashMap<Integer,ArrayList<VillagerTrades.ItemListing>> map1 = new HashMap<>();
        for (VillagerTradeEntry entry : BrickRegistries.WANDERING_TRADE) {
            ArrayList<VillagerTrades.ItemListing> list = map1.getOrDefault(entry.level(), new ArrayList<>());
            list.add(entry.trade());
            map1.put(entry.level(), list);
        }
        map1.forEach(Platform::registerWanderingOffers);
    }

    *///?}
}

