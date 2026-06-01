package com.arc_studio.brick_lib_api.core.data.capability.storage;

import com.arc_studio.brick_lib_api.core.data.capability.transaction.BrickTransactionContext;

/**
 * 存储视图 — 代表一个槽位或资源组的只读视图
 *
 * @param <T> 资源类型
 */
public interface StorageView<T> {

    /**
     * 获取当前视图中的资源变体
     */
    TransferVariant<T> getResource();

    /**
     * 获取当前资源数量
     */
    long getAmount();

    /**
     * 获取此视图的最大容量
     */
    long getCapacity();

    /**
     * 从当前视图提取资源
     *
     * @param maxAmount 最大提取数量
     * @param tx        事务上下文
     * @return 实际提取数量
     */
    long extract(long maxAmount, BrickTransactionContext tx);

    /**
     * 当前视图是否为空
     */
    default boolean isResourceBlank() {
        return getResource().isBlank() || getAmount() == 0;
    }

    /**
     * 获取底层存储视图引用（用于相等性比较）
     */
    default StorageView<T> getUnderlyingView() {
        return this;
    }
}

