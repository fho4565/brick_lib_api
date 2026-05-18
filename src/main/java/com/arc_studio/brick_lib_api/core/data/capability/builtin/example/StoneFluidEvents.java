package com.arc_studio.brick_lib_api.core.data.capability.builtin.example;

import com.arc_studio.brick_lib_api.core.register.BrickRegisterManager;
import com.arc_studio.brick_lib_api.register.BrickRegistries;

//? if forge {

import net.minecraft.world.InteractionResult;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
//? if >= 1.19 {
import net.minecraftforge.event.level.ChunkEvent;
//?} else {
/*import net.minecraftforge.event.world.ChunkEvent;
*///?}
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import com.arc_studio.brick_lib_api.core.data.capability.builtin.impl.SimpleFluidStorage;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
//?}

//? if neoforge {

/*import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction;
//? if < 1.20.6 {
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.common.NeoForge;
//?} else {
/^import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.ChunkEvent;
import net.neoforged.neoforge.common.NeoForge;
^///?}
*///?}

//? if fabric {
/*import com.arc_studio.brick_lib_api.core.data.capability.builtin.impl.SimpleFluidStorage;
import com.arc_studio.brick_lib_api.core.data.capability.compat.FabricTransferAdapter;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.minecraft.world.level.block.Blocks;
*///?}

/**
 * 石头流体存储的跨加载器事件注册
 * <p>
 * - Fabric: 将石头的流体存储注册到 Fabric Transfer API（BlockApiLookup），使其他模组可通过 Fabric API 访问<br>
 * - Forge: 注册 BlockEntityType + {@code PlayerInteractEvent.RightClickBlock} + {@code ChunkEvent.Load}<br>
 * - NeoForge: 注册 BlockEntityType（或 RegisterCapabilitiesEvent for &gt;= 1.20.6）+ 事件
 * </p>
 */
public final class StoneFluidEvents {

    //? if forge {
    private static final ResourceLocation CHEST_FLUID_CAPABILITY_ID =
            new ResourceLocation("brick_lib_api", "chest_fluid");
    //?}

    private StoneFluidEvents() {
    }

    /**
     * 在模组初始化时调用，注册各加载器的事件/API
     */
    public static void register() {
        //? if fabric {
        /*registerFabricLookup();
        *///?}

        //? if forge {
        // 保留 BlockEntityType 注册仅用于兼容旧世界中已经保存的 brick_lib_api:stone_fluid 方块实体。
        // 新逻辑不再替换原版 ChestBlockEntity，而是在 AttachCapabilitiesEvent 中给现有箱子附加流体能力。
        registerBlockEntity();
        MinecraftForge.EVENT_BUS.register(StoneFluidEvents.class);
        //?}

        //? if neoforge {
        /*registerBlockEntity();
        NeoForge.EVENT_BUS.register(StoneFluidEvents.class);
        *///?}
    }

    //? if neoforge {
    /*public static void registerModBus(IEventBus modBus) {
        modBus.addListener(StoneFluidEvents::onRegisterCapabilities);
    }
    *///?}

    // ========================
    // BlockEntity 注册（Forge / NeoForge）
    // ========================

    //? if forge {
    private static void registerBlockEntity() {
        BrickRegisterManager.register(
                BrickRegistries.BLOCK_ENTITY_TYPE,
                new net.minecraft.resources.ResourceLocation("brick_lib_api", "stone_fluid"),
                () -> StoneFluidBlockEntity.TYPE = BlockEntityType.Builder.of(
                        StoneFluidBlockEntity::new, Blocks.CHEST
                ).build(null)
        );
    }
    //?}

    //? if neoforge {
    /*private static void registerBlockEntity() {
        BrickRegisterManager.register(
                BrickRegistries.BLOCK_ENTITY_TYPE,
                //? if >= 1.21 {
                /^net.minecraft.resources.ResourceLocation.fromNamespaceAndPath("brick_lib_api", "stone_fluid"),
                ^///?} else {
                new net.minecraft.resources.ResourceLocation("brick_lib_api", "stone_fluid"),
                //?}
                () -> {
                    //? if >= 1.21.3 {
                    /^return StoneFluidBlockEntity.TYPE = new BlockEntityType<>(
                            StoneFluidBlockEntity::new, Blocks.CHEST
                    );
                    ^///?} else {
                    return StoneFluidBlockEntity.TYPE = BlockEntityType.Builder.of(
                        StoneFluidBlockEntity::new, Blocks.CHEST
                    ).build(null);
                    //?}
                }
        );
    }
    *///?}

    // ========================
    // Fabric: 注册 BlockApiLookup
    // ========================

