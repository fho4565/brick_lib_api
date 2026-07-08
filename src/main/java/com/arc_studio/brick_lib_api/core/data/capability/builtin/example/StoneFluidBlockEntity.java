package com.arc_studio.brick_lib_api.core.data.capability.builtin.example;

import com.arc_studio.brick_lib_api.core.data.capability.IFluidStorage;
import com.arc_studio.brick_lib_api.core.data.capability.block_entity.BrickFluidCapabilityBlockEntity;
import com.arc_studio.brick_lib_api.core.data.capability.impl.SimpleFluidStorage;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.BrickTransaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
//? if > 1.19.2 {
import net.minecraft.core.HolderLookup;
//? }
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
//? if = 1.21.11 {
/*import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
*///? }
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
//? if forge {
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraftforge.common.capabilities.Capability;
//? if >= 1.19.3 {
import net.minecraftforge.common.capabilities.ForgeCapabilities;
//?}
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
//?}

//? if neoforge {
/*import net.minecraft.core.Direction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
*///?}

/**
 * 石头流体存储的 BlockEntity
 * <p>
 * 为石头方块提供加载器原生的流体能力支持（Forge IFluidStorage / NeoForge IFluidStorage），
 * 使其他模组的管道（如 Mekanism 机械管道）能够连接并传输流体。
 * </p>
 * <p>
 * 实际数据由 {@link StoneFluidData}（BrickSavedData）管理，此 BlockEntity 仅作为能力桥接。
 * </p>
 */
public class StoneFluidBlockEntity extends BrickFluidCapabilityBlockEntity {

    /** BlockEntityType 实例 — 在 StoneFluidEvents 中注册 */
    public static BlockEntityType<StoneFluidBlockEntity> TYPE;

    //? if forge {
    private LazyOptional<IFluidHandler> fluidHandlerLazy = LazyOptional.empty();
    //?}

    public StoneFluidBlockEntity(BlockPos pos, BlockState state) {
        super(TYPE, pos, state);
    }

    //? if forge {
    @Override
    public void onLoad() {
        super.onLoad();
        if (level instanceof ServerLevel serverLevel && getBlockState().is(Blocks.CHEST)) {
            serverLevel.setBlockEntity(new ChestBlockEntity(worldPosition, getBlockState()));
        }
    }
    //?}

    /**
     * 获取此位置的 UCS SimpleFluidStorage（从 BrickSavedData）
     */
    @Nullable
    public SimpleFluidStorage getStorage(Direction side) {
        if (level instanceof ServerLevel serverLevel
                && getBlockState().is(Blocks.CHEST)
                && level.getBlockState(worldPosition).is(Blocks.CHEST)) {
            StoneFluidData data = StoneFluidData.get(serverLevel);
            return data.getOrCreate(worldPosition);
        }
        return null;
    }

    @Override
    @Nullable
    public IFluidStorage getFluidStorage(@Nullable Direction side) {
        return getStorage(side);
    }

    // ========================
    // Forge: ICapabilityProvider
    // ========================

    //? if forge {

