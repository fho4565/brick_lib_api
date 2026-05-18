package com.arc_studio.brick_lib_api.core.data.capability.builtin.impl;

import com.arc_studio.brick_lib_api.core.data.capability.builtin.IItemHandler;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.SnapshotParticipant;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.TransactionContext;
import com.arc_studio.brick_lib_api.platform.Platform;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * 物品存储的默认实现
 * <p>
 * 支持事务快照回滚。基于固定数量的槽位。
 * </p>
 */
public class SimpleItemStorage extends SnapshotParticipant<SimpleItemStorage.ItemSnapshot> implements IItemHandler {

    private final int slotCount;
    private final long slotCapacity;
    private final ItemStack[] items;
    private final long[] amounts;

    /**
     * @param slotCount    槽位数量
     * @param slotCapacity 每个槽位最大容量
     */
    public SimpleItemStorage(int slotCount, long slotCapacity) {
        this.slotCount = slotCount;
        this.slotCapacity = slotCapacity;
        this.items = new ItemStack[slotCount];
        this.amounts = new long[slotCount];
    }

    /**
     * 使用默认容量 64 创建
     */
    public SimpleItemStorage(int slotCount) {
        this(slotCount, 64);
    }

    @Override
    public int getSlots() {
        return slotCount;
    }

    @Override
    @Nullable
    public ItemStack getStackInSlot(int slot) {
        checkSlot(slot);
        return items[slot];
    }

    @Override
    public long getAmountInSlot(int slot) {
        checkSlot(slot);
        return amounts[slot];
    }

    @Override
    public long getSlotCapacity(int slot) {
        checkSlot(slot);
        return slotCapacity;
    }

    @Override
    public long insertItem(int slot, ItemStack resource, long maxAmount, TransactionContext tx) {
        checkSlot(slot);
        if (resource == null || maxAmount <= 0) return 0;
        if (!isItemValid(slot, resource)) return 0;

        // 槽位非空时必须是相同物品
        if (items[slot] != null && !Platform.itemEqual(items[slot], resource,true)) return 0;

        updateSnapshot(tx);

        long space = slotCapacity - amounts[slot];
        long inserted = Math.min(maxAmount, space);
        if (inserted > 0) {
            items[slot] = resource;
            amounts[slot] += inserted;
        }
        return inserted;
    }

    @Override
    public long extractItem(int slot, long maxAmount, TransactionContext tx) {
        checkSlot(slot);
        if (maxAmount <= 0 || items[slot] == null) return 0;

        updateSnapshot(tx);

        long extracted = Math.min(maxAmount, amounts[slot]);
        amounts[slot] -= extracted;
        if (amounts[slot] == 0) {
            items[slot] = null;
        }
        return extracted;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack resource) {
        checkSlot(slot);
        return resource != null;
    }

    /**
     * 直接设置槽位内容（绕过事务，用于反序列化）
     */
    public void setSlot(int slot, @Nullable ItemStack item, long amount) {
        checkSlot(slot);
        this.items[slot] = item;
        this.amounts[slot] = item == null ? 0 : Math.max(0, Math.min(amount, slotCapacity));
    }

    /**
     * 指定槽位是否为空
     */
    public boolean isSlotEmpty(int slot) {
        checkSlot(slot);
        return items[slot] == null || amounts[slot] == 0;
    }

    private void checkSlot(int slot) {
        if (slot < 0 || slot >= slotCount) {
            throw new IndexOutOfBoundsException("Slot index " + slot + " out of range [0, " + slotCount + ").");
        }
    }

    // ---- SnapshotParticipant ----

    @Override
    protected ItemSnapshot createSnapshot() {
        ItemStack[] copy = new ItemStack[slotCount];
        for (int i = 0; i < slotCount; i++) {
            copy[i] = items[i] != null ? items[i].copy() : null;
        }
        return new ItemSnapshot(copy, amounts.clone());
    }

    @Override
    protected void readSnapshot(ItemSnapshot snapshot) {
        System.arraycopy(snapshot.items, 0, this.items, 0, slotCount);
        System.arraycopy(snapshot.amounts, 0, this.amounts, 0, slotCount);
    }

    record ItemSnapshot(ItemStack[] items, long[] amounts) {
        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof ItemSnapshot that)) return false;
            return Arrays.deepEquals(items, that.items) && Arrays.equals(amounts, that.amounts);
        }

        @Override
        public int hashCode() {
            return 31 * Arrays.deepHashCode(items) + Arrays.hashCode(amounts);
        }
    }
}

