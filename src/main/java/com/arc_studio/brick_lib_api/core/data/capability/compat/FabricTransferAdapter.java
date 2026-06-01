package com.arc_studio.brick_lib_api.core.data.capability.compat;

//? if fabric {
/*import com.arc_studio.brick_lib_api.core.data.capability.builtin.*;
import com.arc_studio.brick_lib_api.core.data.capability.builtin.impl.SimpleEnergyStorage;
import com.arc_studio.brick_lib_api.core.data.capability.provider.CapabilityProvider;
import com.arc_studio.brick_lib_api.core.data.BrickLazyOptional;
import com.arc_studio.brick_lib_api.core.data.capability.provider.ProviderRegistry;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.BrickTransaction;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.BrickTransactionContext;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidStorage;
import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.fabricmc.fabric.api.transfer.v1.transaction.base.SnapshotParticipant;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import team.reborn.energy.api.EnergyStorage;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;

/^*
 * Fabric Transfer API 适配器。
 * <p>
 * 负责 Fabric {@code Storage<ItemVariant>} / {@code Storage<FluidVariant>} 与 UCS 内置
 * {@link IItemStorage} / {@link IFluidStorage} 的双向桥接，并通过 Fabric fallback 将 UCS 能力暴露给其他模组。
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

    /^* 将 Fabric 物品 Storage 包装为 UCS IItemStorage。 ^/
    public static IItemStorage wrapItemStorage(Storage<ItemVariant> fabricStorage) {
        return new FabricItemHandlerWrapper(fabricStorage);
    }

    /^* 从世界中通过 Fabric Transfer API 查找物品处理器并包装为 UCS。 ^/
    @Nullable
    public static IItemStorage findItemHandler(Level level, BlockPos pos, @Nullable Direction side) {
        Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, pos, side);
        return storage != null ? wrapItemStorage(storage) : null;
    }

    /^* 将 UCS IItemStorage 包装为 Fabric Storage。 ^/
    public static Storage<ItemVariant> wrapAsItemStorage(IItemStorage handler) {
        return new UcsItemStorageWrapper(handler);
    }

    /^* 将 Fabric 流体 Storage 包装为 UCS IFluidStorage。 ^/
    public static IFluidStorage wrapFluidStorage(Storage<FluidVariant> fabricStorage) {
        return new FabricFluidHandlerWrapper(fabricStorage);
    }

    /^* 从世界中通过 Fabric Transfer API 查找流体处理器并包装为 UCS。 ^/
    @Nullable
    public static IFluidStorage findFluidHandler(Level level, BlockPos pos, @Nullable Direction side) {
        Storage<FluidVariant> storage = FluidStorage.SIDED.find(level, pos, side);
        return storage != null ? wrapFluidStorage(storage) : null;
    }

    /^* 将 UCS IFluidStorage 包装为 Fabric Storage。 ^/
    public static Storage<FluidVariant> wrapAsFluidStorage(IFluidStorage handler) {
        return new UcsFluidStorageWrapper(handler);
    }

    @Nullable
    private static Storage<ItemVariant> findFallbackItemStorage(
            Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction side
    ) {
        IItemStorage handler = getUcsItemHandler(blockEntity, side);
        return handler != null ? wrapAsItemStorage(handler) : null;
    }

    @Nullable
    private static Storage<FluidVariant> findFallbackFluidStorage(
            Level level, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, @Nullable Direction side
    ) {
        IFluidStorage handler = getUcsFluidHandler(blockEntity, side);
        return handler != null ? wrapAsFluidStorage(handler) : null;
    }

    @Nullable
    private static IItemStorage getUcsItemHandler(@Nullable BlockEntity blockEntity, @Nullable Direction side) {
        if (blockEntity == null) {
            return null;
        }
        if (blockEntity instanceof CapabilityProvider provider) {
            BrickLazyOptional<IItemStorage> result = provider.getCapability(BuiltinCapabilities.ITEM_HANDLER, side);
            if (result.isPresent()) {
                return result.orElseThrow();
            }
        }
        BrickLazyOptional<IItemStorage> result = ProviderRegistry.getProviders(blockEntity).getCapability(BuiltinCapabilities.ITEM_HANDLER, side);
        return result.isPresent() ? result.orElseThrow() : null;
    }

    @Nullable
    private static IFluidStorage getUcsFluidHandler(@Nullable BlockEntity blockEntity, @Nullable Direction side) {
        if (blockEntity == null) {
            return null;
        }
        if (blockEntity instanceof CapabilityProvider provider) {
            BrickLazyOptional<IFluidStorage> result = provider.getCapability(BuiltinCapabilities.FLUID_HANDLER, side);
            if (result.isPresent()) {
                return result.orElseThrow();
            }
        }
        BrickLazyOptional<IFluidStorage> result = ProviderRegistry.getProviders(blockEntity).getCapability(BuiltinCapabilities.FLUID_HANDLER, side);
        return result.isPresent() ? result.orElseThrow() : null;
    }

    // ========================
    // Fabric → UCS: Item
    // ========================

    private static class FabricItemHandlerWrapper implements IItemStorage {
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
        public long insertItem(int slot, ItemStack resource, long maxAmount, BrickTransactionContext tx) {
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
        public long extractItem(int slot, long maxAmount, BrickTransactionContext tx) {
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

    private static class FabricFluidHandlerWrapper implements IFluidStorage {
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
        public long fill(Fluid fluid, long maxAmount, BrickTransactionContext tx) {
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
        public long drain(Fluid fluid, long maxAmount, BrickTransactionContext tx) {
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
        public long drain(long maxAmount, BrickTransactionContext tx) {
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
        private final IItemStorage handler;

        UcsItemStorageWrapper(IItemStorage handler) {
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
                try (BrickTransaction ucsTx =
                             BrickTransaction.openOuter()) {
                    // Probe in UCS and let try-with-resources roll it back immediately.
                    moved = handler.insertItem(slot, resource.toStack((int) Math.min(remaining, Integer.MAX_VALUE)), remaining, ucsTx);
                }
                if (moved > 0) {
                    final int targetSlot = slot;
                    final long amount = moved;
                    final ItemStack stack = resource.toStack((int) Math.min(amount, Integer.MAX_VALUE));
                    runOnFabricCommit(transaction, () -> {
                        try (BrickTransaction commitTx =
                                     BrickTransaction.openOuter()) {
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
                try (BrickTransaction ucsTx =
                             BrickTransaction.openOuter()) {
                    moved = handler.extractItem(slot, remaining, ucsTx);
                }
                if (moved > 0) {
                    final int targetSlot = slot;
                    final long amount = moved;
                    runOnFabricCommit(transaction, () -> {
                        try (BrickTransaction commitTx =
                                     BrickTransaction.openOuter()) {
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
        private final IItemStorage handler;
        private final int slot;

        UcsItemStorageView(IItemStorage handler, int slot) {
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
            try (BrickTransaction ucsTx =
                         BrickTransaction.openOuter()) {
                extracted = handler.extractItem(slot, maxAmount, ucsTx);
            }
            if (extracted > 0) {
                final long amount = extracted;
                runOnFabricCommit(transaction, () -> {
                    try (BrickTransaction commitTx =
                                 BrickTransaction.openOuter()) {
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
        private final IFluidStorage handler;

        UcsFluidStorageWrapper(IFluidStorage handler) {
            this.handler = handler;
        }

        @Override
        public long insert(FluidVariant resource, long maxAmount, net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext transaction) {
            if (resource.isBlank() || maxAmount <= 0) {
                return 0;
            }
            long filled;
            try (BrickTransaction ucsTx =
                         BrickTransaction.openOuter()) {
                filled = handler.fill(resource.getFluid(), maxAmount, ucsTx);
            }
            if (filled > 0) {
                final Fluid fluid = resource.getFluid();
                final long amount = filled;
                runOnFabricCommit(transaction, () -> {
                    try (BrickTransaction commitTx =
                                 BrickTransaction.openOuter()) {
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
            try (BrickTransaction ucsTx =
                         BrickTransaction.openOuter()) {
                drained = handler.drain(resource.getFluid(), maxAmount, ucsTx);
            }
            if (drained > 0) {
                final Fluid fluid = resource.getFluid();
                final long amount = drained;
                runOnFabricCommit(transaction, () -> {
                    try (BrickTransaction commitTx =
                                 BrickTransaction.openOuter()) {
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
        private final IFluidStorage handler;
        private final int tank;

        UcsFluidStorageView(IFluidStorage handler, int tank) {
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
            try (BrickTransaction ucsTx =
                         BrickTransaction.openOuter()) {
                drained = handler.drain(resource.getFluid(), maxAmount, ucsTx);
            }
            if (drained > 0) {
                final Fluid fluid = resource.getFluid();
                final long amount = drained;
                runOnFabricCommit(transaction, () -> {
                    try (BrickTransaction commitTx =
                                 BrickTransaction.openOuter()) {
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

    // ========================
    // UCS → Fabric: Energy (for block lookup)
    // ========================

    /^*
     * 将 UCS {@link IEnergyStorage} 包装为 TR {@link EnergyStorage}，用于被动查询（block lookup）。
     * <p>
     * 使用与物品/流体相同的 probe+defer 模式：
     * 在 Fabric BrickTransaction 中试探（probe）可提取/可接收量并立即返回，
     * 实际的 Brick Lib 事务提交延迟到 Fabric Tx 提交后的 close callback 中执行。
     * </p>
     *
     * @param storage  UCS 能量存储
     * @param onChanged 能量变化后的回调（通常为 {@code data::setDirty}）
     * @return Fabric TR EnergyStorage
     ^/
    public static EnergyStorage wrapAsEnergyStorage(IEnergyStorage storage, @Nullable Runnable onChanged) {
        return new FabricEnergyLookupWrapper(storage, onChanged);
    }

    /^*
     * 将 UCS {@link IEnergyStorage} 包装为 TR {@link EnergyStorage}，用于主动推出场景（active push）。
     * <p>
     * 参考 Create Addition 交流发电机 {@code InternalEnergyStorage} 的实现：
     * 绕过 Brick Lib 事务系统，直接修改底层能量值并使用 Fabric 的
     * {@link SnapshotParticipant} 做事务快照回滚。
     * </p>
     * <p>
     * 优势：在单个 Fabric BrickTransaction 内完成源扣减+邻居插入，原子性有保障。
     * 仅适用于 {@link com.arc_studio.brick_lib_api.core.data.capability.builtin.impl.SimpleEnergyStorage}
     * 等可直接感知内部状态的能量存储。
     * </p>
     *
     * @param storage UCS 能量存储（需支持直接能量值读取）
     * @return Fabric TR EnergyStorage 源包装
     ^/
    public static EnergyStorage wrapEnergySourceForPush(IEnergyStorage storage) {
        return new FabricEnergySourceWrapper(storage);
    }

    /^*
     * 能量桥接 — 被动查询（probe+defer 模式）。
     * <p>
     * 与 {@link UcsItemStorageWrapper} 同理：先在 Brick Lib 事务中试探可接受量，
     * 返回试探结果；实际修改延迟到 Fabric Tx 提交后的 close callback。
     * </p>
     ^/
    private static class FabricEnergyLookupWrapper implements EnergyStorage {
        private final IEnergyStorage storage;
        @Nullable
        private final Runnable onChanged;

        FabricEnergyLookupWrapper(IEnergyStorage storage, @Nullable Runnable onChanged) {
            this.storage = storage;
            this.onChanged = onChanged;
        }

        @Override
        public long insert(long maxAmount, net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext transaction) {
            if (!storage.canReceive() || maxAmount <= 0) return 0;

            long accepted;
            try (BrickTransaction ucsTx =
                         BrickTransaction.openOuter()) {
                accepted = storage.receiveEnergy(maxAmount, ucsTx);
                // ucsTx 自动 abort — 仅试探
            }
            if (accepted <= 0) return 0;

            final long amount = accepted;
            runOnFabricCommit(transaction, () -> {
                try (BrickTransaction commitTx =
                             BrickTransaction.openOuter()) {
                    long committed = storage.receiveEnergy(amount, commitTx);
                    if (committed > 0) {
                        commitTx.commit();
                        if (onChanged != null) onChanged.run();
                    }
                }
            });
            return accepted;
        }

        @Override
        public long extract(long maxAmount, net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext transaction) {
            if (!storage.canExtract() || maxAmount <= 0) return 0;

            long extracted;
            try (BrickTransaction ucsTx =
                         BrickTransaction.openOuter()) {
                extracted = storage.extractEnergy(maxAmount, ucsTx);
                // ucsTx 自动 abort — 仅试探
            }
            if (extracted <= 0) return 0;

            final long amount = extracted;
            runOnFabricCommit(transaction, () -> {
                try (BrickTransaction commitTx =
                             BrickTransaction.openOuter()) {
                    long committed = storage.extractEnergy(amount, commitTx);
                    if (committed > 0) {
                        commitTx.commit();
                        if (onChanged != null) onChanged.run();
                    }
                }
            });
            return extracted;
        }

        @Override
        public long getAmount() {
            return storage.getEnergyStored();
        }

        @Override
        public long getCapacity() {
            return storage.getMaxEnergyStored();
        }

        @Override
        public boolean supportsInsertion() {
            return storage.canReceive();
        }

        @Override
        public boolean supportsExtraction() {
            return storage.canExtract();
        }
    }

    /^*
     * 能量源桥接 — 用于主动推出（direct Fabric BrickSnapshotParticipant 模式）。
     * <p>
     * 参考 Create Addition 交流发电机的 {@code InternalEnergyStorage} 实现模式。
     * </p>
     * <p>
     * 对于 {@link SimpleEnergyStorage}：通过 {@code setEnergy()} 直接修改能量值，
     * 完全绕过 Brick Lib 事务系统，使用 Fabric 的 {@link SnapshotParticipant} 做快照回滚。
     * 这与 Create Addition 的 {@code InternalEnergyStorage} 做法完全一致：
     * extract 内直接修改字段 + {@code updateSnapshots} 注册 Fabric 回滚。
     * </p>
     * <p>
     * 对于其他 {@link IEnergyStorage} 实现：回退到 Brick Lib 事务扣减+提交。
     * </p>
     ^/
    private static class FabricEnergySourceWrapper extends SnapshotParticipant<Long> implements EnergyStorage {
        private final IEnergyStorage storage;

        FabricEnergySourceWrapper(IEnergyStorage storage) {
            this.storage = storage;
        }

        @Override
        public long insert(long maxAmount, net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext transaction) {
            if (maxAmount <= 0) return 0;
            long current = storage.getEnergyStored();
            long capacity = storage.getMaxEnergyStored();
            if (current >= capacity) return 0;
            long canAccept = Math.min(maxAmount, capacity - current);
            if (canAccept <= 0) return 0;

            // SimpleEnergyStorage: 直接 setEnergy（alternator 模式）
            if (storage instanceof SimpleEnergyStorage ses) {
                ses.setEnergy(current + canAccept);
                return canAccept;
            }

            // 通用 IEnergyStorage: Brick Lib 事务回充
            try (BrickTransaction brickTx =
                         BrickTransaction.openOuter()) {
                long received = storage.receiveEnergy(canAccept, brickTx);
                if (received > 0) brickTx.commit();
                return received;
            }
        }

        @Override
        public long extract(long maxAmount, net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext transaction) {
            if (!storage.canExtract() || maxAmount <= 0) return 0;
            long current = storage.getEnergyStored();
            if (current <= 0) return 0;
            long canExtract = Math.min(maxAmount, current);
            if (canExtract <= 0) return 0;

            // 记录 Fabric 事务快照以便回滚（updateSnapshots 保存 createSnapshot() 的值）
            updateSnapshots(transaction);

            // SimpleEnergyStorage: 直接 setEnergy（alternator 模式）
            // InternalEnergyStorage 也是 this.amount -= extracted 这么做的
            if (storage instanceof SimpleEnergyStorage ses) {
                ses.setEnergy(current - canExtract);
                return canExtract;
            }

            // 通用 IEnergyStorage: Brick Lib 事务扣减并提交
            try (BrickTransaction brickTx =
                         BrickTransaction.openOuter()) {
                long extracted = storage.extractEnergy(canExtract, brickTx);
                if (extracted > 0) brickTx.commit();
                return extracted;
            }
        }

        @Override
        public long getAmount() {
            return storage.getEnergyStored();
        }

        @Override
        public long getCapacity() {
            return storage.getMaxEnergyStored();
        }

        @Override
        public boolean supportsInsertion() {
            // 主动推出时可能需要退回多余能量
            return true;
        }

        @Override
        public boolean supportsExtraction() {
            return storage.canExtract();
        }

        @Override
        protected Long createSnapshot() {
            return storage.getEnergyStored();
        }

        @Override
        protected void readSnapshot(Long snapshot) {
            // SimpleEnergyStorage: 直接 setEnergy 恢复（alternator 模式）
            if (storage instanceof SimpleEnergyStorage ses) {
                ses.setEnergy(snapshot);
                return;
            }

            // 通用 IEnergyStorage: Brick Lib 事务恢复
            long current = storage.getEnergyStored();
            if (current == snapshot) return;
            try (BrickTransaction brickTx =
                         BrickTransaction.openOuter()) {
                if (current > snapshot) {
                    storage.extractEnergy(current - snapshot, brickTx);
                } else {
                    storage.receiveEnergy(snapshot - current, brickTx);
                }
                brickTx.commit();
            }
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
