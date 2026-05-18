package com.arc_studio.brick_lib_api.core.data.capability.builtin.example;

//? if fabric {
/*import com.arc_studio.brick_lib_api.core.data.capability.builtin.impl.SimpleEnergyStorage;
import com.arc_studio.brick_lib_api.core.data.capability.compat.CapabilityCompat;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
*///?}

//? if forge {
import com.arc_studio.brick_lib_api.core.data.capability.builtin.impl.SimpleEnergyStorage;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.FurnaceBlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
//?}

//? if neoforge {
/*import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
*///?}

/**
 * 熔炉能量能力的跨加载器事件入口。
 * <p>
 * 当前 NeoForge 分支会注册右键交互事件，并将 UCS {@link com.arc_studio.brick_lib_api.core.data.capability.builtin.impl.SimpleEnergyStorage}
 * 桥接为 NeoForge 原生 Energy Storage 能力，方便其他模组管道查询。
 * </p>
 */
public final class FurnaceEnergyEvents {

    //? if forge {
    private static final ResourceLocation FURNACE_ENERGY_CAPABILITY_ID =
            new ResourceLocation("brick_lib_api", "furnace_energy");
    //?}

    private FurnaceEnergyEvents() {
    }

    public static void register() {
        //? if fabric {
        /*registerFabricEvents();
        registerFabricEnergyLookup();
        *///?}

        //? if forge {
        MinecraftForge.EVENT_BUS.register(FurnaceEnergyEvents.class);
        //?}

        //? if neoforge {
        /*NeoForge.EVENT_BUS.register(FurnaceEnergyEvents.class);
        *///?}
    }

