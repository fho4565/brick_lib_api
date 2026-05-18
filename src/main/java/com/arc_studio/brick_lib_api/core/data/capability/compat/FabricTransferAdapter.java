package com.arc_studio.brick_lib_api.core.data.capability.compat;

//? if fabric {
/*import com.arc_studio.brick_lib_api.core.data.capability.builtin.BuiltinCapabilities;
import com.arc_studio.brick_lib_api.core.data.capability.builtin.IFluidHandler;
import com.arc_studio.brick_lib_api.core.data.capability.builtin.IItemHandler;
import com.arc_studio.brick_lib_api.core.data.capability.provider.CapabilityProvider;
import com.arc_studio.brick_lib_api.core.data.capability.provider.LazyOptional;
import com.arc_studio.brick_lib_api.core.data.capability.provider.ProviderRegistry;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.TransactionContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/^*
 * Fabric Transfer API 适配器。
 * <p>
 * 负责 Fabric {@code Storage<ItemVariant>} / {@code Storage<FluidVariant>} 与 UCS 内置
 * {@link IItemHandler} / {@link IFluidHandler} 的双向桥接，并通过 Fabric fallback 将 UCS 能力暴露给其他模组。
 * </p>
 ^/
public final class FabricTransferAdapter {

    private static volatile boolean FALLBACKS_REGISTERED = false;

    private FabricTransferAdapter() {
    }

    /^*
     * 注册 UCS → Fabric Transfer API 的全局 fallback。
     * <p>Fabric 管道、容器、流体传输类模组通常只查询 {@code ItemStorage.SIDED}/{@code FluidStorage.SIDED}；
     * 注册 fallback 后，只要目标 BlockEntity 实现 UCS Provider 或通过 {@link ProviderRegistry} 注册，其他模组即可发现。</p>
     ^/
    public static synchronized void registerFallbacks() {
        if (FALLBACKS_REGISTERED) {
            return;
        }
        FALLBACKS_REGISTERED = true;
        ItemStorage.SIDED.registerFallback(FabricTransferAdapter::findFallbackItemStorage);
        FluidStorage.SIDED.registerFallback(FabricTransferAdapter::findFallbackFluidStorage);
    }

    /^* 将 Fabric 物品 Storage 包装为 UCS IItemHandler。 ^/
    public static IItemHandler wrapItemStorage(Storage<ItemVariant> fabricStorage) {
        return new FabricItemHandlerWrapper(fabricStorage);
    }

    /^* 从世界中通过 Fabric Transfer API 查找物品处理器并包装为 UCS。 ^/
    @Nullable
    public static IItemHandler findItemHandler(Level level, BlockPos pos, @Nullable Direction side) {
        Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, pos, side);
        return storage != null ? wrapItemStorage(storage) : null;
    }

    /^* 将 UCS IItemHandler 包装为 Fabric Storage。 ^/
    public static Storage<ItemVariant> wrapAsItemStorage(IItemHandler handler) {
        return new UcsItemStorageWrapper(handler);
    }

    /^* 将 Fabric 流体 Storage 包装为 UCS IFluidHandler。 ^/
    public static IFluidHandler wrapFluidStorage(Storage<FluidVariant> fabricStorage) {
        return new FabricFluidHandlerWrapper(fabricStorage);
    }

    /^* 从世界中通过 Fabric Transfer API 查找流体处理器并包装为 UCS。 ^/
    @Nullable
    public static IFluidHandler findFluidHandler(Level level, BlockPos pos, @Nullable Direction side) {
        Storage<FluidVariant> storage = FluidStorage.SIDED.find(level, pos, side);
        return storage != null ? wrapFluidStorage(storage) : null;
    }

    /^* 将 UCS IFluidHandler 包装为 Fabric Storage。 ^/
    public static Storage<FluidVariant> wrapAsFluidStorage(IFluidHandler handler) {
        return new UcsFluidStorageWrapper(handler);
    }

    @Nullable
    private static Storage<ItemVariant> findFallbackItemStorage(
            Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction side
    ) {
        IItemHandler handler = getUcsItemHandler(blockEntity, side);
        return handler != null ? wrapAsItemStorage(handler) : null;
    }

    @Nullable
    private static Storage<FluidVariant> findFallbackFluidStorage(
            Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction side
    ) {
        IFluidHandler handler = getUcsFluidHandler(blockEntity, side);
        return handler != null ? wrapAsFluidStorage(handler) : null;
    }

    @Nullable
    private static IItemHandler getUcsItemHandler(@Nullable BlockEntity blockEntity, @Nullable Direction side) {
        if (blockEntity == null) {
            return null;
        }
        if (blockEntity instanceof CapabilityProvider provider) {
            LazyOptional<IItemHandler> result = provider.getCapability(BuiltinCapabilities.ITEM_HANDLER, side);
            if (result.isPresent()) {
                return result.orElseThrow();
            }
        }
        LazyOptional<IItemHandler> result = ProviderRegistry.getProviders(blockEntity).getCapability(BuiltinCapabilities.ITEM_HANDLER, side);
        return result.isPresent() ? result.orElseThrow() : null;
    }

    @Nullable
    private static IFluidHandler getUcsFluidHandler(@Nullable BlockEntity blockEntity, @Nullable Direction side) {
        if (blockEntity == null) {
            return null;
        }
        if (blockEntity instanceof CapabilityProvider provider) {
            LazyOptional<IFluidHandler> result = provider.getCapability(BuiltinCapabilities.FLUID_HANDLER, side);
            if (result.isPresent()) {
                return result.orElseThrow();
            }
        }
        LazyOptional<IFluidHandler> result = ProviderRegistry.getProviders(blockEntity).getCapability(BuiltinCapabilities.FLUID_HANDLER, side);
        return result.isPresent() ? result.orElseThrow() : null;
    }

    // ========================
    // Fabric → UCS: Item
    // ========================

    private static class FabricItemHandlerWrapper implements IItemHandler {
        private final Storage<ItemVariant> fabricStorage;

        FabricItemHandlerWrapper(Storage<ItemVariant> fabricStorage) {
            this.fabricStorage = fabricStorage;
        }

        @Override
        public int getSlots() {
            int count = 0;
            try (Transaction fabricTx = Transaction.openOuter()) {
                Iterator<? extends StorageView<ItemVariant>> iterator = itemIterator(fabricStorage, fabricTx);
                while (iterator.hasNext()) {
                    count++;
                    iterator.next();
                }
            }
            return Math.max(1, count);
        }

        @Override
        public ItemStack getStackInSlot(int slot) {
            if (slot < 0) {
                return ItemStack.EMPTY;
            }
            try (Transaction fabricTx = Transaction.openOuter()) {
                Iterator<? extends StorageView<ItemVariant>> iterator = itemIterator(fabricStorage, fabricTx);
                int i = 0;
                while (iterator.hasNext()) {
                    StorageView<ItemVariant> view = iterator.next();
                    if (i == slot) {
                        return view.getResource().isBlank()
                                ? ItemStack.EMPTY
                                : view.getResource().toStack((int) Math.min(view.getAmount(), Integer.MAX_VALUE));
                    }
                    i++;
                }
            }
            return ItemStack.EMPTY;
        }

        @Override
        public long getAmountInSlot(int slot) {
            if (slot < 0) {
                return 0;
            }
            try (Transaction fabricTx = Transaction.openOuter()) {
                Iterator<? extends StorageView<ItemVariant>> iterator = itemIterator(fabricStorage, fabricTx);
                int i = 0;
                while (iterator.hasNext()) {
                    StorageView<ItemVariant> view = iterator.next();
                    if (i == slot) {
                        return view.getAmount();
                    }
                    i++;
                }
            }
            return 0;
        }

        @Override
        public long getSlotCapacity(int slot) {
            if (slot < 0) {
                return 0;
            }
            try (Transaction fabricTx = Transaction.openOuter()) {
                Iterator<? extends StorageView<ItemVariant>> iterator = itemIterator(fabricStorage, fabricTx);
                int i = 0;
                while (iterator.hasNext()) {
                    StorageView<ItemVariant> view = iterator.next();
                    if (i == slot) {
                        return view.getCapacity();
                    }
                    i++;
                }
            }
            return 64;
        }

        @Override
        public long insertItem(int slot, ItemStack resource, long maxAmount, TransactionContext tx) {
            if (slot < 0 || slot >= getSlots() || resource == null || resource.isEmpty() || maxAmount <= 0) {
                return 0;
            }
            try (Transaction fabricTx = Transaction.openOuter()) {
                long inserted = fabricStorage.insert(ItemVariant.of(resource), maxAmount, fabricTx);
                if (inserted > 0) {
                    fabricTx.commit();
                }
                return inserted;
            }
        }

        @Override
        public long extractItem(int slot, long maxAmount, TransactionContext tx) {
            if (slot < 0 || maxAmount <= 0) {
                return 0;
            }
            try (Transaction fabricTx = Transaction.openOuter()) {
                Iterator<? extends StorageView<ItemVariant>> iterator = itemIterator(fabricStorage, fabricTx);
                int i = 0;
                while (iterator.hasNext()) {
                    StorageView<ItemVariant> view = iterator.next();
                    if (i == slot && !view.getResource().isBlank()) {
                        long extracted = view.extract(view.getResource(), maxAmount, fabricTx);
                        if (extracted > 0) {
                            fabricTx.commit();
                        }
                        return extracted;
                    }
                    i++;
                }
            }
            return 0;
        }

        @Override
        public boolean isItemValid(int slot, ItemStack resource) {
            return slot >= 0 && slot < getSlots() && resource != null && !resource.isEmpty();
        }

    }

    // ========================
    // Fabric → UCS: Fluid
    // ========================

    private static class FabricFluidHandlerWrapper implements IFluidHandler {
        private final Storage<FluidVariant> fabricStorage;

        FabricFluidHandlerWrapper(Storage<FluidVariant> fabricStorage) {
            this.fabricStorage = fabricStorage;
        }

        @Override
        public int getTanks() {
            int count = 0;
            try (Transaction fabricTx = Transaction.openOuter()) {
                Iterator<? extends StorageView<FluidVariant>> iterator = fluidIterator(fabricStorage, fabricTx);
                while (iterator.hasNext()) {
                    count++;
                    iterator.next();
                }
            }
            return Math.max(1, count);
        }

        @Override
        public Fluid getFluidInTank(int tank) {
            if (tank < 0) {
                return null;
            }
            try (Transaction fabricTx = Transaction.openOuter()) {
                Iterator<? extends StorageView<FluidVariant>> iterator = fluidIterator(fabricStorage, fabricTx);
                int i = 0;
                while (iterator.hasNext()) {
                    StorageView<FluidVariant> view = iterator.next();
                    if (i == tank) {
                        return view.getResource().isBlank() ? null : view.getResource().getFluid();
                    }
                    i++;
                }
            }
            return null;
        }

        @Override
        public long getFluidAmountInTank(int tank) {
            if (tank < 0) {
                return 0;
            }
            try (Transaction fabricTx = Transaction.openOuter()) {
                Iterator<? extends StorageView<FluidVariant>> iterator = fluidIterator(fabricStorage, fabricTx);
                int i = 0;
                while (iterator.hasNext()) {
                    StorageView<FluidVariant> view = iterator.next();
                    if (i == tank) {
                        return view.getAmount();
                    }
                    i++;
                }
            }
            return 0;
        }

        @Override
        public long getTankCapacity(int tank) {
            if (tank < 0) {
                return 0;
            }
            try (Transaction fabricTx = Transaction.openOuter()) {
                Iterator<? extends StorageView<FluidVariant>> iterator = fluidIterator(fabricStorage, fabricTx);
                int i = 0;
                while (iterator.hasNext()) {
                    StorageView<FluidVariant> view = iterator.next();
                    if (i == tank) {
                        return view.getCapacity();
                    }
                    i++;
                }
            }
            return 0;
        }

        @Override
        public boolean isFluidValid(int tank, Fluid fluid) {
            return tank >= 0 && tank < getTanks() && fluid != null;
        }

        @Override
        public long fill(Fluid fluid, long maxAmount, TransactionContext tx) {
            if (fluid == null || maxAmount <= 0) {
                return 0;
            }
            try (Transaction fabricTx = Transaction.openOuter()) {
                long inserted = fabricStorage.insert(FluidVariant.of(fluid), maxAmount, fabricTx);
                if (inserted > 0) {
                    fabricTx.commit();
                }
                return inserted;
            }
        }

        @Override
        public long drain(Fluid fluid, long maxAmount, TransactionContext tx) {
            if (fluid == null || maxAmount <= 0) {
                return 0;
            }
            try (Transaction fabricTx = Transaction.openOuter()) {
                long extracted = fabricStorage.extract(FluidVariant.of(fluid), maxAmount, fabricTx);
                if (extracted > 0) {
                    fabricTx.commit();
                }
                return extracted;
            }
        }

        @Override
        public long drain(long maxAmount, TransactionContext tx) {
            if (maxAmount <= 0) {
                return 0;
            }
            try (Transaction fabricTx = Transaction.openOuter()) {
                long total = 0;
                Iterator<? extends StorageView<FluidVariant>> iterator = fluidIterator(fabricStorage, fabricTx);
                while (iterator.hasNext() && total < maxAmount) {
                    StorageView<FluidVariant> view = iterator.next();
                    if (!view.getResource().isBlank()) {
                        total += view.extract(view.getResource(), maxAmount - total, fabricTx);
                    }
                }
                if (total > 0) {
                    fabricTx.commit();
                }
                return total;
            }
        }

    }

    // ========================
    // UCS → Fabric: Item
    // ========================

    private static class UcsItemStorageWrapper implements Storage<ItemVariant> {
        private final IItemHandler handler;

        UcsItemStorageWrapper(IItemHandler handler) {
            this.handler = handler;
        }

        @Override
        public long insert(ItemVariant resource, long maxAmount, net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext transaction) {
            if (resource.isBlank() || maxAmount <= 0) {
                return 0;
            }
            long remaining = maxAmount;
            long inserted = 0;
            for (int slot = 0; slot < handler.getSlots() && remaining > 0; slot++) {
                if (!handler.isItemValid(slot, resource.toStack(1))) {
                    continue;
                }
                long moved;
                try (com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction ucsTx =
                             com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction.openOuter()) {
                    // Probe in UCS and let try-with-resources roll it back immediately.
                    moved = handler.insertItem(slot, resource.toStack((int) Math.min(remaining, Integer.MAX_VALUE)), remaining, ucsTx);
                }
                if (moved > 0) {
                    final int targetSlot = slot;
                    final long amount = moved;
                    final ItemStack stack = resource.toStack((int) Math.min(amount, Integer.MAX_VALUE));
                    runOnFabricCommit(transaction, () -> {
                        try (com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction commitTx =
                                     com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction.openOuter()) {
                            long committed = handler.insertItem(targetSlot, stack, amount, commitTx);
                            if (committed > 0) {
                                commitTx.commit();
                            }
                        }
                    });
                    inserted += moved;
                    remaining -= moved;
                }
            }
            return inserted;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext transaction) {
            if (resource.isBlank() || maxAmount <= 0) {
                return 0;
            }
            long remaining = maxAmount;
            long extracted = 0;
            for (int slot = 0; slot < handler.getSlots() && remaining > 0; slot++) {
                ItemStack stack = handler.getStackInSlot(slot);
                if (stack == null || stack.isEmpty() || !ItemVariant.of(stack).equals(resource)) {
                    continue;
                }
                long moved;
                try (com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction ucsTx =
                             com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction.openOuter()) {
                    moved = handler.extractItem(slot, remaining, ucsTx);
                }
                if (moved > 0) {
                    final int targetSlot = slot;
                    final long amount = moved;
                    runOnFabricCommit(transaction, () -> {
                        try (com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction commitTx =
                                     com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction.openOuter()) {
                            long committed = handler.extractItem(targetSlot, amount, commitTx);
                            if (committed > 0) {
                                commitTx.commit();
                            }
                        }
                    });
                    extracted += moved;
                    remaining -= moved;
                }
            }
            return extracted;
        }

        @Override
        public Iterator<StorageView<ItemVariant>> iterator(
            /^? if < 1.19 {^/ /^net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext transaction ^//^?} else {^/  /^?}^/
        ) {
            return new Iterator<>() {
                private int slot = 0;

                @Override
                public boolean hasNext() {
                    return slot < handler.getSlots();
                }

                @Override
                public StorageView<ItemVariant> next() {
                    return new UcsItemStorageView(handler, slot++);
                }
            };
        }
    }

    private static class UcsItemStorageView implements StorageView<ItemVariant> {
        private final IItemHandler handler;
        private final int slot;

        UcsItemStorageView(IItemHandler handler, int slot) {
            this.handler = handler;
            this.slot = slot;
        }

        @Override
        public ItemVariant getResource() {
            ItemStack stack = handler.getStackInSlot(slot);
            return stack == null || stack.isEmpty() ? ItemVariant.blank() : ItemVariant.of(stack);
        }

        @Override
        public long getAmount() {
            return Math.max(0, handler.getAmountInSlot(slot));
        }

        @Override
        public long getCapacity() {
            return Math.max(0, handler.getSlotCapacity(slot));
        }

        @Override
        public boolean isResourceBlank() {
            return getResource().isBlank() || getAmount() <= 0;
        }

        @Override
        public long extract(ItemVariant resource, long maxAmount, net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext transaction) {
            if (resource.isBlank() || maxAmount <= 0 || !resource.equals(getResource())) {
                return 0;
            }
            long extracted;
            try (com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction ucsTx =
                         com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction.openOuter()) {
                extracted = handler.extractItem(slot, maxAmount, ucsTx);
            }
            if (extracted > 0) {
                final long amount = extracted;
                runOnFabricCommit(transaction, () -> {
                    try (com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction commitTx =
                                 com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction.openOuter()) {
                        long committed = handler.extractItem(slot, amount, commitTx);
                        if (committed > 0) {
                            commitTx.commit();
                        }
                    }
                });
            }
            return extracted;
        }
    }

    // ========================
    // UCS → Fabric: Fluid
    // ========================

    private static class UcsFluidStorageWrapper implements Storage<FluidVariant> {
        private final IFluidHandler handler;

        UcsFluidStorageWrapper(IFluidHandler handler) {
            this.handler = handler;
        }

        @Override
        public long insert(FluidVariant resource, long maxAmount, net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext transaction) {
            if (resource.isBlank() || maxAmount <= 0) {
                return 0;
            }
            long filled;
            try (com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction ucsTx =
                         com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction.openOuter()) {
                filled = handler.fill(resource.getFluid(), maxAmount, ucsTx);
            }
            if (filled > 0) {
                final Fluid fluid = resource.getFluid();
                final long amount = filled;
                runOnFabricCommit(transaction, () -> {
                    try (com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction commitTx =
                                 com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction.openOuter()) {
                        long committed = handler.fill(fluid, amount, commitTx);
                        if (committed > 0) {
                            commitTx.commit();
                        }
                    }
                });
            }
            return filled;
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext transaction) {
            if (resource.isBlank() || maxAmount <= 0) {
                return 0;
            }
            long drained;
            try (com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction ucsTx =
                         com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction.openOuter()) {
                drained = handler.drain(resource.getFluid(), maxAmount, ucsTx);
            }
            if (drained > 0) {
                final Fluid fluid = resource.getFluid();
                final long amount = drained;
                runOnFabricCommit(transaction, () -> {
                    try (com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction commitTx =
                                 com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction.openOuter()) {
                        long committed = handler.drain(fluid, amount, commitTx);
                        if (committed > 0) {
                            commitTx.commit();
                        }
                    }
                });
            }
            return drained;
        }

        @Override
        public Iterator<StorageView<FluidVariant>> iterator(
            /^? if < 1.19 {^/ /^net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext transaction ^//^?} else {^/  /^?}^/
        ) {
            return new Iterator<>() {
                private int tank = 0;

                @Override
                public boolean hasNext() {
                    return tank < handler.getTanks();
                }

                @Override
                public StorageView<FluidVariant> next() {
                    return new UcsFluidStorageView(handler, tank++);
                }
            };
        }
    }

    private static class UcsFluidStorageView implements StorageView<FluidVariant> {
        private final IFluidHandler handler;
        private final int tank;

        UcsFluidStorageView(IFluidHandler handler, int tank) {
            this.handler = handler;
            this.tank = tank;
        }

        @Override
        public FluidVariant getResource() {
            Fluid fluid = handler.getFluidInTank(tank);
            return fluid == null ? FluidVariant.blank() : FluidVariant.of(fluid);
        }

        @Override
        public long getAmount() {
            return Math.max(0, handler.getFluidAmountInTank(tank));
        }

        @Override
        public long getCapacity() {
            return Math.max(0, handler.getTankCapacity(tank));
        }

        @Override
        public boolean isResourceBlank() {
            return getResource().isBlank() || getAmount() <= 0;
        }

        @Override
        public long extract(FluidVariant resource, long maxAmount, net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext transaction) {
            if (resource.isBlank() || maxAmount <= 0 || !resource.equals(getResource())) {
                return 0;
            }
            long drained;
            try (com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction ucsTx =
                         com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction.openOuter()) {
                drained = handler.drain(resource.getFluid(), maxAmount, ucsTx);
            }
            if (drained > 0) {
                final Fluid fluid = resource.getFluid();
                final long amount = drained;
                runOnFabricCommit(transaction, () -> {
                    try (com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction commitTx =
                                 com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction.openOuter()) {
                        long committed = handler.drain(fluid, amount, commitTx);
                        if (committed > 0) {
                            commitTx.commit();
                        }
                    }
                });
            }
            return drained;
        }
    }

    private static void runOnFabricCommit(
            net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext fabricTx,
            Runnable action
    ) {
        fabricTx.addCloseCallback((transaction, result) -> {
            if (result.wasCommitted()) {
                action.run();
            }
        });
    }

    private static Iterator<? extends StorageView<ItemVariant>> itemIterator(Storage<ItemVariant> storage, Transaction transaction) {
        //? if < 1.19 {
        /^return storage.iterator(transaction);
        ^///?} else {
        return storage.iterator();
        //?}
    }

    private static Iterator<? extends StorageView<FluidVariant>> fluidIterator(Storage<FluidVariant> storage, Transaction transaction) {
        //? if < 1.19 {
        /^return storage.iterator(transaction);
        ^///?} else {
        return storage.iterator();
        //?}
    }
}
*///?} else {

public final class FabricTransferAdapter {
    private FabricTransferAdapter() {}
}
//?}
