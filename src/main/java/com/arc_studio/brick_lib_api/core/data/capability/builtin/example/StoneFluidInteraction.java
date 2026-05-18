package com.arc_studio.brick_lib_api.core.data.capability.builtin.example;

import com.arc_studio.brick_lib_api.core.data.capability.builtin.IFluidHandler;
import com.arc_studio.brick_lib_api.core.data.capability.builtin.impl.SimpleFluidStorage;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;

/**
 * 石头流体交互处理器
 * <p>
 * 当玩家对石头方块使用水桶时，将水存入石头的流体能力中（最多 32 桶）。
 * 当玩家对石头方块使用空桶时，从石头中提取一桶水。
 * </p>
 *
 * <p>该类由 Mixin 在方块右键事件中调用。</p>
 */
public final class StoneFluidInteraction {

    private StoneFluidInteraction() {
    }

    /**
     * 处理对石头方块的右键交互
     *
     * @return InteractionResult.SUCCESS 如果交互成功，否则 PASS
     */
    public static InteractionResult handleUseOnStone(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        // 只处理石头方块
        if (!state.is(Blocks.CHEST)) {
            return InteractionResult.PASS;
        }

        // 只在服务端处理逻辑
        if (level.isClientSide) {
            ItemStack held = player.getItemInHand(hand);
            if (held.is(Items.WATER_BUCKET) || held.is(Items.BUCKET)) {
                return InteractionResult.SUCCESS;
            }
            return InteractionResult.PASS;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        ItemStack heldItem = player.getItemInHand(hand);

        // 水桶 → 向石头注水
        if (heldItem.is(Items.WATER_BUCKET)) {
            return handleFill(serverLevel, pos, player, hand, heldItem);
        }

        // 空桶 → 从石头取水
        if (heldItem.is(Items.BUCKET)) {
            return handleDrain(serverLevel, pos, player, hand, heldItem);
        }

        return InteractionResult.PASS;
    }

    /**
     * 水桶 → 注水
     */
    private static InteractionResult handleFill(
            ServerLevel level, BlockPos pos, Player player, InteractionHand hand, ItemStack waterBucket
    ) {
        StoneFluidData data = StoneFluidData.get(level);
        SimpleFluidStorage storage = data.getOrCreate(pos);

        try (Transaction tx = Transaction.openNested(Transaction.getCurrent())) {
            long filled = storage.fill(Fluids.WATER, IFluidHandler.BUCKET, tx);
            if (filled == IFluidHandler.BUCKET) {
                tx.commit();
                data.setDirty();

                // Forge: 不再替换原版 ChestBlockEntity，流体能力通过 AttachCapabilitiesEvent 附加。
                // NeoForge < 1.20.6: 放置 BlockEntity
                //? if neoforge {
                /*//? if < 1.20.6 {
                ensureBlockEntity(level, pos);
                //?}
                *///?}

                // 消耗水桶，给予空桶
                if (!player.getAbilities().instabuild) {
                    player.setItemInHand(hand, new ItemStack(Items.BUCKET));
                }

                // 播放倒水音效
                level.playSound(null, pos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);

                // 通知玩家当前存储量
                long stored = storage.getFluidAmountInTank(0);
                long buckets = stored / IFluidHandler.BUCKET;
                player.displayClientMessage(
                        //? if > 1.18.2 {
                        net.minecraft.network.chat.Component.literal("§b水: " + buckets + " / 32 桶"),
                        //?} else {
                        /*new net.minecraft.network.chat.TextComponent("§b水: " + buckets + " / 32 桶"),
                        *///?}
                        true
                );

                return InteractionResult.SUCCESS;
            }
            // 事务自动回滚（储罐满了）
        } catch (Exception ignored) {

        }

        // 储罐满了，提示
        player.displayClientMessage(
                //? if > 1.18.2 {
                net.minecraft.network.chat.Component.literal("§c石头已满！(32/32 桶)"),
                //?} else {
                /*new net.minecraft.network.chat.TextComponent("§c石头已满！(32/32 桶)"),
                *///?}
                true
        );
        return InteractionResult.PASS;
    }

    /**
     * 空桶 → 取水
     */
    private static InteractionResult handleDrain(
            ServerLevel level, BlockPos pos, Player player, InteractionHand hand, ItemStack emptyBucket
    ) {
        StoneFluidData data = StoneFluidData.get(level);
        SimpleFluidStorage storage = data.getOrCreate(pos);

        // 没水就不处理
        if (storage.isEmpty()) {
            return InteractionResult.PASS;
        }

        try (Transaction tx = Transaction.openOuter()) {
            long drained = storage.drain(Fluids.WATER, IFluidHandler.BUCKET, tx);
            if (drained == IFluidHandler.BUCKET) {
                tx.commit();
                data.setDirty();

                // 消耗空桶，给予水桶
                if (!player.getAbilities().instabuild) {
                    emptyBucket.shrink(1);
                    ItemStack waterBucket = new ItemStack(Items.WATER_BUCKET);
                    if (!player.getInventory().add(waterBucket)) {
                        player.drop(waterBucket, false);
                    }
                }

                // 播放装水音效
                level.playSound(null, pos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);

                // 通知玩家当前存储量
                long stored = storage.getFluidAmountInTank(0);
                long buckets = stored / IFluidHandler.BUCKET;
                player.displayClientMessage(
                        //? if > 1.18.2 {
                        net.minecraft.network.chat.Component.literal("§b水: " + buckets + " / 32 桶"),
                        //?} else {
                        /*new net.minecraft.network.chat.TextComponent("§b水: " + buckets + " / 32 桶"),
                        *///?}
                        true
                );


                return InteractionResult.SUCCESS;
            }
            // 不够一桶，事务自动回滚
        }

        return InteractionResult.PASS;
    }

    // ========================
    // Forge / NeoForge: BlockEntity 管理
    // ========================


    //? if neoforge {
    /*//? if < 1.20.6 {
    private static void ensureBlockEntity(ServerLevel level, BlockPos pos) {
        BlockEntity existing = level.getBlockEntity(pos);
        if (!(existing instanceof StoneFluidBlockEntity)) {
            level.setBlockEntity(new StoneFluidBlockEntity(pos, level.getBlockState(pos)));
        }
    }
    //?}
    *///?}
}



