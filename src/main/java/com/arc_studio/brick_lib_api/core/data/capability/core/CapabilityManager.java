package com.arc_studio.brick_lib_api.core.data.capability.core;

import com.arc_studio.brick_lib_api.BrickLibAPI;
import com.arc_studio.brick_lib_api.core.data.capability.BuiltinCapabilities;
import com.arc_studio.brick_lib_api.core.data.capability.IEnergyStorage;
import com.arc_studio.brick_lib_api.core.data.capability.IFluidStorage;
import com.arc_studio.brick_lib_api.core.data.capability.IItemStorage;
import com.arc_studio.brick_lib_api.core.data.BrickLazyOptional;
import com.arc_studio.brick_lib_api.core.data.capability.CapabilityProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import org.jetbrains.annotations.Nullable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
//? if fabric {
/*import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
*///?}
//? if forge {
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
//?}
//? if neoforge {
/*import net.neoforged.neoforge.capabilities.BlockCapability;
*///?}

/**
 * 能力系统跨加载器兼容层。
 * <p>
 * 查询顺序：UCS Provider / 静态注册的 provider 工厂 → 当前加载器原生能力 → 自定义原生映射。
 * 这样 UCS 代码可以访问其他模组暴露的原生物品、流体、能量能力；同时各加载器入口会注册
 * UCS → 原生的 fallback/provider，使其他模组管道也能发现 UCS 能力。
 * </p>
 */
public final class CapabilityManager {

    private static volatile boolean BUILTIN_MAPPINGS_INITIALIZED = false;

    private CapabilityManager() {
    }

    /**
     * 从方块位置查询能力 — 自动适配当前加载器的原生系统。
     */
    public static <T> BrickLazyOptional<T> getCapability(Level level, BlockPos pos, BrickCapability<T> cap, @Nullable Direction side) {
        BlockEntity be = level.getBlockEntity(pos);
        BrickLazyOptional<T> ucs = getFromUcsProvider(be, cap, side);
        if (ucs.isPresent()) {
            return ucs;
        }
        return getFromNativeLoader(level, pos, be, cap, side);
    }

    /**
     * 从 BlockEntity 查询能力 — 自动适配当前加载器。
     */
    public static <T> BrickLazyOptional<T> getCapability(BlockEntity be, BrickCapability<T> cap, @Nullable Direction side) {
        BrickLazyOptional<T> ucs = getFromUcsProvider(be, cap, side);
        if (ucs.isPresent()) {
            return ucs;
        }
        return getFromNativeBlockEntity(be, cap, side);
    }

    private static <T> BrickLazyOptional<T> getFromUcsProvider(@Nullable Object target, BrickCapability<T> cap, @Nullable Direction side) {
        if (target == null) {
            return BrickLazyOptional.empty();
        }

        if (target instanceof CapabilityProvider provider) {
            BrickLazyOptional<T> result = provider.getCapability(cap, side);
            if (result.isPresent()) {
                return result;
            }
        }

        CapabilityProvider registeredProvider = CapabilityProvider.getProviders(target);
        BrickLazyOptional<T> registeredResult = registeredProvider.getCapability(cap, side);
        if (registeredResult.isPresent()) {
            return registeredResult;
        }

        return BrickLazyOptional.empty();
    }

    // ========================
    // 原生加载器桥接
    // ========================

    private static <T> BrickLazyOptional<T> getFromNativeLoader(
            Level level, BlockPos pos, @Nullable BlockEntity be, BrickCapability<T> cap, @Nullable Direction side
    ) {
        //? if forge {
        return be != null ? getFromForgeProvider(be, cap, side) : BrickLazyOptional.empty();
        //?} else if neoforge {
        /*return getFromNeoForge(level, pos, cap, side);
        *///?} else if fabric {
        /*return getFromFabric(level, pos, cap, side);
        *///?}
    }

    private static <T> BrickLazyOptional<T> getFromNativeBlockEntity(BlockEntity be, BrickCapability<T> cap, @Nullable Direction side) {
        //? if forge {
        return getFromForgeProvider(be, cap, side);
        //?} else if neoforge {
        /*return be.getLevel() != null ? getFromNeoForge(be.getLevel(), be.getBlockPos(), cap, side) : BrickLazyOptional.empty();
        *///?} else if fabric {
        /*return be.getLevel() != null ? getFromFabric(be.getLevel(), be.getBlockPos(), cap, side) : BrickLazyOptional.empty();
        *///?}
    }

    // ========================
    // Forge 适配
    // ========================

