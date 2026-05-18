package com.arc_studio.brick_lib_api.core.data.capability.storage;

import com.arc_studio.brick_lib_api.core.data.capability.transaction.TransactionContext;

/**
 * 槽位接口 — 类似 Fabric SingleSlotStorage
 * <p>
 * 代表带索引的单个存储槽位，支持原子性设置操作。
 * </p>
 *
 * @param <T> 资源类型
 */
public interface Slot<T> extends StorageView<T> {

    /**
     * 获取槽位索引
     */
    int getIndex();

    /**
     * 槽位是否为空
     */
    boolean isBlank();

    /**
     * 尝试设置槽位内容（原子操作，需事务）
     *
     * @param variant 资源变体
     * @param amount  数量
     * @param tx      事务上下文
     * @return 是否设置成功
     */
    boolean setResource(TransferVariant<T> variant, long amount, TransactionContext tx);

    /**
     * 槽位是否允许插入给定资源
     */
    boolean canInsert(TransferVariant<T> resource);

    /**
     * 槽位是否允许提取
     */
    boolean canExtract();
}

