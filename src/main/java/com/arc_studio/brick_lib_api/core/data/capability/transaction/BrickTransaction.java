package com.arc_studio.brick_lib_api.core.data.capability.transaction;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 事务接口 — 支持嵌套、回滚、提交
 * <p>
 * 融合 Fabric BrickTransaction 设计，支持嵌套事务和快照回滚。
 * 使用 try-with-resources 确保自动关闭（未提交则自动 abort）。
 * </p>
 *
 * <pre>{@code
 * try (BrickTransaction tx = BrickTransaction.openOuter()) {
 *     long extracted = source.extract(resource, 1000, tx);
 *     long inserted = target.insert(resource, extracted, tx);
 *     if (inserted == extracted) {
 *         tx.commit();
 *     }
 *     // 未 commit 则 close 时自动 abort
 * }
 * }</pre>
 */
public class BrickTransaction implements AutoCloseable, BrickTransactionContext {

    private static final ThreadLocal<BrickTransaction> CURRENT = new ThreadLocal<>();

    private final int depth;
    @Nullable
    private final BrickTransaction parent;
    private boolean committed = false;

    private boolean closed = false;

    private final List<BrickSnapshotParticipant<?>> participants = new ArrayList<>();
    private final List<BrickTransactionListener> listeners = new ArrayList<>();

    private BrickTransaction(@Nullable BrickTransaction parent) {
        this.parent = parent;
        this.depth = parent == null ? 0 : parent.depth + 1;
        CURRENT.set(this);
    }

    /**
     * 打开一个新的外部事务
     *
     * @throws BrickTransactionException 如果当前线程已有活跃外部事务
     */
    public static BrickTransaction openOuter() {
        BrickTransaction current = getCurrentOpenTransaction();
        if (current != null) {
            return new BrickTransaction(current);
            //throw new BrickTransactionException("An outer transaction is already active on this thread. Use openNested() instead.");
        }
        return new BrickTransaction(null);
    }

    /**
     * 获取当前事务并打开
     *
     */
    public static BrickTransaction open() {
        return openNested(getCurrent());
    }

    /**
     * 打开一个嵌套事务
     *
     * @param parent 父事务上下文，可为 null（此时等同于 openOuter）
     */
    public static BrickTransaction openNested(@Nullable BrickTransactionContext parent) {
        if (parent == null) {
            return openOuter();
        }
        BrickTransaction parentTx = parent.getTransaction();
        while (parentTx != null && parentTx.closed) {
            parentTx = parentTx.parent;
        }
        if (parentTx == null) {
            return openOuter();
        }
        return new BrickTransaction(parentTx);
    }

    /**
     * 当前线程是否有活跃事务
     */
    public static boolean isActive() {
        return getCurrentOpenTransaction() != null;
    }

    /**
     * 获取当前线程的活跃事务（若无则返回 null）
     */
    @Nullable
    public static BrickTransaction getCurrent() {
        return getCurrentOpenTransaction();
    }

    /**
     * 提交事务 — 所有快照参与者的状态变为永久
     *
     * @throws BrickTransactionException 如果事务已关闭或已提交
     */
    public void commit() throws BrickTransactionException {
        ensureOpen();
        committed = true;

        // 通知监听器 beforeCommit
        for (BrickTransactionListener listener : listeners) {
            listener.beforeCommit(this);
        }

        // 通知快照参与者提交
        for (BrickSnapshotParticipant<?> participant : participants) {
            participant.onCommit();
        }

        // 通知监听器 afterCommit
        for (BrickTransactionListener listener : listeners) {
            listener.afterCommit(this);
        }
    }

    /**
     * 终止事务 — 所有快照参与者回滚到事务开始前的状态
     */
    public void abort() {
        if (closed || committed) {
            return;
        }
        ensureOpen();

        // 逆序回滚快照
        for (int i = participants.size() - 1; i >= 0; i--) {
            participants.get(i).rollbackSnapshot();
        }

        // 通知快照参与者回滚
        for (BrickSnapshotParticipant<?> participant : participants) {
            participant.onRollback();
        }

        // 通知监听器
        for (BrickTransactionListener listener : listeners) {
            listener.onAbort(this);
        }
    }

    /**
     * 关闭事务 — 未提交则自动 abort
     */
    @Override
    public void close() {
        if (closed) return;
        try {
            if (!committed) {
                abort();
            }

            // 释放快照资源
            for (BrickSnapshotParticipant<?> participant : participants) {
                participant.releaseCurrentSnapshot();
            }
        } finally {
            closed = true;
            // 恢复父事务为当前事务
            restoreParentTransaction();
        }
    }

    @Nullable
    private static BrickTransaction getCurrentOpenTransaction() {
        BrickTransaction current = CURRENT.get();
        while (current != null && current.closed) {
            current = current.parent;
        }
        if (current == null) {
            CURRENT.remove();
        } else {
            CURRENT.set(current);
        }
        return current;
    }

    private void restoreParentTransaction() {
        BrickTransaction current = parent;
        while (current != null && current.closed) {
            current = current.parent;
        }
        if (current == null) {
            CURRENT.remove();
        } else {
            CURRENT.set(current);
        }
    }

    @Override
    public int nestingDepth() {
        return depth;
    }

    @Override
    public BrickTransaction getTransaction() {
        return this;
    }

    @Override
    public void addParticipant(BrickSnapshotParticipant<?> participant) {
        ensureOpen();
        participants.add(participant);
    }

    @Override
    public void addListener(BrickTransactionListener listener) {
        ensureOpen();
        listeners.add(listener);
    }

    private void ensureOpen() {
        if (closed) {
            throw new BrickTransactionException("BrickTransaction is already closed.");
        }
        if (committed) {
            throw new BrickTransactionException("BrickTransaction is already committed.");
        }
    }
}