    //? if forge {
    private static final Map<String, net.minecraftforge.common.capabilities.Capability<?>> FORGE_CAP_CACHE = new ConcurrentHashMap<>();

    public static <T, R> void registerForgeMapping(BrickCapability<T> ucsCap, Capability<R> forgeCap) {
        FORGE_CAP_CACHE.put(ucsCap.getName(), forgeCap);
    }

    @SuppressWarnings("unchecked")
    private static <T> BrickLazyOptional<T> getFromForgeProvider(BlockEntity be, BrickCapability<T> ucsCap, @Nullable Direction side) {
        if (ucsCap == BuiltinCapabilities.ENERGY) {
            IEnergyStorage energy = ForgeCapabilityAdapter.findEnergy(be, side);
            return energy != null ? BrickLazyOptional.of((T) energy) : BrickLazyOptional.empty();
        }
        if (ucsCap == BuiltinCapabilities.ITEM_HANDLER) {
            IItemStorage itemHandler = ForgeCapabilityAdapter.findItemHandler(be, side);
            return itemHandler != null ? BrickLazyOptional.of((T) itemHandler) : BrickLazyOptional.empty();
        }
        if (ucsCap == BuiltinCapabilities.FLUID_HANDLER) {
            IFluidStorage fluidHandler = ForgeCapabilityAdapter.findFluidHandler(be, side);
            return fluidHandler != null ? BrickLazyOptional.of((T) fluidHandler) : BrickLazyOptional.empty();
        }

        Capability<T> forgeCap = (Capability<T>) FORGE_CAP_CACHE.get(ucsCap.getName());
        if (forgeCap != null) {
            LazyOptional<T> forgeResult = be.getCapability(forgeCap, side);
            if (forgeResult.isPresent()) {
                return BrickLazyOptional.of(() -> forgeResult.orElseThrow(IllegalStateException::new));
            }
        }
        return BrickLazyOptional.empty();
    }

    public static ICapabilityProvider wrapAsForgeProvider(CapabilityProvider ucsProvider) {
        return new ICapabilityProvider() {
            @Override
            public <T> LazyOptional<T> getCapability(
                    Capability<T> cap, @Nullable Direction side
            ) {
                for (var entry : FORGE_CAP_CACHE.entrySet()) {
                    if (entry.getValue() == cap) {
                        BrickCapability<T> ucsCap = BrickCapability.of(entry.getKey());
                        BrickLazyOptional<T> ucsResult = ucsProvider.getCapability(ucsCap, side);
                        if (ucsResult.isPresent()) {
                            return LazyOptional.of(ucsResult::orElseThrow);
                        }
                    }
                }
                return LazyOptional.empty();
            }
        };
    }
    //?}

    // ========================
    // NeoForge 适配
    // ========================

    //? if neoforge {
    /*private static final java.util.Map<String, BlockCapability<?, Direction>> NEOFORGE_CAP_CACHE =
            new java.util.concurrent.ConcurrentHashMap<>();

    public static <T, R> void registerNeoForgeMapping(BrickCapability<T> ucsCap, BlockCapability<R, Direction> neoCap) {
        NEOFORGE_CAP_CACHE.put(ucsCap.getName(), neoCap);
    }

    @SuppressWarnings("unchecked")
    private static <T> BrickLazyOptional<T> getFromNeoForge(Level level, BlockPos pos, BrickCapability<T> ucsCap, @Nullable Direction side) {
        if (ucsCap == BuiltinCapabilities.ENERGY) {
            com.arc_studio.brick_lib_api.core.data.capability.IEnergyStorage energy =
                    ForgeCapabilityAdapter.findNeoForgeEnergy(level, pos, side);
            return energy != null ? BrickLazyOptional.of((T) energy) : BrickLazyOptional.empty();
        }
        if (ucsCap == BuiltinCapabilities.ITEM_HANDLER) {
            com.arc_studio.brick_lib_api.core.data.capability.IItemStorage itemHandler =
                    ForgeCapabilityAdapter.findNeoForgeItemHandler(level, pos, side);
            return itemHandler != null ? BrickLazyOptional.of((T) itemHandler) : BrickLazyOptional.empty();
        }
        if (ucsCap == BuiltinCapabilities.FLUID_HANDLER) {
            com.arc_studio.brick_lib_api.core.data.capability.IFluidStorage fluidHandler =
                    ForgeCapabilityAdapter.findNeoForgeFluidHandler(level, pos, side);
            return fluidHandler != null ? BrickLazyOptional.of((T) fluidHandler) : BrickLazyOptional.empty();
        }

        BlockCapability<?, Direction> neoCap = NEOFORGE_CAP_CACHE.get(ucsCap.getName());
        if (neoCap != null) {
            Object result = level.getCapability(neoCap, pos, side);
            if (result != null) {
                return BrickLazyOptional.of((T) result);
            }
        }
        return BrickLazyOptional.empty();
    }
    *///?}

