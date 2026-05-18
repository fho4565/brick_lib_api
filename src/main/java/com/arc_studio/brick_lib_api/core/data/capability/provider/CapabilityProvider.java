package com.arc_studio.brick_lib_api.core.data.capability.provider;

import com.arc_studio.brick_lib_api.core.data.capability.core.Capability;
import com.arc_studio.brick_lib_api.core.data.capability.storage.Storage;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

/**
 * 能力提供者接口 — 类似 Forge ICapabilityProvider
 * <p>
 * 使用更高效的查询机制，支持方向感知和失效通知。
 * </p>
 */
public interface CapabilityProvider {

    /**
     * 查询能力
     *
     * @param cap  能力类型
     * @param side 方向（可为 null 表示无方向）
     * @return LazyOptional，支持失效通知
     */
    <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side);

    /**
     * 获取所有能力（用于序列化/调试）
     */
    default Map<Capability<?>, Storage<?>> getAllCapabilities() {
        return Map.of();
    }

    /**
     * 使所有惰性引用失效（对象销毁时调用）
     */
    default void invalidate() {
    }
}

