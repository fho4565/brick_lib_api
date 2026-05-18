package com.arc_studio.brick_lib_api.core.data.capability.compat;

import com.arc_studio.brick_lib_api.core.data.capability.builtin.BuiltinCapabilities;
import com.arc_studio.brick_lib_api.core.data.capability.core.Capability;
import com.arc_studio.brick_lib_api.core.data.capability.provider.CapabilityProvider;
import com.arc_studio.brick_lib_api.core.data.capability.provider.LazyOptional;
import com.arc_studio.brick_lib_api.core.data.capability.provider.ProviderRegistry;
//? if fabric {
/*import com.arc_studio.brick_lib_api.BrickLibAPI;
import com.arc_studio.brick_lib_api.core.data.capability.builtin.IEnergyStorage;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
*///?}
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

//? if forge {
import net.minecraftforge.common.capabilities.ICapabilityProvider;
//?}
//? if neoforge {
/*import net.neoforged.neoforge.capabilities.BlockCapability;
*///?}

/**
 * 能力系统跨加载器兼容层。
 * <p>
 * 查询顺序：UCS Provider / ProviderRegistry → 当前加载器原生能力 → 自定义原生映射。
 * 这样 UCS 代码可以访问其他模组暴露的原生物品、流体、能量能力；同时各加载器入口会注册
 * UCS → 原生的 fallback/provider，使其他模组管道也能发现 UCS 能力。
 * </p>
 */
public final class CapabilityCompat {

    private static volatile boolean BUILTIN_MAPPINGS_INITIALIZED = false;

    private CapabilityCompat() {
    }

    /**
     * 从方块位置查询能力 — 自动适配当前加载器的原生系统。
     */
    public static <T> LazyOptional<T> getCapability(Level level, BlockPos pos, Capability<T> cap, @Nullable Direction side) {
        BlockEntity be = level.getBlockEntity(pos);
        LazyOptional<T> ucs = getFromUcsProvider(be, cap, side);
        if (ucs.isPresent()) {
            return ucs;
        }
        return getFromNativeLoader(level, pos, be, cap, side);
    }

    /**
     * 从 BlockEntity 查询能力 — 自动适配当前加载器。
     */
    public static <T> LazyOptional<T> getCapability(BlockEntity be, Capability<T> cap, @Nullable Direction side) {
        LazyOptional<T> ucs = getFromUcsProvider(be, cap, side);
        if (ucs.isPresent()) {
            return ucs;
        }
        return getFromNativeBlockEntity(be, cap, side);
    }

    private static <T> LazyOptional<T> getFromUcsProvider(@Nullable Object target, Capability<T> cap, @Nullable Direction side) {
        if (target == null) {
            return LazyOptional.empty();
        }

        if (target instanceof CapabilityProvider provider) {
            LazyOptional<T> result = provider.getCapability(cap, side);
            if (result.isPresent()) {
                return result;
            }
        }

        CapabilityProvider registeredProvider = ProviderRegistry.getProviders(target);
        LazyOptional<T> registeredResult = registeredProvider.getCapability(cap, side);
        if (registeredResult.isPresent()) {
            return registeredResult;
        }

        return LazyOptional.empty();
    }

    // ========================
    // 原生加载器桥接
    // ========================

    @SuppressWarnings("unchecked")
    private static <T> LazyOptional<T> getFromNativeLoader(
            Level level, BlockPos pos, @Nullable BlockEntity be, Capability<T> cap, @Nullable Direction side
    ) {
        //? if forge {
        return be != null ? getFromForgeProvider(be, cap, side) : LazyOptional.empty();
        //?} else if neoforge {
        /*return getFromNeoForge(level, pos, cap, side);
        *///?} else if fabric {
        /*return getFromFabric(level, pos, cap, side);
        *///?}
    }

