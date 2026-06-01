package com.arc_studio.brick_lib_api.core.data.capability.util;

import com.arc_studio.brick_lib_api.core.data.capability.storage.TransferVariant;
import com.arc_studio.brick_lib_api.core.data.capability.transaction.BrickTransactionContext;

/**
 * 前置条件校验工具类
 */
public final class Preconditions {

    private Preconditions() {
    }

    /**
     * 检查对象非空
     */
    public static void notNull(Object obj, String message) {
        if (obj == null) {
            throw new NullPointerException(message);
        }
    }

    /**
     * 检查值非负
     */
    public static void notNegative(long value) {
        if (value < 0) {
            throw new IllegalArgumentException("Value must not be negative, got: " + value);
        }
    }

    /**
     * 检查变体非空
     */
    public static void notBlank(TransferVariant<?> variant) {
        if (variant == null || variant.isBlank()) {
            throw new IllegalArgumentException("TransferVariant must not be blank.");
        }
    }

    /**
     * 检查事务上下文非空
     */
    public static void inTransaction(BrickTransactionContext tx) {
        if (tx == null) {
            throw new IllegalStateException("Operation requires an active BrickTransactionContext.");
        }
    }
}