    //? if fabric {
    /*private static void registerFabricEvents() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            var pos = hitResult.getBlockPos();
            var state = world.getBlockState(pos);
            InteractionResult result = FurnaceEnergyInteraction.handleUseOnFurnace(
                    state, world, pos, player, hand, hitResult
            );
            return result == InteractionResult.PASS ? InteractionResult.PASS : result;
        });
    }

    private static void registerFabricEnergyLookup() {
        CapabilityCompat.ENERGY_STORAGE_LOOKUP.registerForBlocks((world, pos, state, blockEntity, direction) -> {
            if (!state.is(Blocks.FURNACE)) return null;
            if (world instanceof ServerLevel serverLevel) {
                SimpleEnergyStorage storage = FurnaceEnergyData.get(serverLevel).getOrCreate(pos);
                return new FabricFurnaceEnergyStorage(serverLevel, pos, storage);
            }
            if (world.isClientSide) {
                return ClientFabricFurnaceEnergyStorage.INSTANCE;
            }
            return null;
        }, Blocks.FURNACE);
        registerOptionalFabricNativeEnergyBridge();
    }

    // Fabric itself provides BlockApiLookup, but no built-in standard FE-like energy interface.
    // BrickLib's own lookup above is the canonical Fabric API integration. This optional bridge
    // only mirrors the same furnace storage into a common runtime energy lookup when that API is
    // already present, so existing cables can discover the furnace without adding a hard dependency.
    private static void registerOptionalFabricNativeEnergyBridge() {
        try {
            Class<?> energyStorageClass = Class.forName("team.reborn.energy.api.EnergyStorage");
            Class<?> lookupClass = Class.forName("net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup");
            Class<?> providerClass = Class.forName("net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup$BlockApiProvider");
            Object lookup = energyStorageClass.getField("SIDED").get(null);

            Object provider = Proxy.newProxyInstance(
                    providerClass.getClassLoader(),
                    new Class<?>[]{providerClass},
                    (proxy, method, args) -> {
                        if (!"find".equals(method.getName()) || args == null || args.length < 3) {
                            return null;
                        }
                        Object world = args[0];
                        var pos = (net.minecraft.core.BlockPos) args[1];
                        var state = (net.minecraft.world.level.block.state.BlockState) args[2];
                        if (!state.is(Blocks.FURNACE)) {
                            return null;
                        }
                        if (world instanceof ServerLevel serverLevel) {
                            SimpleEnergyStorage storage = FurnaceEnergyData.get(serverLevel).getOrCreate(pos);
                            return createOptionalFabricNativeEnergyProxy(energyStorageClass, serverLevel, pos, storage);
                        }
                        if (world instanceof net.minecraft.world.level.Level && ((net.minecraft.world.level.Level) world).isClientSide) {
                            return createOptionalClientFabricNativeEnergyProxy(energyStorageClass);
                        }
                        return null;
                    }
            );

            Method registerForBlocks = lookupClass.getMethod("registerForBlocks", providerClass, Block[].class);
            registerForBlocks.invoke(lookup, provider, (Object) new Block[]{Blocks.FURNACE});
        } catch (ClassNotFoundException | NoSuchFieldException ignored) {
            // Optional bridge target is absent. BrickLib's own Fabric API lookup remains active.
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | LinkageError e) {
            com.arc_studio.brick_lib_api.BrickLibAPI.LOGGER.warn("Failed to register optional Fabric furnace energy bridge", e);
        }
    }

    private static Object createOptionalFabricNativeEnergyProxy(
            Class<?> energyStorageClass,
            ServerLevel level,
            net.minecraft.core.BlockPos pos,
            SimpleEnergyStorage storage
    ) {
        return Proxy.newProxyInstance(
                energyStorageClass.getClassLoader(),
                new Class<?>[]{energyStorageClass},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if (method.getDeclaringClass() == Object.class) {
                        if ("toString".equals(name)) return "BrickLibOptionalFabricEnergy[" + pos + "]";
                        if ("hashCode".equals(name)) return System.identityHashCode(proxy);
                        if ("equals".equals(name)) return args != null && args.length > 0 && proxy == args[0];
                    }
                    if ("insert".equals(name)) {
                        return insertOptionalFabricNativeEnergy(level, pos, storage, (Long) args[0], args[1]);
                    }
                    if ("extract".equals(name)) {
                        return extractOptionalFabricNativeEnergy(level, pos, storage, (Long) args[0], args[1]);
                    }
                    if ("getAmount".equals(name)) return level.getBlockState(pos).is(Blocks.FURNACE) ? storage.getEnergyStored() : 0L;
                    if ("getCapacity".equals(name)) return level.getBlockState(pos).is(Blocks.FURNACE) ? storage.getMaxEnergyStored() : 0L;
                    if ("supportsInsertion".equals(name)) return level.getBlockState(pos).is(Blocks.FURNACE) && storage.canReceive();
                    if ("supportsExtraction".equals(name)) return level.getBlockState(pos).is(Blocks.FURNACE) && storage.canExtract();
                    if (method.getReturnType() == boolean.class) return false;
                    if (method.getReturnType() == long.class) return 0L;
                    if (method.getReturnType() == int.class) return 0;
                    return null;
                }
        );
    }

    private static Object createOptionalClientFabricNativeEnergyProxy(Class<?> energyStorageClass) {
        return Proxy.newProxyInstance(
                energyStorageClass.getClassLoader(),
                new Class<?>[]{energyStorageClass},
                (proxy, method, args) -> {
                    String name = method.getName();
                    if (method.getDeclaringClass() == Object.class) {
                        if ("toString".equals(name)) return "BrickLibOptionalClientFabricEnergy";
                        if ("hashCode".equals(name)) return System.identityHashCode(proxy);
                        if ("equals".equals(name)) return args != null && args.length > 0 && proxy == args[0];
                    }
                    if ("getAmount".equals(name)) return 0L;
                    if ("getCapacity".equals(name)) return FurnaceEnergyData.CAPACITY;
                    if ("supportsInsertion".equals(name) || "supportsExtraction".equals(name)) return true;
                    if (method.getReturnType() == boolean.class) return false;
                    if (method.getReturnType() == long.class) return 0L;
                    if (method.getReturnType() == int.class) return 0;
                    return null;
                }
        );
    }

    private static long insertOptionalFabricNativeEnergy(
            ServerLevel level,
            net.minecraft.core.BlockPos pos,
            SimpleEnergyStorage storage,
            long maxAmount,
            Object fabricTx
    ) {
        if (!level.getBlockState(pos).is(Blocks.FURNACE) || maxAmount <= 0 || !storage.canReceive()) return 0;
        long received;
        try (com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction tx =
                     com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction.openOuter()) {
            received = storage.receiveEnergy(maxAmount, tx);
        }
        if (received <= 0) return 0;
        return runOnOptionalFabricNativeCommit(fabricTx, () -> {
            if (!level.getBlockState(pos).is(Blocks.FURNACE)) return;
            try (com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction tx =
                         com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction.openOuter()) {
                long committed = storage.receiveEnergy(received, tx);
                if (committed > 0) {
                    tx.commit();
                    FurnaceEnergyData.get(level).setDirty();
                }
            }
        }) ? received : 0;
    }

    private static long extractOptionalFabricNativeEnergy(
            ServerLevel level,
            net.minecraft.core.BlockPos pos,
            SimpleEnergyStorage storage,
            long maxAmount,
            Object fabricTx
    ) {
        if (!level.getBlockState(pos).is(Blocks.FURNACE) || maxAmount <= 0 || !storage.canExtract()) return 0;
        long extracted;
        try (com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction tx =
                     com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction.openOuter()) {
            extracted = storage.extractEnergy(maxAmount, tx);
        }
        if (extracted <= 0) return 0;
        return runOnOptionalFabricNativeCommit(fabricTx, () -> {
            if (!level.getBlockState(pos).is(Blocks.FURNACE)) return;
            try (com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction tx =
                         com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction.openOuter()) {
                long committed = storage.extractEnergy(extracted, tx);
                if (committed > 0) {
                    tx.commit();
                    FurnaceEnergyData.get(level).setDirty();
                }
            }
        }) ? extracted : 0;
    }

    private static boolean runOnOptionalFabricNativeCommit(Object fabricTx, Runnable action) {
        if (fabricTx == null) return false;
        try {
            Class<?> transactionContextClass = Class.forName("net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext");
            if (transactionContextClass.isInstance(fabricTx)) {
                for (Method method : transactionContextClass.getMethods()) {
                    if (!"addCloseCallback".equals(method.getName()) || method.getParameterCount() != 1) continue;
                    method.invoke(fabricTx, createOptionalFabricNativeCloseCallback(method.getParameterTypes()[0], action));
                    return true;
                }
            }
        } catch (ClassNotFoundException ignored) {
            // Older or absent Fabric Transfer API. Fall back to the runtime object's public methods below.
        } catch (IllegalAccessException | InvocationTargetException e) {
            com.arc_studio.brick_lib_api.BrickLibAPI.LOGGER.warn("Failed to bind optional Fabric furnace energy operation to transaction", e);
            return false;
        }

        try {
            for (Method method : fabricTx.getClass().getMethods()) {
                if (!"addCloseCallback".equals(method.getName()) || method.getParameterCount() != 1) continue;
                method.invoke(fabricTx, createOptionalFabricNativeCloseCallback(method.getParameterTypes()[0], action));
                return true;
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            com.arc_studio.brick_lib_api.BrickLibAPI.LOGGER.warn("Failed to bind optional Fabric furnace energy operation to transaction", e);
        }
        return false;
    }

    private static Object createOptionalFabricNativeCloseCallback(Class<?> callbackClass, Runnable action) {
        return Proxy.newProxyInstance(
                callbackClass.getClassLoader(),
                new Class<?>[]{callbackClass},
                (proxy, callbackMethod, args) -> {
                    String name = callbackMethod.getName();
                    if (callbackMethod.getDeclaringClass() == Object.class) {
                        if ("toString".equals(name)) return "BrickLibOptionalFabricEnergyCloseCallback";
                        if ("hashCode".equals(name)) return System.identityHashCode(proxy);
                        if ("equals".equals(name)) return args != null && args.length > 0 && proxy == args[0];
                    }
                    if (args != null && args.length > 0) {
                        Object result = args.length >= 2 ? args[1] : args[0];
                        if (isOptionalFabricNativeCommitted(result)) {
                            action.run();
                        }
                    }
                    return null;
                }
        );
    }

    private static boolean isOptionalFabricNativeCommitted(Object result) {
        if (result == null) return false;
        try {
            Method wasCommitted = result.getClass().getMethod("wasCommitted");
            return Boolean.TRUE.equals(wasCommitted.invoke(result));
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException ignored) {
            return "COMMITTED".equals(String.valueOf(result));
        }
    }

    private enum ClientFabricFurnaceEnergyStorage implements com.arc_studio.brick_lib_api.core.data.capability.builtin.IEnergyStorage {
        INSTANCE;

        @Override public long receiveEnergy(long maxReceive, com.arc_studio.brick_lib_api.core.data.capability.transaction.TransactionContext tx) { return 0; }
        @Override public long extractEnergy(long maxExtract, com.arc_studio.brick_lib_api.core.data.capability.transaction.TransactionContext tx) { return 0; }
        @Override public long getEnergyStored() { return 0; }
        @Override public long getMaxEnergyStored() { return FurnaceEnergyData.CAPACITY; }
        @Override public boolean canReceive() { return true; }
        @Override public boolean canExtract() { return true; }
    }

    private static class FabricFurnaceEnergyStorage implements com.arc_studio.brick_lib_api.core.data.capability.builtin.IEnergyStorage {
        private final ServerLevel level;
        private final net.minecraft.core.BlockPos pos;
        private final SimpleEnergyStorage storage;

        FabricFurnaceEnergyStorage(ServerLevel level, net.minecraft.core.BlockPos pos, SimpleEnergyStorage storage) {
            this.level = level;
            this.pos = pos;
            this.storage = storage;
        }

        @Override
        public long receiveEnergy(long maxReceive, com.arc_studio.brick_lib_api.core.data.capability.transaction.TransactionContext tx) {
            if (!isValid() || maxReceive <= 0 || !storage.canReceive()) return 0;
            long received = storage.receiveEnergy(maxReceive, tx);
            if (received > 0) {
                tx.addListener(new com.arc_studio.brick_lib_api.core.data.capability.transaction.TransactionListener() {
                    @Override public void beforeCommit(com.arc_studio.brick_lib_api.core.data.capability.transaction.TransactionContext tx) {}
                    @Override public void afterCommit(com.arc_studio.brick_lib_api.core.data.capability.transaction.TransactionContext tx) {
                        FurnaceEnergyData.get(level).setDirty();
                    }
                    @Override public void onAbort(com.arc_studio.brick_lib_api.core.data.capability.transaction.TransactionContext tx) {}
                });
            }
            return received;
        }

        @Override
        public long extractEnergy(long maxExtract, com.arc_studio.brick_lib_api.core.data.capability.transaction.TransactionContext tx) {
            if (!isValid() || maxExtract <= 0 || !storage.canExtract()) return 0;
            long extracted = storage.extractEnergy(maxExtract, tx);
            if (extracted > 0) {
                tx.addListener(new com.arc_studio.brick_lib_api.core.data.capability.transaction.TransactionListener() {
                    @Override public void beforeCommit(com.arc_studio.brick_lib_api.core.data.capability.transaction.TransactionContext tx) {}
                    @Override public void afterCommit(com.arc_studio.brick_lib_api.core.data.capability.transaction.TransactionContext tx) {
                        FurnaceEnergyData.get(level).setDirty();
                    }
                    @Override public void onAbort(com.arc_studio.brick_lib_api.core.data.capability.transaction.TransactionContext tx) {}
                });
            }
            return extracted;
        }

        @Override public long getEnergyStored() { return isValid() ? storage.getEnergyStored() : 0; }
        @Override public long getMaxEnergyStored() { return isValid() ? storage.getMaxEnergyStored() : 0; }
        @Override public boolean canReceive() { return isValid() && storage.canReceive(); }
        @Override public boolean canExtract() { return isValid() && storage.canExtract(); }

        private boolean isValid() {
            return level.getBlockState(pos).is(Blocks.FURNACE);
        }
    }
    *///?}