    // ========================
    // Fabric 适配
    // ========================

    //? if fabric {
    /*public static final BlockApiLookup<IEnergyStorage, Direction> ENERGY_STORAGE_LOOKUP = BlockApiLookup.get(BrickLibAPI.ofPath("energy_storage"), IEnergyStorage.class, Direction.class);

    private static final Map<String, BlockApiLookup<?, Direction>> FABRIC_LOOKUP_CACHE = new ConcurrentHashMap<>();

    /^*
     * 注册 UCS BrickCapability 与 Fabric BlockApiLookup 的自定义映射。
     * <p>内置物品/流体能力会直接走 Fabric Transfer API，不需要手动注册。</p>
     ^/
    public static <T> void registerFabricMapping(BrickCapability<T> ucsCap, BlockApiLookup<T, Direction> fabricLookup) {
        FABRIC_LOOKUP_CACHE.put(ucsCap.getName(), fabricLookup);
    }

    @SuppressWarnings("unchecked")
    private static <T> BrickLazyOptional<T> getFromFabric(Level level, BlockPos pos, BrickCapability<T> ucsCap, @Nullable Direction side) {
        if (ucsCap == BuiltinCapabilities.ITEM_HANDLER) {
            IItemStorage itemHandler = FabricTransferAdapter.findItemHandler(level, pos, side);
            return itemHandler != null ? BrickLazyOptional.of((T) itemHandler) : BrickLazyOptional.empty();
        }
        if (ucsCap == BuiltinCapabilities.FLUID_HANDLER) {
            IFluidStorage fluidHandler = FabricTransferAdapter.findFluidHandler(level, pos, side);
            return fluidHandler != null ? BrickLazyOptional.of((T) fluidHandler) : BrickLazyOptional.empty();
        }

        BlockApiLookup<T, Direction> lookup = (BlockApiLookup<T, Direction>) FABRIC_LOOKUP_CACHE.get(ucsCap.getName());
        if (lookup != null) {
            T result = lookup.find(level, pos, side);
            if (result != null) {
                return BrickLazyOptional.of(result);
            }
        }
        return BrickLazyOptional.empty();
    }
    *///?}

    // ========================
    // 内置映射注册
    // ========================

    /**
     * 初始化内置能力映射 — 在模组初始化时调用。
     */
    @SuppressWarnings("removal")
    public static synchronized void initBuiltinMappings() {
        if (BUILTIN_MAPPINGS_INITIALIZED) {
            return;
        }
        BUILTIN_MAPPINGS_INITIALIZED = true;

        //? if forge {
        //? if >= 1.19.3 {
        registerForgeMapping(BuiltinCapabilities.ENERGY, net.minecraftforge.common.capabilities.ForgeCapabilities.ENERGY);
        registerForgeMapping(BuiltinCapabilities.ITEM_HANDLER, net.minecraftforge.common.capabilities.ForgeCapabilities.ITEM_HANDLER);
        registerForgeMapping(BuiltinCapabilities.FLUID_HANDLER, net.minecraftforge.common.capabilities.ForgeCapabilities.FLUID_HANDLER);
        //?} else {
        /*registerForgeMapping(BuiltinCapabilities.ENERGY, net.minecraftforge.energy.CapabilityEnergy.ENERGY);
        registerForgeMapping(BuiltinCapabilities.ITEM_HANDLER, net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
        registerForgeMapping(BuiltinCapabilities.FLUID_HANDLER, net.minecraftforge.fluids.capability.CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
        *///?}
        //?} else if neoforge {
        /*registerNeoForgeMapping(BuiltinCapabilities.ENERGY, net.neoforged.neoforge.capabilities.Capabilities.EnergyStorage.BLOCK);
        registerNeoForgeMapping(BuiltinCapabilities.ITEM_HANDLER, net.neoforged.neoforge.capabilities.Capabilities.ItemHandler.BLOCK);
        registerNeoForgeMapping(BuiltinCapabilities.FLUID_HANDLER, net.neoforged.neoforge.capabilities.Capabilities.FluidHandler.BLOCK);
        *///?} else if fabric {
        /*registerFabricMapping(BuiltinCapabilities.ENERGY, ENERGY_STORAGE_LOOKUP);
        FabricTransferAdapter.registerFallbacks();
        *///?}
    }
}
