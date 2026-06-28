package com.arc_studio.brick_lib_api.core.data.capability.transaction;

//? if > 1.21.8 {
import net.neoforged.neoforge.transfer.transaction.TransactionContext;
//? }

/**
 * 事务上下文 — 传递给所有事务感知的操作
 */
public interface BrickTransactionContext /*? if > 1.21.8 {*/ extends TransactionContext /*?}*/ {
    /**
     * 获取事务的嵌套深度（外部事务为 0）
     */
    int nestingDepth();

    /**
     * 获取当前事务对象
     */
    BrickTransaction getTransaction();

    /**
     * 添加快照参与者到此事务上下文
     */
    void addParticipant(BrickSnapshotParticipant<?> participant);

    /**
     * 添加事务生命周期监听器
     */
    void addListener(BrickTransactionListener listener);
}