    //? if fabric {
    /*private static void registerFabricLookup() {
        // 将石头的 SimpleFluidStorage 通过 Fabric Transfer API 暴露给其他模组
        FluidStorage.SIDED.registerForBlocks((world, pos, state, blockEntity, direction) -> {
            if (!state.is(Blocks.CHEST)) return null;
            if (!(world instanceof net.minecraft.server.level.ServerLevel serverLevel)) return null;

            StoneFluidData data = StoneFluidData.get(serverLevel);
            SimpleFluidStorage storage = data.getOrCreate(pos);

            // 将 UCS SimpleFluidStorage (IFluidHandler) 包装为 Fabric Storage<FluidVariant>
            return FabricTransferAdapter.wrapAsFluidStorage(storage);
        }, Blocks.CHEST);
    }
    *///?}

    // ========================
    // Forge: 事件处理
    // ========================

    //? if forge {

    @SubscribeEvent
    public static void onAttachBlockEntityCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        BlockEntity blockEntity = event.getObject();
        if (blockEntity instanceof ChestBlockEntity) {
            event.addCapability(CHEST_FLUID_CAPABILITY_ID, new ForgeChestFluidProvider(blockEntity));
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

        InteractionResult result = StoneFluidInteraction.handleUseOnStone(
                state, level, pos, player, hand, event.getHitVec()
        );
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }

    private static class ForgeChestFluidProvider implements ICapabilityProvider {
        private final BlockEntity blockEntity;
        private final LazyOptional<net.minecraftforge.fluids.capability.IFluidHandler> fluidHandler;

        ForgeChestFluidProvider(BlockEntity blockEntity) {
            this.blockEntity = blockEntity;
            this.fluidHandler = LazyOptional.of(() -> new ForgeChestFluidHandler(blockEntity));
        }

