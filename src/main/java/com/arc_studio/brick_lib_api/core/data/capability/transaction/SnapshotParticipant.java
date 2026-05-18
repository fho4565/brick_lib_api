package com.arc_studio.brick_lib_api.core.data.capability.transaction;

import org.jetbrains.annotations.Nullable;

/**
 * 快照参与者 — 支持原子性状态回滚
 * <p>
 * 融合 Fabric 设计，但简化版本管理。
 * 实现者只需提供 {@link #createSnapshot()} 和 {@link #readSnapshot(S)} 方法。
 * </p>
 *
 * @param <S> 快照数据类型
 */
public abstract class SnapshotParticipant<S> {

    @Nullable
    private S currentSnapshot;

    /**
     * 创建当前状态快照
     */
    protected abstract S createSnapshot();

    /**
     * 从快照恢复状态
     */
    protected abstract void readSnapshot(S snapshot);

    /**
     * 释放快照资源（可选，用于对象池优化）
     */
    protected void releaseSnapshot(S snapshot) {
    }

    /**
     * 事务最终提交后调用（用于 markDirty 等通知）
     */
    protected void onCommit() {
    }

    /**
     * 事务回滚后调用
     */
    protected void onRollback() {
    }

    /**
     * 由事务系统调用 — 在参与事务前保存快照
     */
    public final void updateSnapshot(TransactionContext tx) {
        if (currentSnapshot == null) {
            currentSnapshot = createSnapshot();
        }
        tx.addParticipant(this);
    }

    /**
     * 由事务系统调用 — 回滚到快照状态
     */
    final void rollbackSnapshot() {
        if (currentSnapshot != null) {
            readSnapshot(currentSnapshot);
        }
    }

    /**
     * 由事务系统调用 — 释放快照资源
     */
    final void releaseCurrentSnapshot() {
        if (currentSnapshot != null) {
            releaseSnapshot(currentSnapshot);
            currentSnapshot = null;
        }
    }
}

