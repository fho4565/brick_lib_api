package com.arc_studio.brick_lib_api.core.data.capability.core;

import com.arc_studio.brick_lib_api.core.data.capability.storage.TransferVariant;

/**
 * 能力标识符 — 全局唯一的类型令牌
 * <p>
 * 结合 Forge 的 CapabilityToken 和 Fabric 的泛型设计。
 * 每个 Capability 实例标识一种特定的能力类型。
 * </p>
 *
 * @param <T> 能力接口类型
 */
public interface Capability<T> {

    /**
     * 唯一名称（通常是接口的规范类名）
     */
    String getName();

    /**
     * 获取能力代表的资源类型 Class
     */
    Class<T> getTypeClass();

    /**
     * 检查能力是否支持给定操作类型
     */
    boolean supportsOperation(OperationType op);

    /**
     * 创建此能力的空变体
     */
    default TransferVariant<T> blankVariant() {
        return TransferVariant.blank(this);
    }
}