        @Override
        public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
            //? if >= 1.19.3 {
            if (cap == net.minecraftforge.common.capabilities.ForgeCapabilities.FLUID_HANDLER) {
            //?} else {
            /*if (cap == net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            *///?}
                return fluidHandler.cast();
            }
            return LazyOptional.empty();
        }
    }

    private static class ForgeChestFluidHandler implements net.minecraftforge.fluids.capability.IFluidHandler {
        private final BlockEntity blockEntity;

        ForgeChestFluidHandler(BlockEntity blockEntity) {
            this.blockEntity = blockEntity;
        }

        private SimpleFluidStorage storage() {
            var level = blockEntity.getLevel();
            var pos = blockEntity.getBlockPos();
            if (level instanceof ServerLevel serverLevel
                    && blockEntity.getBlockState().is(Blocks.CHEST)
                    && level.getBlockState(pos).is(Blocks.CHEST)) {
                return StoneFluidData.get(serverLevel).getOrCreate(pos);
            }
            return null;
        }

        @Override
        public int getTanks() {
            SimpleFluidStorage s = storage();
            return s != null ? s.getTanks() : 1;
        }

        @Override
        public @NotNull FluidStack getFluidInTank(int tank) {
            SimpleFluidStorage s = storage();
            if (s == null) return FluidStack.EMPTY;
            Fluid fluid = s.getFluidInTank(tank);
            if (fluid == null) return FluidStack.EMPTY;
            return new FluidStack(fluid, dropletsToMb(s.getFluidAmountInTank(tank)));
        }

        @Override
        public int getTankCapacity(int tank) {
            SimpleFluidStorage s = storage();
            return s != null ? dropletsToMb(s.getTankCapacity(tank)) : 0;
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            SimpleFluidStorage s = storage();
            return s != null && !stack.isEmpty() && s.isFluidValid(tank, stack.getFluid());
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            SimpleFluidStorage s = storage();
            if (s == null || resource.isEmpty()) return 0;
            if (!action.execute()) {
                return getFillableMb(s, resource.getFluid(), resource.getAmount());
            }
            long droplets = mbToDroplets(resource.getAmount());
            try (Transaction tx = Transaction.openOuter()) {
                long filled = s.fill(resource.getFluid(), droplets, tx);
                if (action.execute() && filled > 0) {
                    tx.commit();
                    markDataDirty();
                }
                return dropletsToMb(filled);
            }
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            SimpleFluidStorage s = storage();
            if (s == null || resource.isEmpty()) return FluidStack.EMPTY;
            if (!action.execute()) {
                int drained = getDrainableMb(s, resource.getFluid(), resource.getAmount());
                return drained > 0 ? new FluidStack(resource.getFluid(), drained) : FluidStack.EMPTY;
            }
            long droplets = mbToDroplets(resource.getAmount());
            try (Transaction tx = Transaction.openOuter()) {
                long drained = s.drain(resource.getFluid(), droplets, tx);
                if (action.execute() && drained > 0) {
                    tx.commit();
                    markDataDirty();
                }
                return drained > 0 ? new FluidStack(resource.getFluid(), dropletsToMb(drained)) : FluidStack.EMPTY;
            }
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            SimpleFluidStorage s = storage();
            if (s == null || maxDrain <= 0) return FluidStack.EMPTY;
            Fluid fluid = s.getFluidInTank(0);
            if (fluid == null) return FluidStack.EMPTY;
            if (!action.execute()) {
                int drained = getDrainableMb(s, fluid, maxDrain);
                return drained > 0 ? new FluidStack(fluid, drained) : FluidStack.EMPTY;
            }
            try (Transaction tx = Transaction.openOuter()) {
                long drained = s.drain(mbToDroplets(maxDrain), tx);
                if (action.execute() && drained > 0) {
                    tx.commit();
                    markDataDirty();
                }
                return drained > 0 ? new FluidStack(fluid, dropletsToMb(drained)) : FluidStack.EMPTY;
            }
        }

        private void markDataDirty() {
            if (blockEntity.getLevel() instanceof ServerLevel serverLevel) {
                StoneFluidData.get(serverLevel).setDirty();
            }
        }

        private static int getFillableMb(SimpleFluidStorage storage, Fluid fluid, int maxFill) {
            Fluid storedFluid = storage.getFluidInTank(0);
            if (storedFluid != null && storedFluid != fluid) return 0;
            long remaining = storage.getTankCapacity(0) - storage.getFluidAmountInTank(0);
            return Math.min(maxFill, dropletsToMb(remaining));
        }

        private static int getDrainableMb(SimpleFluidStorage storage, Fluid fluid, int maxDrain) {
            if (storage.getFluidInTank(0) != fluid) return 0;
            return Math.min(maxDrain, dropletsToMb(storage.getFluidAmountInTank(0)));
        }

        private static long mbToDroplets(int mb) {
            return (long) mb * 81;
        }

        private static int dropletsToMb(long droplets) {
            return (int) (droplets / 81);
        }
    }
    //?}

    // ========================
    // NeoForge: 事件处理
    // ========================

    //? if neoforge {

    /*@SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        var level = event.getLevel();
        var pos = event.getPos();
        var state = level.getBlockState(pos);
        var player = event.getEntity();
        var hand = event.getHand();

        InteractionResult result = StoneFluidInteraction.handleUseOnStone(
                state, level, pos, player, hand, event.getHitVec()
        );
        if (result != InteractionResult.PASS) {
            event.setCanceled(true);
            event.setCancellationResult(result);
        }
    }
    *///?}

    //? if neoforge {
    /*/^*
     * NeoForge: 使用 RegisterCapabilitiesEvent 注册方块级流体能力。
     * 不需要替换原版 ChestBlockEntity，空箱子也必须返回 handler，
     * 这样管道可以主动发现并连接。
     ^/
    public static void onRegisterCapabilities(RegisterCapabilitiesEvent event) {
        event.registerBlock(
                Capabilities.FluidHandler.BLOCK,
                (level, pos, state, be, side) -> {
                    if (!state.is(Blocks.CHEST)) return null;
                    if (!(level instanceof ServerLevel serverLevel)) return null;
                    StoneFluidData data = StoneFluidData.get(serverLevel);
                    var storage = data.getOrCreate(pos);
                    return new NeoForgeFluidHandlerBridge(serverLevel, pos, storage);
                },
                Blocks.CHEST
        );
    }

    /^*
     * 将 UCS SimpleFluidStorage 桥接为 NeoForge IFluidHandler
     ^/
    private static class NeoForgeFluidHandlerBridge implements net.neoforged.neoforge.fluids.capability.IFluidHandler {
        private final ServerLevel level;
        private final net.minecraft.core.BlockPos pos;
        private final com.arc_studio.brick_lib_api.core.data.capability.builtin.impl.SimpleFluidStorage storage;

        NeoForgeFluidHandlerBridge(ServerLevel level, net.minecraft.core.BlockPos pos,
                                   com.arc_studio.brick_lib_api.core.data.capability.builtin.impl.SimpleFluidStorage storage) {
            this.level = level;
            this.pos = pos;
            this.storage = storage;
        }

        @Override
        public int getTanks() {
            if (!isValid()) return 1;
            return storage.getTanks();
        }

        @Override
        public net.neoforged.neoforge.fluids.FluidStack getFluidInTank(int tank) {
            if (!isValid()) return net.neoforged.neoforge.fluids.FluidStack.EMPTY;
            var fluid = storage.getFluidInTank(tank);
            if (fluid == null) return net.neoforged.neoforge.fluids.FluidStack.EMPTY;
            long amount = storage.getFluidAmountInTank(tank);
            return new net.neoforged.neoforge.fluids.FluidStack(fluid, dropletsToMb(amount));
        }

        @Override
        public int getTankCapacity(int tank) {
            if (!isValid()) return 0;
            return dropletsToMb(storage.getTankCapacity(tank));
        }

        @Override
        public boolean isFluidValid(int tank, net.neoforged.neoforge.fluids.FluidStack stack) {
            if (!isValid() || stack.isEmpty()) return false;
            return storage.isFluidValid(tank, stack.getFluid());
        }

        @Override
        public int fill(net.neoforged.neoforge.fluids.FluidStack resource, FluidAction action) {
            if (!isValid() || resource.isEmpty()) return 0;
            if (!action.execute()) {
                return getFillableMb(storage, resource.getFluid(), resource.getAmount());
            }
            long droplets = mbToDroplets(resource.getAmount());
            try (com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction tx =
                         com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction.openOuter()) {
                long filled = storage.fill(resource.getFluid(), droplets, tx);
                if (action.execute() && filled > 0) {
                    tx.commit();
                    StoneFluidData.get(level).setDirty();
                }
                return dropletsToMb(filled);
            }
        }

        @Override
        public net.neoforged.neoforge.fluids.FluidStack drain(net.neoforged.neoforge.fluids.FluidStack resource, FluidAction action) {
            if (!isValid() || resource.isEmpty()) return net.neoforged.neoforge.fluids.FluidStack.EMPTY;
            if (!action.execute()) {
                int drained = getDrainableMb(storage, resource.getFluid(), resource.getAmount());
                return drained > 0 ? new net.neoforged.neoforge.fluids.FluidStack(resource.getFluid(), drained)
                        : net.neoforged.neoforge.fluids.FluidStack.EMPTY;
            }
            long droplets = mbToDroplets(resource.getAmount());
            try (com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction tx =
                         com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction.openOuter()) {
                long drained = storage.drain(resource.getFluid(), droplets, tx);
                if (action.execute() && drained > 0) {
                    tx.commit();
                    StoneFluidData.get(level).setDirty();
                }
                return drained > 0 ? new net.neoforged.neoforge.fluids.FluidStack(resource.getFluid(), dropletsToMb(drained))
                        : net.neoforged.neoforge.fluids.FluidStack.EMPTY;
            }
        }

        @Override
        public net.neoforged.neoforge.fluids.FluidStack drain(int maxDrain, FluidAction action) {
            if (!isValid() || maxDrain <= 0) return net.neoforged.neoforge.fluids.FluidStack.EMPTY;
            var fluid = storage.getFluidInTank(0);
            if (fluid == null) return net.neoforged.neoforge.fluids.FluidStack.EMPTY;
            if (!action.execute()) {
                int drained = getDrainableMb(storage, fluid, maxDrain);
                return drained > 0 ? new net.neoforged.neoforge.fluids.FluidStack(fluid, drained)
                        : net.neoforged.neoforge.fluids.FluidStack.EMPTY;
            }
            long droplets = mbToDroplets(maxDrain);
            try (com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction tx =
                         com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction.openOuter()) {
                long drained = storage.drain(droplets, tx);
                if (action.execute() && drained > 0) {
                    tx.commit();
                    StoneFluidData.get(level).setDirty();
                }
                return drained > 0 ? new net.neoforged.neoforge.fluids.FluidStack(fluid, dropletsToMb(drained))
                        : net.neoforged.neoforge.fluids.FluidStack.EMPTY;
            }
        }

        private boolean isValid() {
            return level.getBlockState(pos).is(Blocks.CHEST);
        }

        private static int getFillableMb(com.arc_studio.brick_lib_api.core.data.capability.builtin.impl.SimpleFluidStorage storage,
                                         net.minecraft.world.level.material.Fluid fluid, int maxFill) {
            var storedFluid = storage.getFluidInTank(0);
            if (storedFluid != null && storedFluid != fluid) return 0;
            long remaining = storage.getTankCapacity(0) - storage.getFluidAmountInTank(0);
            return Math.min(maxFill, dropletsToMb(remaining));
        }

        private static int getDrainableMb(com.arc_studio.brick_lib_api.core.data.capability.builtin.impl.SimpleFluidStorage storage,
                                          net.minecraft.world.level.material.Fluid fluid, int maxDrain) {
            if (storage.getFluidInTank(0) != fluid) return 0;
            return Math.min(maxDrain, dropletsToMb(storage.getFluidAmountInTank(0)));
        }

        private static long mbToDroplets(int mb) { return (long) mb * 81; }
        private static int dropletsToMb(long droplets) { return (int) (droplets / 81); }
    }
    *///?}
}
