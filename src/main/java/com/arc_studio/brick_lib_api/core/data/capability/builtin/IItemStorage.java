package com.arc_studio.brick_lib_api.core.data.capability.builtin;

import com.arc_studio.brick_lib_api.core.data.capability.transaction.BrickTransactionContext;
import net.minecraft.world.item.ItemStack;

/**
 * 物品存储能力接口
 * <p>
 * 定义物品的插入、提取、查询操作。
 * 资源类型为 Minecraft 的 ItemStack 概念抽象（使用 TransferVariant 包装）。
 * </p>
 */
public interface IItemStorage {

    /**
     * 获取槽位数量
     */
    int getSlots();

    /**
     * 获取指定槽位中的物品变体标识（如 Item 类型）
     *
     * @param slot 槽位索引
     * @return 槽位中的物品
     */
    ItemStack getStackInSlot(int slot);

    /**
     * 获取指定槽位中的物品数量
     *
     * @param slot 槽位索引
     * @return 数量
     */
    long getAmountInSlot(int slot);

    /**
     * 获取指定槽位的最大容量
     *
     * @param slot 槽位索引
     * @return 最大容量
     */
    long getSlotCapacity(int slot);

    /**
     * 向指定槽位插入物品
     *
     * @param slot      槽位索引
     * @param resource  物品资源
     * @param maxAmount 最大插入数量
     * @param tx        事务上下文
     * @return 实际插入数量
     */
    long insertItem(int slot, ItemStack resource, long maxAmount, BrickTransactionContext tx);

    /**
     * 从指定槽位提取物品
     *
     * @param slot      槽位索引
     * @param maxAmount 最大提取数量
     * @param tx        事务上下文
     * @return 实际提取数量
     */
    long extractItem(int slot, long maxAmount, BrickTransactionContext tx);

    /**
     * 检查指定槽位是否允许插入给定物品
     *
     * @param slot     槽位索引
     * @param resource 物品资源
     * @return 是否允许
     */
    boolean isItemValid(int slot, ItemStack resource);
}

