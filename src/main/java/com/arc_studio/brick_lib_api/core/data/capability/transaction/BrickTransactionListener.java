package com.arc_studio.brick_lib_api.core.data.capability.transaction;

/**
 * 事务监听器 — 扩展 Fabric 的 CloseCallback
 */
public interface BrickTransactionListener {
    /**
     * 事务提交前调用
     */
    void beforeCommit(BrickTransactionContext tx);

    /**
     * 事务提交后调用
     */
    void afterCommit(BrickTransactionContext tx);

    /**
     * 事务回滚时调用
     */
    void onAbort(BrickTransactionContext tx);
}