    @Override
    @SuppressWarnings("removal")
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        //? if >= 1.19.3 {
        if (cap == ForgeCapabilities.FLUID_HANDLER) {
        //?} else {
        /*if (cap == net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
        *///?}
            return getFluidHandlerLazy().cast();
        }
        return super.getCapability(cap, side);
    }

    private LazyOptional<IFluidHandler> getFluidHandlerLazy() {
        if (!fluidHandlerLazy.isPresent()) {
            fluidHandlerLazy = LazyOptional.of(() -> new ForgeFluidHandlerBridge(this));
        }
        return fluidHandlerLazy;
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        fluidHandlerLazy.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        fluidHandlerLazy = LazyOptional.empty();
    }

    /**
     * 将 UCS SimpleFluidStorage 桥接为 Forge IFluidStorage
     */
    private static class ForgeFluidHandlerBridge implements IFluidHandler {
        private final StoneFluidBlockEntity be;

        ForgeFluidHandlerBridge(StoneFluidBlockEntity be) {
            this.be = be;
        }

        private SimpleFluidStorage storage() {
            return be.getStorage(Direction.UP);
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
            var fluid = s.getFluidInTank(tank);
            if (fluid == null) return FluidStack.EMPTY;
            long amount = s.getFluidAmountInTank(tank);
            return new FluidStack(fluid, IFluidStorage.dropletsToMb(amount));
        }

        @Override
        public int getTankCapacity(int tank) {
            SimpleFluidStorage s = storage();
            if (s == null) return 0;
            return IFluidStorage.dropletsToMb(s.getTankCapacity(tank));
        }

        @Override
        public boolean isFluidValid(int tank, @NotNull FluidStack stack) {
            SimpleFluidStorage s = storage();
            if (s == null) return false;
            return s.isFluidValid(tank, stack.getFluid(),Direction.UP);
        }

        @Override
        public int fill(FluidStack resource, FluidAction action) {
            SimpleFluidStorage s = storage();
            if (s == null || resource.isEmpty()) return 0;
            if (action.simulate()) {
                return IFluidStorage.getFillableMb(s, resource.getFluid(),Direction.UP, resource.getAmount());
            }
            long filled = executePartialFill(s, resource.getFluid(), IFluidStorage.mbToDroplets(resource.getAmount()));
            return IFluidStorage.dropletsToMb(filled);
        }

        @Override
        public @NotNull FluidStack drain(FluidStack resource, FluidAction action) {
            SimpleFluidStorage s = storage();
            if (s == null || resource.isEmpty()) return FluidStack.EMPTY;
            if (action.simulate()) {
                int drained = IFluidStorage.getDrainableMb(s, resource.getFluid(), resource.getAmount());
                return drained > 0 ? new FluidStack(resource.getFluid(), drained) : FluidStack.EMPTY;
            }
            long drained = executePartialDrain(s, resource.getFluid(), IFluidStorage.mbToDroplets(resource.getAmount()));
            return drained > 0 ? new FluidStack(resource.getFluid(), IFluidStorage.dropletsToMb(drained)) : FluidStack.EMPTY;
        }

        @Override
        public @NotNull FluidStack drain(int maxDrain, FluidAction action) {
            SimpleFluidStorage s = storage();
            if (s == null || maxDrain <= 0) return FluidStack.EMPTY;
            var fluid = s.getFluidInTank(0);
            if (fluid == null) return FluidStack.EMPTY;
            if (action.simulate()) {
                int drained = IFluidStorage.getDrainableMb(s, fluid, maxDrain);
                return drained > 0 ? new FluidStack(fluid, drained) : FluidStack.EMPTY;
            }
            long drained = executePartialDrain(s,fluid, IFluidStorage.mbToDroplets(maxDrain));
            return drained > 0 ? new FluidStack(fluid, IFluidStorage.dropletsToMb(drained)) : FluidStack.EMPTY;
        }

        private long executePartialFill(SimpleFluidStorage storage, net.minecraft.world.level.material.Fluid fluid, long amount) {
            if (fluid == null || amount <= 0) return 0;
            try (BrickTransaction tx = BrickTransaction.openOuter()) {
                long filled = storage.fill(fluid,Direction.UP, amount, tx);
                if (filled > 0) {
                    tx.commit();
                    markDataDirty();
                }
                return filled;
            }
        }

        private long executePartialDrain(SimpleFluidStorage storage, net.minecraft.world.level.material.Fluid fluid, long amount) {
            if (fluid == null || amount <= 0) return 0;
            try (BrickTransaction tx = BrickTransaction.openOuter()) {
                long drained = storage.drain(fluid, Direction.UP, amount, tx);
                if (drained > 0) {
                    tx.commit();
                    markDataDirty();
                }
                return drained;
            }
        }

        private void markDataDirty() {
            if (be.level instanceof ServerLevel serverLevel) {
                StoneFluidData.get(serverLevel).setDirty();
            }
        }
    }
    //?}

    // ========================

    // ========================
    // NBT: 此 BE 不存储数据（数据在 StoneFluidData）
    // ========================

    //? if !fabric {
    //? if >= 1.20.6 {
    /*//? if < 1.21.8 {
    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
    }
    //? } else {

    /^@Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
    }
^/
    //? }
    *///?} else {
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
    }
    //?}
    //?}
}

