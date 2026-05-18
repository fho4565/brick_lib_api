package com.arc_studio.brick_lib_api.core.data.capability.core;

/**
 * 操作类型枚举 — 扩展 Fabric 的传输操作概念
 */
public enum OperationType {
    /** 可插入 */
    INSERT,
    /** 可提取 */
    EXTRACT,
    /** 支持事务 */
    TRANSACT,
    /** 仅查询 */
    QUERY
}

