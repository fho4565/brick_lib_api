package com.arc_studio.brick_lib_api.core.data.capability.transaction;

/**
 * 事务上下文 — 传递给所有事务感知的操作
 */
public interface TransactionContext {
    /**
     * 获取事务的嵌套深度（外部事务为 0）
     */
    int nestingDepth();

    /**
     * 获取当前事务对象
     */
    Transaction getTransaction();

    /**
     * 添加快照参与者到此事务上下文
     */
    void addParticipant(SnapshotParticipant<?> participant);

    /**
     * 添加事务生命周期监听器
     */
    void addListener(TransactionListener listener);
}

