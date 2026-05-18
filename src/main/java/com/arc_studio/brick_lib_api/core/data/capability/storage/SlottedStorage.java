package com.arc_studio.brick_lib_api.core.data.capability.storage;

import com.arc_studio.brick_lib_api.core.data.capability.transaction.TransactionContext;

import java.util.Collections;
import java.util.List;

/**
 * 带槽位的存储 — 融合 Fabric SlottedStorage
 * <p>
 * 提供基于索引的槽位访问和优化的堆叠插入。
 * </p>
 *
 * @param <T> 资源类型
 */
public interface SlottedStorage<T> extends Storage<T> {

    /**
     * 获取槽位数量
     */
    int getSlotCount();

    /**
     * 获取指定索引的槽位
     */
    Slot<T> getSlot(int index);

    /**
     * 批量获取所有槽位（不可变列表）
     */
    default List<Slot<T>> getSlots() {
        return Collections.unmodifiableList(
                java.util.stream.IntStream.range(0, getSlotCount())
                        .mapToObj(this::getSlot)
                        .toList()
        );
    }

    /**
     * 优先堆叠插入 — 先尝试填满相同变体的槽位，再使用空槽位
     *
     * @param resource  资源变体
     * @param maxAmount 最大插入数量
     * @param tx        事务上下文
     * @return 实际插入数量
     */
    default long insertStacking(TransferVariant<T> resource, long maxAmount, TransactionContext tx) {
        long remaining = maxAmount;

        // 第一轮：填充已有相同资源的槽位
        for (int i = 0; i < getSlotCount() && remaining > 0; i++) {
            Slot<T> slot = getSlot(i);
            if (!slot.isBlank() && slot.getResource().equals(resource) && slot.canInsert(resource)) {
                long space = slot.getCapacity() - slot.getAmount();
                if (space > 0) {
                    long toInsert = Math.min(remaining, space);
                    long inserted = slot.setResource(resource, slot.getAmount() + toInsert, tx) ? toInsert : 0;
                    remaining -= inserted;
                }
            }
        }

        // 第二轮：使用空槽位
        for (int i = 0; i < getSlotCount() && remaining > 0; i++) {
            Slot<T> slot = getSlot(i);
            if (slot.isBlank() && slot.canInsert(resource)) {
                long toInsert = Math.min(remaining, slot.getCapacity());
                long inserted = slot.setResource(resource, toInsert, tx) ? toInsert : 0;
                remaining -= inserted;
            }
        }

        return maxAmount - remaining;
    }
}

