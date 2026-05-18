package com.arc_studio.brick_lib_api.core.data.capability.transaction;

/**
 * 事务异常
 */
public class TransactionException extends RuntimeException {
    public TransactionException(String message) {
        super(message);
    }

    public TransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}