    //? if forge {
    @SubscribeEvent
    public static void onAttachBlockEntityCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        BlockEntity blockEntity = event.getObject();
        if (blockEntity instanceof FurnaceBlockEntity) {
            event.addCapability(FURNACE_ENERGY_CAPABILITY_ID, new ForgeFurnaceEnergyProvider(blockEntity));
        }
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        //? if >= 1.19 {
        var level = event.getLevel();
        //?} else {
        /*var level = event.getWorld();
        *///?}
        var pos = event.getPos();
        var state = level.getBlockState(pos);
        //? if >= 1.19 {
        var player = event.getEntity();
        //?} else {
        /*var player = event.getPlayer();
        *///?}
        var hand = event.getHand();

        InteractionResult result = FurnaceEnergyInteraction.handleUseOnFurnace(
                state, level, pos, player, hand, event.getHitVec()
        );
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    private static class ForgeFurnaceEnergyProvider implements ICapabilityProvider {
        private final LazyOptional<net.minecraftforge.energy.IEnergyStorage> energyHandler;

        ForgeFurnaceEnergyProvider(BlockEntity blockEntity) {
            this.energyHandler = LazyOptional.of(() -> new ForgeFurnaceEnergyStorage(blockEntity));
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            //? if >= 1.19.3 {
            if (cap == net.minecraftforge.common.capabilities.ForgeCapabilities.ENERGY) {
            //?} else {
            /*if (cap == net.minecraftforge.energy.CapabilityEnergy.ENERGY) {
            *///?}
                return energyHandler.cast();
            }
            return LazyOptional.empty();
        }
    }

