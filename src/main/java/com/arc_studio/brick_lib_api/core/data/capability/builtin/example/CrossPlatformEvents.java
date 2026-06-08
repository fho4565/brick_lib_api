package com.arc_studio.brick_lib_api.core.data.capability.builtin.example;

import com.arc_studio.brick_lib_api.core.data.ResourceID;
import com.arc_studio.brick_lib_api.core.data.capability.CapabilityApi;
import com.arc_studio.brick_lib_api.core.data.capability.core.CapabilityEntry;
import com.arc_studio.brick_lib_api.core.register.BrickRegisterManager;
import com.arc_studio.brick_lib_api.register.BrickRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.BlockHitResult;

//? if neoforge {
/*import com.arc_studio.brick_lib_api.platform.NeoForgePlatform;
import net.neoforged.bus.api.IEventBus;
*///?}

/**
 * 跨加载器的事件入口 — 简化版，使用 {@link CapabilityApi} 声明式注册。
 * <p>
 * 注册两种能力方块：
 * <ul>
 *   <li>熔炉 — 能量能力 + 主动推出</li>
 *   <li>箱子 — 流体能力</li>
 * </ul>
 * </p>
 */
public final class CrossPlatformEvents {

    private CrossPlatformEvents() {
    }

    public static void register() {
        // ===== 1. 注册熔炉能量能力 =====
        ResourceID furnaceEnergyId = new ResourceID("brick_lib_api", "furnace_energy");
        BrickRegisterManager.register(
                BrickRegistries.CAPABILITY_ENERGY,
                furnaceEnergyId,
                () -> new CapabilityEntry<>(
                        Blocks.FURNACE,
                        furnaceEnergyId,
                        (level, pos, state, be, side) -> FurnaceEnergyData.get(level).getOrCreate(pos),
                        (level, pos) -> FurnaceEnergyData.get(level).setDirty()
                )
        );

        // ===== 2. 注册箱子流体能力 =====
        ResourceID chestFluidId = new ResourceID("brick_lib_api", "chest_fluid");
        BrickRegisterManager.register(
                BrickRegistries.CAPABILITY_FLUID,
                chestFluidId,
                () -> new CapabilityEntry<>(
                        Blocks.CHEST,
                        chestFluidId,
                        (level, pos, state, be, side) -> StoneFluidData.get(level).getOrCreate(pos),
                        (level, pos) -> StoneFluidData.get(level).setDirty()
                )
        );

        // ===== 3. 注册方块交互处理 =====
        CapabilityApi.registerInteraction(CrossPlatformEvents::handleInteraction);

        // ===== 4. 注册熔炉能量主动推出 =====
        CapabilityApi.registerEnergyEjector(
                (level, consumer) ->
                    FurnaceEnergyData.get(level).forEachPosition(consumer::accept),
                1000
        );

        // ===== 5. 初始化平台钩子 =====
        CapabilityApi.init();
    }

    //? if neoforge {
    /*public static void registerModBus(IEventBus modBus) {
        modBus.addListener(NeoForgePlatform::onRegisterCapabilities);
    }
    *///?}

    /**
     * 统一的右键交互处理 — 依次尝试熔炉和箱子交互。
     */
    private static InteractionResult handleInteraction(
            Player player, Level level, BlockPos pos, InteractionHand hand, BlockHitResult hitResult
    ) {
        var state = level.getBlockState(pos);

        InteractionResult result = FurnaceEnergyInteraction.handleUseOnFurnace(
                state, level, pos, player, hand, hitResult
        );
        if (result != InteractionResult.PASS) return result;

        return StoneFluidInteraction.handleUseOnStone(
                state, level, pos, player, hand, hitResult
        );
    }

    // ===== Forge：注册 StoneFluidBlockEntity（兼容旧世界） =====

    //? if forge {
    static {
        BrickRegisterManager.register(
                BrickRegistries.BLOCK_ENTITY_TYPE,
                new ResourceID("brick_lib_api", "stone_fluid"),
                () -> StoneFluidBlockEntity.TYPE = BlockEntityType.Builder.of(
                        StoneFluidBlockEntity::new, Blocks.CHEST
                ).build(null)
        );
    }
    //?}
}
