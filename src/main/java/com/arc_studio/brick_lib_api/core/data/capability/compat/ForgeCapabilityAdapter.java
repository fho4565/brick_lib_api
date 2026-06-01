package com.arc_studio.brick_lib_api.core.data.capability.compat;

//? if forge || neoforge {
import com.arc_studio.brick_lib_api.core.data.capability.builtin.IFluidStorage;
import com.arc_studio.brick_lib_api.core.data.capability.builtin.IItemStorage;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.BrickTransactionContext;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.BrickTransactionListener;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
//?}

/**
 * Forge / NeoForge 能力系统适配器。
 * <p>
 * 将 Forge / NeoForge 的 IEnergyStorage、IItemStorage、IFluidStorage 包装为 UCS 内置接口。
 * 在 Fabric 环境下此类为空占位。
 * </p>
 */
public final class ForgeCapabilityAdapter {

    private ForgeCapabilityAdapter() {
    }

    //? if forge {

    @Nullable
    public static com.arc_studio.brick_lib_api.core.data.capability.builtin.IEnergyStorage findEnergy(BlockEntity be, @Nullable Direction side) {
        //? if >= 1.19.3 {
        var opt = be.getCapability(ForgeCapabilities.ENERGY, side);
        //?} else {
        /*var opt = be.getCapability(net.minecraftforge.energy.CapabilityEnergy.ENERGY, side);
        *///?}
        return opt.map(ForgeCapabilityAdapter::wrapForgeEnergy).orElse(null);
    }

    @Nullable
    public static IItemStorage findItemHandler(BlockEntity be, @Nullable Direction side) {
        //? if >= 1.19.3 {
        var opt = be.getCapability(ForgeCapabilities.ITEM_HANDLER, side);
        //?} else {
        /*var opt = be.getCapability(net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
        *///?}
        return opt.map(ForgeCapabilityAdapter::wrapForgeItemHandler).orElse(null);
    }

    @Nullable
    public static IFluidStorage findFluidHandler(BlockEntity be, @Nullable Direction side) {
        //? if >= 1.19.3 {
        var opt = be.getCapability(ForgeCapabilities.FLUID_HANDLER, side);
        //?} else {
        /*var opt = be.getCapability(net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
        *///?}
        return opt.map(ForgeCapabilityAdapter::wrapForgeFluidHandler).orElse(null);
    }

    private static com.arc_studio.brick_lib_api.core.data.capability.builtin.IEnergyStorage wrapForgeEnergy(net.minecraftforge.energy.IEnergyStorage forge) {
        return new com.arc_studio.brick_lib_api.core.data.capability.builtin.IEnergyStorage() {
            @Override
            public long receiveEnergy(long maxReceive, BrickTransactionContext tx) {
                return forge.receiveEnergy(IFluidStorage.clampToInt(maxReceive), false);
            }

            @Override
            public long extractEnergy(long maxExtract, BrickTransactionContext tx) {
                return forge.extractEnergy(IFluidStorage.clampToInt(maxExtract), false);
            }

            @Override public long getEnergyStored() { return forge.getEnergyStored(); }
            @Override public long getMaxEnergyStored() { return forge.getMaxEnergyStored(); }
            @Override public boolean canReceive() { return forge.canReceive(); }
            @Override public boolean canExtract() { return forge.canExtract(); }
        };
    }

    private static IItemStorage wrapForgeItemHandler(IItemHandler forge) {
        return new IItemStorage() {
            @Override public int getSlots() { return forge.getSlots(); }
            @Override public ItemStack getStackInSlot(int slot) { return forge.getStackInSlot(slot); }
            @Override public long getAmountInSlot(int slot) { return forge.getStackInSlot(slot).getCount(); }
            @Override public long getSlotCapacity(int slot) { return forge.getSlotLimit(slot); }

            @Override
            public long insertItem(int slot, ItemStack resource, long maxAmount, BrickTransactionContext tx) {
                if (resource == null || resource.isEmpty() || maxAmount <= 0) return 0;
                ItemStack stack = resource.copy();
                stack.setCount(IFluidStorage.clampToInt(maxAmount));
                ItemStack remainder = forge.insertItem(slot, stack, false);
                return maxAmount - remainder.getCount();
            }

            @Override
            public long extractItem(int slot, long maxAmount, BrickTransactionContext tx) {
                return forge.extractItem(slot, IFluidStorage.clampToInt(maxAmount), false).getCount();
            }

            @Override public boolean isItemValid(int slot, ItemStack resource) { return forge.isItemValid(slot, resource); }
        };
    }