    private static class ForgeFurnaceEnergyStorage implements net.minecraftforge.energy.IEnergyStorage {
        private final BlockEntity blockEntity;

        ForgeFurnaceEnergyStorage(BlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        @Nullable
        private SimpleEnergyStorage storage() {
            var level = blockEntity.getLevel();
            var pos = blockEntity.getBlockPos();
            if (level instanceof ServerLevel serverLevel
                    && blockEntity.getBlockState().is(Blocks.FURNACE)
                    && level.getBlockState(pos).is(Blocks.FURNACE)) {
                return FurnaceEnergyData.get(serverLevel).getOrCreate(pos);
            }
            return null;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            SimpleEnergyStorage s = storage();
            if (s == null || maxReceive <= 0 || !s.canReceive()) return 0;
            if (simulate) {
                return clampToInt(Math.min(FurnaceEnergyData.TRANSFER_AMOUNT,
                        Math.min(maxReceive, s.getMaxEnergyStored() - s.getEnergyStored())));
            }
            try (Transaction tx = Transaction.openOuter()) {
                long received = s.receiveEnergy(maxReceive, tx);
                if (received > 0) {
                    tx.commit();
                    markDataDirty();
                }
                return clampToInt(received);
            }
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            SimpleEnergyStorage s = storage();
            if (s == null || maxExtract <= 0 || !s.canExtract()) return 0;
            if (simulate) {
                return clampToInt(Math.min(FurnaceEnergyData.TRANSFER_AMOUNT,
                        Math.min(maxExtract, s.getEnergyStored())));
            }
            try (Transaction tx = Transaction.openOuter()) {
                long extracted = s.extractEnergy(maxExtract, tx);
                if (extracted > 0) {
                    tx.commit();
                    markDataDirty();
                }
                return clampToInt(extracted);
            }
        }

        @Override
        public int getEnergyStored() {
            SimpleEnergyStorage s = storage();
            return s != null ? clampToInt(s.getEnergyStored()) : 0;
        }

        @Override
        public int getMaxEnergyStored() {
            SimpleEnergyStorage s = storage();
            return s != null ? clampToInt(s.getMaxEnergyStored()) : 0;
        }

        @Override
        public boolean canExtract() {
            SimpleEnergyStorage s = storage();
            return s != null && s.canExtract();
        }

        @Override
        public boolean canReceive() {
            SimpleEnergyStorage s = storage();
            return s != null && s.canReceive();
        }

        private void markDataDirty() {
            if (blockEntity.getLevel() instanceof ServerLevel serverLevel) {
                FurnaceEnergyData.get(serverLevel).setDirty();
            }
        }

        private static int clampToInt(long value) {
            return value > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) Math.max(0, value);
        }
    }
    //?}