    private static <T> LazyOptional<T> getFromNativeBlockEntity(
            BlockEntity be, Capability<T> cap, @Nullable Direction side
    ) {
        //? if forge {
        return getFromForgeProvider(be, cap, side);
        //?} else if neoforge {
        /*return be.getLevel() != null ? getFromNeoForge(be.getLevel(), be.getBlockPos(), cap, side) : LazyOptional.empty();
        *///?} else if fabric {
        /*return be.getLevel() != null ? getFromFabric(be.getLevel(), be.getBlockPos(), cap, side) : LazyOptional.empty();
        *///?}
    }

    // ========================
    // Forge 适配
    // ========================

    //? if forge {
    private static final java.util.Map<String, net.minecraftforge.common.capabilities.Capability<?>> FORGE_CAP_CACHE =
            new java.util.concurrent.ConcurrentHashMap<>();

    public static <T, R> void registerForgeMapping(Capability<T> ucsCap, net.minecraftforge.common.capabilities.Capability<R> forgeCap) {
        FORGE_CAP_CACHE.put(ucsCap.getName(), forgeCap);
    }

    @SuppressWarnings("unchecked")
    private static <T> LazyOptional<T> getFromForgeProvider(BlockEntity be, Capability<T> ucsCap, @Nullable Direction side) {
        if (ucsCap == BuiltinCapabilities.ENERGY) {
            com.arc_studio.brick_lib_api.core.data.capability.builtin.IEnergyStorage energy =
                    ForgeCapabilityAdapter.findEnergy(be, side);
            return energy != null ? LazyOptional.of((T) energy) : LazyOptional.empty();
        }
        if (ucsCap == BuiltinCapabilities.ITEM_HANDLER) {
            com.arc_studio.brick_lib_api.core.data.capability.builtin.IItemHandler itemHandler =
                    ForgeCapabilityAdapter.findItemHandler(be, side);
            return itemHandler != null ? LazyOptional.of((T) itemHandler) : LazyOptional.empty();
        }
        if (ucsCap == BuiltinCapabilities.FLUID_HANDLER) {
            com.arc_studio.brick_lib_api.core.data.capability.builtin.IFluidHandler fluidHandler =
                    ForgeCapabilityAdapter.findFluidHandler(be, side);
            return fluidHandler != null ? LazyOptional.of((T) fluidHandler) : LazyOptional.empty();
        }

        net.minecraftforge.common.capabilities.Capability<T> forgeCap =
                (net.minecraftforge.common.capabilities.Capability<T>) FORGE_CAP_CACHE.get(ucsCap.getName());
        if (forgeCap != null) {
            net.minecraftforge.common.util.LazyOptional<T> forgeResult = be.getCapability(forgeCap, side);
            if (forgeResult.isPresent()) {
                return LazyOptional.of(() -> forgeResult.orElseThrow(IllegalStateException::new));
            }
        }
        return LazyOptional.empty();
    }