    private static IFluidStorage wrapForgeFluidHandler(IFluidHandler forge) {
        return new IFluidStorage() {
            @Override public int getTanks() { return forge.getTanks(); }
            @Override public Fluid getFluidInTank(int tank) { return forge.getFluidInTank(tank).getFluid(); }
            @Override public long getFluidAmountInTank(int tank) { return IFluidStorage.mbToDroplets(forge.getFluidInTank(tank).getAmount()); }
            @Override public long getTankCapacity(int tank) { return IFluidStorage.mbToDroplets(forge.getTankCapacity(tank)); }
            @Override public boolean isFluidValid(int tank, Fluid fluid) {
                return fluid != null && forge.isFluidValid(tank, new net.minecraftforge.fluids.FluidStack(fluid, 1));
            }

            @Override
            public long fill(Fluid fluid, long maxAmount, BrickTransactionContext tx) {
                if (fluid == null || maxAmount <= 0) return 0;
                int requested = IFluidStorage.dropletsToMb(maxAmount);
                if (requested <= 0) return 0;
                int filled = forge.fill(new net.minecraftforge.fluids.FluidStack(fluid, requested),
                        IFluidHandler.FluidAction.SIMULATE);
                if (filled > 0) {
                    tx.addListener(new BrickTransactionListener() {
                        @Override public void beforeCommit(BrickTransactionContext tx) {
                            forge.fill(new net.minecraftforge.fluids.FluidStack(fluid, filled),
                                    IFluidHandler.FluidAction.EXECUTE);
                        }
                        @Override public void afterCommit(BrickTransactionContext tx) {}
                        @Override public void onAbort(BrickTransactionContext tx) {}
                    });
                }
                return IFluidStorage.mbToDroplets(filled);
            }

            @Override
            public long drain(Fluid fluid, long maxAmount, BrickTransactionContext tx) {
                if (fluid == null || maxAmount <= 0) return 0;
                int requested = IFluidStorage.dropletsToMb(maxAmount);
                if (requested <= 0) return 0;
                var drained = forge.drain(new net.minecraftforge.fluids.FluidStack(fluid, requested),
                        IFluidHandler.FluidAction.SIMULATE);
                if (!drained.isEmpty()) {
                    tx.addListener(new BrickTransactionListener() {
                        @Override public void beforeCommit(BrickTransactionContext tx) {
                            forge.drain(drained,
                                    IFluidHandler.FluidAction.EXECUTE);
                        }
                        @Override public void afterCommit(BrickTransactionContext tx) {}
                        @Override public void onAbort(BrickTransactionContext tx) {}
                    });
                }
                return IFluidStorage.mbToDroplets(drained.getAmount());
            }

            @Override
            public long drain(long maxAmount, BrickTransactionContext tx) {
                if (maxAmount <= 0) return 0;
                int requested = IFluidStorage.dropletsToMb(maxAmount);
                if (requested <= 0) return 0;
                var drained = forge.drain(requested,
                        IFluidHandler.FluidAction.SIMULATE);
                if (!drained.isEmpty()) {
                    tx.addListener(new BrickTransactionListener() {
                        @Override public void beforeCommit(BrickTransactionContext tx) {
                            forge.drain(drained,
                                    IFluidHandler.FluidAction.EXECUTE);
                        }
                        @Override public void afterCommit(BrickTransactionContext tx) {}
                        @Override public void onAbort(BrickTransactionContext tx) {}
                    });
                }
                return IFluidStorage.mbToDroplets(drained.getAmount());
            }
        };
    }
    //?}

