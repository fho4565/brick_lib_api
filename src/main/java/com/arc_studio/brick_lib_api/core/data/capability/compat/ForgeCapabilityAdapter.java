package com.arc_studio.brick_lib_api.core.data.capability.compat;

//? if forge || neoforge {
import com.arc_studio.brick_lib_api.core.data.capability.transaction.TransactionContext;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.TransactionListener;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
//?}

/**
 * Forge / NeoForge 能力系统适配器。
 * <p>
 * 将 Forge / NeoForge 的 IEnergyStorage、IItemHandler、IFluidHandler 包装为 UCS 内置接口。
 * 在 Fabric 环境下此类为空占位。
 * </p>
 */
public final class ForgeCapabilityAdapter {

    private static final long DROPLETS_PER_MB = 81L;

    private ForgeCapabilityAdapter() {
    }

    //? if forge {

    @Nullable
    public static com.arc_studio.brick_lib_api.core.data.capability.builtin.IEnergyStorage findEnergy(BlockEntity be, @Nullable Direction side) {
        //? if >= 1.19.3 {
        var opt = be.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ENERGY, side);
        //?} else {
        /*var opt = be.getCapability(net.minecraftforge.energy.CapabilityEnergy.ENERGY, side);
        *///?}
        return opt.map(ForgeCapabilityAdapter::wrapForgeEnergy).orElse(null);
    }

    @Nullable
    public static com.arc_studio.brick_lib_api.core.data.capability.builtin.IItemHandler findItemHandler(BlockEntity be, @Nullable Direction side) {
        //? if >= 1.19.3 {
        var opt = be.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER, side);
        //?} else {
        /*var opt = be.getCapability(net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side);
        *///?}
        return opt.map(ForgeCapabilityAdapter::wrapForgeItemHandler).orElse(null);
    }

    @Nullable
    public static com.arc_studio.brick_lib_api.core.data.capability.builtin.IFluidHandler findFluidHandler(BlockEntity be, @Nullable Direction side) {
        //? if >= 1.19.3 {
        var opt = be.getCapability(net.minecraftforge.common.capabilities.ForgeCapabilities.FLUID_HANDLER, side);
        //?} else {
        /*var opt = be.getCapability(net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side);
        *///?}
        return opt.map(ForgeCapabilityAdapter::wrapForgeFluidHandler).orElse(null);
    }

    private static com.arc_studio.brick_lib_api.core.data.capability.builtin.IEnergyStorage wrapForgeEnergy(
            net.minecraftforge.energy.IEnergyStorage forge
    ) {
        return new com.arc_studio.brick_lib_api.core.data.capability.builtin.IEnergyStorage() {
            @Override
            public long receiveEnergy(long maxReceive, TransactionContext tx) {
                return forge.receiveEnergy(clampInt(maxReceive), false);
            }

            @Override
            public long extractEnergy(long maxExtract, TransactionContext tx) {
                return forge.extractEnergy(clampInt(maxExtract), false);
            }

            @Override public long getEnergyStored() { return forge.getEnergyStored(); }
            @Override public long getMaxEnergyStored() { return forge.getMaxEnergyStored(); }
            @Override public boolean canReceive() { return forge.canReceive(); }
            @Override public boolean canExtract() { return forge.canExtract(); }
        };
    }

    private static com.arc_studio.brick_lib_api.core.data.capability.builtin.IItemHandler wrapForgeItemHandler(
            net.minecraftforge.items.IItemHandler forge
    ) {
        return new com.arc_studio.brick_lib_api.core.data.capability.builtin.IItemHandler() {
            @Override public int getSlots() { return forge.getSlots(); }
            @Override public ItemStack getStackInSlot(int slot) { return forge.getStackInSlot(slot); }
            @Override public long getAmountInSlot(int slot) { return forge.getStackInSlot(slot).getCount(); }
            @Override public long getSlotCapacity(int slot) { return forge.getSlotLimit(slot); }

            @Override
            public long insertItem(int slot, ItemStack resource, long maxAmount, TransactionContext tx) {
                if (resource == null || resource.isEmpty() || maxAmount <= 0) return 0;
                ItemStack stack = resource.copy();
                stack.setCount(clampInt(maxAmount));
                ItemStack remainder = forge.insertItem(slot, stack, false);
                return maxAmount - remainder.getCount();
            }

            @Override
            public long extractItem(int slot, long maxAmount, TransactionContext tx) {
                return forge.extractItem(slot, clampInt(maxAmount), false).getCount();
            }

            @Override public boolean isItemValid(int slot, ItemStack resource) { return forge.isItemValid(slot, resource); }
        };
    }

    private static com.arc_studio.brick_lib_api.core.data.capability.builtin.IFluidHandler wrapForgeFluidHandler(
            net.minecraftforge.fluids.capability.IFluidHandler forge
    ) {
        return new com.arc_studio.brick_lib_api.core.data.capability.builtin.IFluidHandler() {
            @Override public int getTanks() { return forge.getTanks(); }
            @Override public net.minecraft.world.level.material.Fluid getFluidInTank(int tank) { return forge.getFluidInTank(tank).getFluid(); }
            @Override public long getFluidAmountInTank(int tank) { return mbToDroplets(forge.getFluidInTank(tank).getAmount()); }
            @Override public long getTankCapacity(int tank) { return mbToDroplets(forge.getTankCapacity(tank)); }
            @Override public boolean isFluidValid(int tank, net.minecraft.world.level.material.Fluid fluid) {
                return fluid != null && forge.isFluidValid(tank, new net.minecraftforge.fluids.FluidStack(fluid, 1));
            }

            @Override
            public long fill(net.minecraft.world.level.material.Fluid fluid, long maxAmount, TransactionContext tx) {
                if (fluid == null || maxAmount <= 0) return 0;
                int requested = dropletsToMb(maxAmount);
                if (requested <= 0) return 0;
                int filled = forge.fill(new net.minecraftforge.fluids.FluidStack(fluid, requested),
                        net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE);
                if (filled > 0) {
                    tx.addListener(new TransactionListener() {
                        @Override public void beforeCommit(TransactionContext tx) {
                            forge.fill(new net.minecraftforge.fluids.FluidStack(fluid, filled),
                                    net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
                        }
                        @Override public void afterCommit(TransactionContext tx) {}
                        @Override public void onAbort(TransactionContext tx) {}
                    });
                }
                return mbToDroplets(filled);
            }

            @Override
            public long drain(net.minecraft.world.level.material.Fluid fluid, long maxAmount, TransactionContext tx) {
                if (fluid == null || maxAmount <= 0) return 0;
                int requested = dropletsToMb(maxAmount);
                if (requested <= 0) return 0;
                var drained = forge.drain(new net.minecraftforge.fluids.FluidStack(fluid, requested),
                        net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE);
                if (!drained.isEmpty()) {
                    tx.addListener(new TransactionListener() {
                        @Override public void beforeCommit(TransactionContext tx) {
                            forge.drain(drained,
                                    net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
                        }
                        @Override public void afterCommit(TransactionContext tx) {}
                        @Override public void onAbort(TransactionContext tx) {}
                    });
                }
                return mbToDroplets(drained.getAmount());
            }

            @Override
            public long drain(long maxAmount, TransactionContext tx) {
                if (maxAmount <= 0) return 0;
                int requested = dropletsToMb(maxAmount);
                if (requested <= 0) return 0;
                var drained = forge.drain(requested,
                        net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE);
                if (!drained.isEmpty()) {
                    tx.addListener(new TransactionListener() {
                        @Override public void beforeCommit(TransactionContext tx) {
                            forge.drain(drained,
                                    net.minecraftforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
                        }
                        @Override public void afterCommit(TransactionContext tx) {}
                        @Override public void onAbort(TransactionContext tx) {}
                    });
                }
                return mbToDroplets(drained.getAmount());
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
    public static com.arc_studio.brick_lib_api.core.data.capability.builtin.IItemHandler findNeoForgeItemHandler(
            Level level, BlockPos pos, @Nullable Direction side
    ) {
        var neo = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK, pos, side);
        return neo != null ? wrapNeoForgeItemHandler(neo) : null;
    }

    @Nullable
    public static com.arc_studio.brick_lib_api.core.data.capability.builtin.IFluidHandler findNeoForgeFluidHandler(
            Level level, BlockPos pos, @Nullable Direction side
    ) {
        var neo = level.getCapability(net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK, pos, side);
        return neo != null ? wrapNeoForgeFluidHandler(neo) : null;
    }

    private static com.arc_studio.brick_lib_api.core.data.capability.builtin.IEnergyStorage wrapNeoForgeEnergy(
            net.neoforged.neoforge.energy.IEnergyStorage neo
    ) {
        return new com.arc_studio.brick_lib_api.core.data.capability.builtin.IEnergyStorage() {
            @Override public long receiveEnergy(long maxReceive, TransactionContext tx) { return neo.receiveEnergy(clampInt(maxReceive), false); }
            @Override public long extractEnergy(long maxExtract, TransactionContext tx) { return neo.extractEnergy(clampInt(maxExtract), false); }
            @Override public long getEnergyStored() { return neo.getEnergyStored(); }
            @Override public long getMaxEnergyStored() { return neo.getMaxEnergyStored(); }
            @Override public boolean canReceive() { return neo.canReceive(); }
            @Override public boolean canExtract() { return neo.canExtract(); }
        };
    }

    private static com.arc_studio.brick_lib_api.core.data.capability.builtin.IItemHandler wrapNeoForgeItemHandler(
            net.neoforged.neoforge.items.IItemHandler neo
    ) {
        return new com.arc_studio.brick_lib_api.core.data.capability.builtin.IItemHandler() {
            @Override public int getSlots() { return neo.getSlots(); }
            @Override public ItemStack getStackInSlot(int slot) { return neo.getStackInSlot(slot); }
            @Override public long getAmountInSlot(int slot) { return neo.getStackInSlot(slot).getCount(); }
            @Override public long getSlotCapacity(int slot) { return neo.getSlotLimit(slot); }

            @Override
            public long insertItem(int slot, ItemStack resource, long maxAmount, TransactionContext tx) {
                if (resource == null || resource.isEmpty() || maxAmount <= 0) return 0;
                ItemStack stack = resource.copy();
                stack.setCount(clampInt(maxAmount));
                ItemStack remainder = neo.insertItem(slot, stack, false);
                return maxAmount - remainder.getCount();
            }

            @Override public long extractItem(int slot, long maxAmount, TransactionContext tx) { return neo.extractItem(slot, clampInt(maxAmount), false).getCount(); }
            @Override public boolean isItemValid(int slot, ItemStack resource) { return neo.isItemValid(slot, resource); }
        };
    }

    private static com.arc_studio.brick_lib_api.core.data.capability.builtin.IFluidHandler wrapNeoForgeFluidHandler(
            net.neoforged.neoforge.fluids.capability.IFluidHandler neo
    ) {
        return new com.arc_studio.brick_lib_api.core.data.capability.builtin.IFluidHandler() {
            @Override public int getTanks() { return neo.getTanks(); }
            @Override public net.minecraft.world.level.material.Fluid getFluidInTank(int tank) { return neo.getFluidInTank(tank).getFluid(); }
            @Override public long getFluidAmountInTank(int tank) { return mbToDroplets(neo.getFluidInTank(tank).getAmount()); }
            @Override public long getTankCapacity(int tank) { return mbToDroplets(neo.getTankCapacity(tank)); }
            @Override public boolean isFluidValid(int tank, net.minecraft.world.level.material.Fluid fluid) {
                return fluid != null && neo.isFluidValid(tank, new net.neoforged.neoforge.fluids.FluidStack(fluid, 1));
            }
            @Override public long fill(net.minecraft.world.level.material.Fluid fluid, long maxAmount, TransactionContext tx) {
                if (fluid == null || maxAmount <= 0) return 0;
                int requested = dropletsToMb(maxAmount);
                if (requested <= 0) return 0;
                int filled = neo.fill(new net.neoforged.neoforge.fluids.FluidStack(fluid, requested),
                        net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE);
                if (filled > 0) {
                    tx.addListener(new TransactionListener() {
                        @Override public void beforeCommit(TransactionContext tx) {
                            neo.fill(new net.neoforged.neoforge.fluids.FluidStack(fluid, filled),
                                    net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
                        }
                        @Override public void afterCommit(TransactionContext tx) {}
                        @Override public void onAbort(TransactionContext tx) {}
                    });
                }
                return mbToDroplets(filled);
            }
            @Override public long drain(net.minecraft.world.level.material.Fluid fluid, long maxAmount, TransactionContext tx) {
                if (fluid == null || maxAmount <= 0) return 0;
                int requested = dropletsToMb(maxAmount);
                if (requested <= 0) return 0;
                var drained = neo.drain(new net.neoforged.neoforge.fluids.FluidStack(fluid, requested),
                        net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE);
                if (!drained.isEmpty()) {
                    tx.addListener(new TransactionListener() {
                        @Override public void beforeCommit(TransactionContext tx) {
                            neo.drain(drained,
                                    net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
                        }
                        @Override public void afterCommit(TransactionContext tx) {}
                        @Override public void onAbort(TransactionContext tx) {}
                    });
                }
                return mbToDroplets(drained.getAmount());
            }
            @Override public long drain(long maxAmount, TransactionContext tx) {
                if (maxAmount <= 0) return 0;
                int requested = dropletsToMb(maxAmount);
                if (requested <= 0) return 0;
                var drained = neo.drain(requested,
                        net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.SIMULATE);
                if (!drained.isEmpty()) {
                    tx.addListener(new TransactionListener() {
                        @Override public void beforeCommit(TransactionContext tx) {
                            neo.drain(drained,
                                    net.neoforged.neoforge.fluids.capability.IFluidHandler.FluidAction.EXECUTE);
                        }
                        @Override public void afterCommit(TransactionContext tx) {}
                        @Override public void onAbort(TransactionContext tx) {}
                    });
                }
                return mbToDroplets(drained.getAmount());
            }
        };
    }
    *///?}

    private static int clampInt(long value) {
        return (int) Math.max(0, Math.min(value, Integer.MAX_VALUE));
    }

    private static long mbToDroplets(int mb) {
        return Math.max(0, mb) * DROPLETS_PER_MB;
    }

    private static int dropletsToMb(long droplets) {
        return clampInt(droplets / DROPLETS_PER_MB);
    }
}