    public static ICapabilityProvider wrapAsForgeProvider(CapabilityProvider ucsProvider) {
        return new ICapabilityProvider() {
            @Override
            public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(
                    net.minecraftforge.common.capabilities.Capability<T> cap, @Nullable Direction side
            ) {
                for (var entry : FORGE_CAP_CACHE.entrySet()) {
                    if (entry.getValue() == cap) {
                        Capability<T> ucsCap = (Capability<T>) com.arc_studio.brick_lib_api.core.data.capability.core.CapabilityManager.get(entry.getKey());
                        LazyOptional<T> ucsResult = ucsProvider.getCapability(ucsCap, side);
                        if (ucsResult.isPresent()) {
                            return net.minecraftforge.common.util.LazyOptional.of(ucsResult::orElseThrow);
                        }
                    }
                }
                return net.minecraftforge.common.util.LazyOptional.empty();
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

    public static <T, R> void registerNeoForgeMapping(Capability<T> ucsCap, BlockCapability<R, Direction> neoCap) {
        NEOFORGE_CAP_CACHE.put(ucsCap.getName(), neoCap);
    }

    @SuppressWarnings("unchecked")
    private static <T> LazyOptional<T> getFromNeoForge(Level level, BlockPos pos, Capability<T> ucsCap, @Nullable Direction side) {
        if (ucsCap == BuiltinCapabilities.ENERGY) {
            com.arc_studio.brick_lib_api.core.data.capability.builtin.IEnergyStorage energy =
                    ForgeCapabilityAdapter.findNeoForgeEnergy(level, pos, side);
            return energy != null ? LazyOptional.of((T) energy) : LazyOptional.empty();
        }
        if (ucsCap == BuiltinCapabilities.ITEM_HANDLER) {
            com.arc_studio.brick_lib_api.core.data.capability.builtin.IItemHandler itemHandler =
                    ForgeCapabilityAdapter.findNeoForgeItemHandler(level, pos, side);
            return itemHandler != null ? LazyOptional.of((T) itemHandler) : LazyOptional.empty();
        }
        if (ucsCap == BuiltinCapabilities.FLUID_HANDLER) {
            com.arc_studio.brick_lib_api.core.data.capability.builtin.IFluidHandler fluidHandler =
                    ForgeCapabilityAdapter.findNeoForgeFluidHandler(level, pos, side);
            return fluidHandler != null ? LazyOptional.of((T) fluidHandler) : LazyOptional.empty();
        }

        BlockCapability<?, Direction> neoCap = NEOFORGE_CAP_CACHE.get(ucsCap.getName());
        if (neoCap != null) {
            Object result = level.getCapability(neoCap, pos, side);
            if (result != null) {
                return LazyOptional.of((T) result);
            }
        }
        return LazyOptional.empty();
    }
    *///?}

    // ========================
    // Fabric 适配
    // ========================

    //? if fabric {
    /*public static final BlockApiLookup<IEnergyStorage, Direction> ENERGY_STORAGE_LOOKUP = BlockApiLookup.get(
            //? if >= 1.21 {
            /^net.minecraft.resources.ResourceLocation.fromNamespaceAndPath(BrickLibAPI.MOD_ID, "energy_storage"),
            ^///?} else {
            new net.minecraft.resources.ResourceLocation(BrickLibAPI.MOD_ID, "energy_storage"),
            //?}
            IEnergyStorage.class,
            Direction.class
    );

    private static final java.util.Map<String, BlockApiLookup<?, Direction>> FABRIC_LOOKUP_CACHE =
            new java.util.concurrent.ConcurrentHashMap<>();

    /^*
     * 注册 UCS Capability 与 Fabric BlockApiLookup 的自定义映射。
     * <p>内置物品/流体能力会直接走 Fabric Transfer API，不需要手动注册。</p>
     ^/
    public static <T> void registerFabricMapping(Capability<T> ucsCap, BlockApiLookup<T, Direction> fabricLookup) {
        FABRIC_LOOKUP_CACHE.put(ucsCap.getName(), fabricLookup);
    }

    @SuppressWarnings("unchecked")
    private static <T> LazyOptional<T> getFromFabric(Level level, BlockPos pos, Capability<T> ucsCap, @Nullable Direction side) {
        if (ucsCap == BuiltinCapabilities.ITEM_HANDLER) {
            com.arc_studio.brick_lib_api.core.data.capability.builtin.IItemHandler itemHandler =
                    FabricTransferAdapter.findItemHandler(level, pos, side);
            return itemHandler != null ? LazyOptional.of((T) itemHandler) : LazyOptional.empty();
        }
        if (ucsCap == BuiltinCapabilities.FLUID_HANDLER) {
            com.arc_studio.brick_lib_api.core.data.capability.builtin.IFluidHandler fluidHandler =
                    FabricTransferAdapter.findFluidHandler(level, pos, side);
            return fluidHandler != null ? LazyOptional.of((T) fluidHandler) : LazyOptional.empty();
        }

        BlockApiLookup<T, Direction> lookup =
                (BlockApiLookup<T, Direction>) FABRIC_LOOKUP_CACHE.get(ucsCap.getName());
        if (lookup != null) {
            T result = lookup.find(level, pos, side);
            if (result != null) {
                return LazyOptional.of(result);
            }
        }
        return LazyOptional.empty();
    }
    *///?}

    // ========================
    // 内置映射注册
    // ========================

    /**
     * 初始化内置能力映射 — 在模组初始化时调用。
     */
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