    //? if neoforge {
    /*@Nullable
    public static com.arc_studio.brick_lib_api.core.data.capability.builtin.IEnergyStorage findNeoForgeEnergy(
            Level level, BlockPos pos, @Nullable Direction side
    ) {
        var neo = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK, pos, side);
        return neo != null ? wrapNeoForgeEnergy(neo) : null;
    }

    @Nullable
    public static com.arc_studio.brick_lib_api.core.data.capability.builtin.IItemStorage findNeoForgeItemHandler(
            Level level, BlockPos pos, @Nullable Direction side
    ) {
        var neo = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, pos, side);
        return neo != null ? wrapNeoForgeItemHandler(neo) : null;
    }

    @Nullable
    public static com.arc_studio.brick_lib_api.core.data.capability.builtin.IFluidStorage findNeoForgeFluidHandler(
            Level level, BlockPos pos, @Nullable Direction side
    ) {
        var neo = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK, pos, side);
        return neo != null ? wrapNeoForgeFluidHandler(neo) : null;
    }

    private static com.arc_studio.brick_lib_api.core.data.capability.builtin.IEnergyStorage wrapNeoForgeEnergy(
            net.neoforged.neoforge.energy.IEnergyStorage neo
    ) {
        return new com.arc_studio.brick_lib_api.core.data.capability.builtin.IEnergyStorage() {
            @Override public long receiveEnergy(long maxReceive, BrickTransactionContext tx) { return neo.receiveEnergy(CompatUtil.clampToInt(maxReceive), false); }
            @Override public long extractEnergy(long maxExtract, BrickTransactionContext tx) { return neo.extractEnergy(CompatUtil.clampToInt(maxExtract), false); }
            @Override public long getEnergyStored() { return neo.getEnergyStored(); }
            @Override public long getMaxEnergyStored() { return neo.getMaxEnergyStored(); }
            @Override public boolean canReceive() { return neo.canReceive(); }
            @Override public boolean canExtract() { return neo.canExtract(); }
        };
    }

    private static com.arc_studio.brick_lib_api.core.data.capability.builtin.IItemStorage wrapNeoForgeItemHandler(
            net.neoforged.neoforge.items.IItemHandler neo
    ) {
        return new com.arc_studio.brick_lib_api.core.data.capability.builtin.IItemStorage() {
            @Override public int getSlots() { return neo.getSlots(); }
            @Override public ItemStack getStackInSlot(int slot) { return neo.getStackInSlot(slot); }
            @Override public long getAmountInSlot(int slot) { return neo.getStackInSlot(slot).getCount(); }
            @Override public long getSlotCapacity(int slot) { return neo.getSlotLimit(slot); }

            @Override
            public long insertItem(int slot, ItemStack resource, long maxAmount, BrickTransactionContext tx) {
                if (resource == null || resource.isEmpty() || maxAmount <= 0) return 0;
                ItemStack stack = resource.copy();
                stack.setCount(CompatUtil.clampToInt(maxAmount));
                ItemStack remainder = neo.insertItem(slot, stack, false);
                return maxAmount - remainder.getCount();
            }

            @Override public long extractItem(int slot, long maxAmount, BrickTransactionContext tx) { return neo.extractItem(slot, CompatUtil.clampToInt(maxAmount), false).getCount(); }
            @Override public boolean isItemValid(int slot, ItemStack resource) { return neo.isItemValid(slot, resource); }
        };
    }

    private static com.arc_studio.brick_lib_api.core.data.capability.builtin.IFluidStorage wrapNeoForgeFluidHandler(
            net.neoforged.neoforge.fluids.capability.IFluidHandler neo
    ) {
        return new com.arc_studio.brick_lib_api.core.data.capability.builtin.IFluidStorage() {
            @Override public int getTanks() { return neo.getTanks(); }
            @Override public net.minecraft.world.level.material.Fluid getFluidInTank(int tank) { return neo.getFluidInTank(tank).getFluid(); }
            @Override public long getFluidAmountInTank(int tank) { return CompatUtil.mbToDroplets(neo.getFluidInTank(tank).getAmount()); }
            @Override public long getTankCapacity(int tank) { return CompatUtil.mbToDroplets(neo.getTankCapacity(tank)); }
            @Override public boolean isFluidValid(int tank, net.minecraft.world.level.material.Fluid fluid) {
                return fluid != null && neo.isFluidValid(tank, new net.neoforged.neoforge.fluids.FluidStack(fluid, 1));
            }
            @Override public long fill(net.minecraft.world.level.material.Fluid fluid, long maxAmount, BrickTransactionContext tx) {
                if (fluid == null || maxAmount <= 0) return 0;
                int requested = CompatUtil.dropletsToMb(maxAmount);
                if (requested <= 0) return 0;
                int filled = neo.fill(new net.neoforged.neoforge.fluids.FluidStack(fluid, requested),
                        net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE);
                if (filled > 0) {
                    tx.addListener(new BrickTransactionListener() {
                        @Override public void beforeCommit(BrickTransactionContext tx) {
                            neo.fill(new net.neoforged.neoforge.fluids.FluidStack(fluid, filled),
                                    net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
                        }
                        @Override public void afterCommit(BrickTransactionContext tx) {}
                        @Override public void onAbort(BrickTransactionContext tx) {}
                    });
                }
                return CompatUtil.mbToDroplets(filled);
            }
            @Override public long drain(net.minecraft.world.level.material.Fluid fluid, long maxAmount, BrickTransactionContext tx) {
                if (fluid == null || maxAmount <= 0) return 0;
                int requested = CompatUtil.dropletsToMb(maxAmount);
                if (requested <= 0) return 0;
                var drained = neo.drain(new net.neoforged.neoforge.fluids.FluidStack(fluid, requested),
                        net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE);
                if (!drained.isEmpty()) {
                    tx.addListener(new BrickTransactionListener() {
                        @Override public void beforeCommit(BrickTransactionContext tx) {
                            neo.drain(drained,
                                    net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
                        }
                        @Override public void afterCommit(BrickTransactionContext tx) {}
                        @Override public void onAbort(BrickTransactionContext tx) {}
                    });
                }
                return CompatUtil.mbToDroplets(drained.getAmount());
            }
            @Override public long drain(long maxAmount, BrickTransactionContext tx) {
                if (maxAmount <= 0) return 0;
                int requested = CompatUtil.dropletsToMb(maxAmount);
                if (requested <= 0) return 0;
                var drained = neo.drain(requested,
                        net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE);
                if (!drained.isEmpty()) {
                    tx.addListener(new BrickTransactionListener() {
                        @Override public void beforeCommit(BrickTransactionContext tx) {
                            neo.drain(drained,
                                    net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
                        }
                        @Override public void afterCommit(BrickTransactionContext tx) {}
                        @Override public void onAbort(BrickTransactionContext tx) {}
                    });
                }
                return CompatUtil.mbToDroplets(drained.getAmount());
            }
        };
    }
    *///?}
}