    //? if neoforge {
    /*public static void registerModBus(IEventBus modBus) {
        modBus.addListener(FurnaceEnergyEvents::onRegisterCapabilities);
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        var level = event.getLevel();
        var pos = event.getPos();
        var state = level.getBlockState(pos);
        var player = event.getEntity();
        var hand = event.getHand();

        InteractionResult result = FurnaceEnergyInteraction.handleUseOnFurnace(
                state, level, pos, player, hand, event.getHitVec()
        );
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlock(
                Capabilities.EnergyStorage.BLOCK,
                (level, pos, state, be, side) -> {
                    if (!state.is(Blocks.FURNACE)) return null;
                    if (!(level instanceof ServerLevel serverLevel)) return null;
                    var storage = FurnaceEnergyData.get(serverLevel).getOrCreate(pos);
                    return new NeoForgeEnergyStorageBridge(serverLevel, pos, storage);
                },
                Blocks.FURNACE
        );
    }

    private static class NeoForgeEnergyStorageBridge implements net.neoforged.neoforge.energy.IEnergyStorage {
        private final ServerLevel level;
        private final net.minecraft.core.BlockPos pos;
        private final com.arc_studio.brick_lib_api.core.data.capability.builtin.impl.SimpleEnergyStorage storage;

        NeoForgeEnergyStorageBridge(
                ServerLevel level,
                net.minecraft.core.BlockPos pos,
                com.arc_studio.brick_lib_api.core.data.capability.builtin.impl.SimpleEnergyStorage storage
        ) {
            this.level = level;
            this.pos = pos;
            this.storage = storage;
        }

        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) {
            if (!isValid() || maxReceive <= 0 || !storage.canReceive()) return 0;
            if (simulate) {
                return clampToInt(Math.min(FurnaceEnergyData.TRANSFER_AMOUNT,
                        Math.min(maxReceive, storage.getMaxEnergyStored() - storage.getEnergyStored())));
            }

            try (com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction tx =
                         com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction.openOuter()) {
                long received = storage.receiveEnergy(maxReceive, tx);
                if (received > 0) {
                    tx.commit();
                    FurnaceEnergyData.get(level).setDirty();
                }
                return clampToInt(received);
            }
        }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) {
            if (!isValid() || maxExtract <= 0 || !storage.canExtract()) return 0;
            if (simulate) {
                return clampToInt(Math.min(FurnaceEnergyData.TRANSFER_AMOUNT,
                        Math.min(maxExtract, storage.getEnergyStored())));
            }

            try (com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction tx =
                         com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction.openOuter()) {
                long extracted = storage.extractEnergy(maxExtract, tx);
                if (extracted > 0) {
                    tx.commit();
                    FurnaceEnergyData.get(level).setDirty();
                }
                return clampToInt(extracted);
            }
        }

        @Override
        public int getEnergyStored() {
            if (!isValid()) return 0;
            return clampToInt(storage.getEnergyStored());
        }

        @Override
        public int getMaxEnergyStored() {
            if (!isValid()) return 0;
            return clampToInt(storage.getMaxEnergyStored());
        }

        @Override
        public boolean canExtract() {
            return isValid() && storage.canExtract();
        }

        @Override
        public boolean canReceive() {
            return isValid() && storage.canReceive();
        }

        private boolean isValid() {
            return level.getBlockState(pos).is(Blocks.FURNACE);
        }

        private static int clampToInt(long value) {
            return value > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) Math.max(0, value);
        }
    }
    *///?}
}

