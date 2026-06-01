package com.arc_studio.brick_lib_api.core.data.capability.builtin.example;

import com.arc_studio.brick_lib_api.core.data.ResourceID;
import com.arc_studio.brick_lib_api.core.data.capability.builtin.impl.SimpleEnergyStorage;
import com.arc_studio.brick_lib_api.core.data.capability.compat.BlockInteractionApi;
import com.arc_studio.brick_lib_api.core.data.capability.compat.CapabilityRegistration;
import com.arc_studio.brick_lib_api.core.data.capability.compat.EnergyEjectorApi;
import com.arc_studio.brick_lib_api.platform.NeoForgePlatform;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.phys.BlockHitResult;

//? if forge {
import com.arc_studio.brick_lib_api.core.register.BrickRegisterManager;
import com.arc_studio.brick_lib_api.register.BrickRegistries;

import java.util.function.BiConsumer;
//?}
//? if neoforge {
/*import net.neoforged.bus.api.IEventBus;
*///?}

/**
 * 跨加载器的事件入口 — 简化版，使用 {@link CapabilityRegistration} /
 * {@link BlockInteractionApi} / {@link EnergyEjectorApi} 声明式注册。
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
        CapabilityRegistration.registerEnergyBlock(Blocks.FURNACE,
                new ResourceID("brick_lib_api", "furnace_energy"),
                (level, pos, state, be, side) ->
                    FurnaceEnergyData.get(level).getOrCreate(pos)
        );

        // ===== 2. 注册箱子流体能力 =====
        CapabilityRegistration.registerFluidBlock(Blocks.CHEST,
                new ResourceID("brick_lib_api", "chest_fluid"),
                (level, pos, state, be, side) ->
                    StoneFluidData.get(level).getOrCreate(pos)
        );

        // ===== 3. 初始化能力注册（平台钩子） =====
        CapabilityRegistration.init();

        // ===== 4. 注册方块交互处理 =====
        BlockInteractionApi.register(CrossPlatformEvents::handleInteraction);
        BlockInteractionApi.init();

        // ===== 5. 注册熔炉能量主动推出 =====
        EnergyEjectorApi.register(
                (level, consumer) ->
                    FurnaceEnergyData.get(level).forEachPosition(consumer::accept),
                1000
        );
        EnergyEjectorApi.init();
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
