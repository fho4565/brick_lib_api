package com.arc_studio.brick_lib_api.core.data.capability.transaction;

/**
 * 事务监听器 — 扩展 Fabric 的 CloseCallback
 */
public interface TransactionListener {
    /**
     * 事务提交前调用
     */
    void beforeCommit(TransactionContext tx);

    /**
     * 事务提交后调用
     */
    void afterCommit(TransactionContext tx);

    /**
     * 事务回滚时调用
     */
    void onAbort(TransactionContext tx);
}

