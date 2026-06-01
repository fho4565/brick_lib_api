package com.arc_studio.brick_lib_api.core.data.capability.transaction;

/**
 * 事务异常
 */
public class BrickTransactionException extends RuntimeException {
    public BrickTransactionException(String message) {
        super(message);
    }

    public BrickTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}

