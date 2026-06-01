package com.arc_studio.brick_lib_api.core.data.capability.builtin.example;

import com.arc_studio.brick_lib_api.core.data.capability.builtin.impl.SimpleEnergyStorage;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.BrickTransaction;
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
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;

/**
 * 熔炉 FE 能量交互处理器。
 * <p>
 * 玩家手持红石右键熔炉时，消耗 1 个红石并向熔炉存入最多 1,000 FE；
 * 手持空瓶右键熔炉时，从熔炉提取最多 1,000 FE（空瓶不消耗）。
 * </p>
 */
public final class FurnaceEnergyInteraction {

    private FurnaceEnergyInteraction() {
    }

    public static InteractionResult handleUseOnFurnace(
            BlockState state,
            Level level,
            BlockPos pos,
            Player player,
            InteractionHand hand,
            BlockHitResult hitResult
    ) {
        if (!state.is(Blocks.FURNACE)) {
            return InteractionResult.PASS;
        }

        ItemStack heldItem = player.getItemInHand(hand);
        if (!heldItem.is(Items.REDSTONE) && !heldItem.is(Items.GLASS_BOTTLE)) {
            return InteractionResult.PASS;
        }

        if (level.isClientSide) {
            return InteractionResult.SUCCESS;
        }

        ServerLevel serverLevel = (ServerLevel) level;
        if (heldItem.is(Items.REDSTONE)) {
            return handleCharge(serverLevel, pos, player, hand, heldItem);
        }
        return handleDischarge(serverLevel, pos, player);
    }

    private static InteractionResult handleCharge(
            ServerLevel level, BlockPos pos, Player player, InteractionHand hand, ItemStack redstone
    ) {
        FurnaceEnergyData data = FurnaceEnergyData.get(level);
        SimpleEnergyStorage storage = data.getOrCreate(pos);

        try (BrickTransaction tx = BrickTransaction.openOuter()) {
            long received = storage.receiveEnergy(FurnaceEnergyData.TRANSFER_AMOUNT, tx);
            if (received > 0) {
                tx.commit();
                data.setDirty();

                if (!player.getAbilities().instabuild) {
                    redstone.shrink(1);
                    if (redstone.isEmpty()) {
                        player.setItemInHand(hand, ItemStack.EMPTY);
                    }
                }

                level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.5F, 1.2F);
                sendEnergyMessage(player, "§e熔炉能量 +" + received + " FE，当前: "
                        + storage.getEnergyStored() + " / " + storage.getMaxEnergyStored() + " FE");
                return InteractionResult.SUCCESS;
            }
        }

        sendEnergyMessage(player, "§c熔炉能量已满！(" + storage.getEnergyStored()
                + " / " + storage.getMaxEnergyStored() + " FE)");
        return InteractionResult.SUCCESS;
    }

    private static InteractionResult handleDischarge(ServerLevel level, BlockPos pos, Player player) {
        FurnaceEnergyData data = FurnaceEnergyData.get(level);
        SimpleEnergyStorage storage = data.getOrCreate(pos);

        try (BrickTransaction tx = BrickTransaction.openOuter()) {
            long extracted = storage.extractEnergy(FurnaceEnergyData.TRANSFER_AMOUNT, tx);
            if (extracted > 0) {
                tx.commit();
                data.setDirty();

                level.playSound(null, pos, SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.BLOCKS, 0.5F, 0.6F);
                sendEnergyMessage(player, "§b熔炉能量 -" + extracted + " FE，当前: "
                        + storage.getEnergyStored() + " / " + storage.getMaxEnergyStored() + " FE");
                return InteractionResult.SUCCESS;
            }
        }

        sendEnergyMessage(player, "§7熔炉没有可减少的能量。当前: 0 / "
                + storage.getMaxEnergyStored() + " FE");
        return InteractionResult.SUCCESS;
    }

    private static void sendEnergyMessage(Player player, String message) {
        player.displayClientMessage(
                //? if > 1.18.2 {
                net.minecraft.network.chat.Component.literal(message),
                //?} else {
                /*new net.minecraft.network.chat.TextComponent(message),
                *///?}
                true
        );
    }
}

