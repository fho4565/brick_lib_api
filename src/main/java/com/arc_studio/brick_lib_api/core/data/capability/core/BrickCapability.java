package com.arc_studio.brick_lib_api.core.data.capability.core;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 能力标识符 — 全局唯一的类型令牌。
 * <p>
 * 通过 {@link #of(Class)} 创建/获取能力实例：
 * <pre>{@code
 * BrickCapability<IEnergyStorage> ENERGY = BrickCapability.of(IEnergyStorage.class);
 * }</pre>
 * </p>
 *
 * @param <T> 能力接口类型
 */
public interface BrickCapability<T> {

    /**
     * 唯一名称（通常是接口的规范类名）
     */
    String getName();

    /**
     * 通过 Class 获取或创建一个 BrickCapability。
     */
    @SuppressWarnings("unchecked")
    static <T> BrickCapability<T> of(Class<T> type) {
        String name = type.getName();
        return (BrickCapability<T>) REGISTRY.computeIfAbsent(name, k -> new BrickCapability<T>() {
            @Override
            public String getName() {
                return name;
            }

            @Override
            public String toString() {
                return "BrickCapability[" + name + "]";
            }
        });
    }

    /**
     * 通过名称查找已注册的 BrickCapability。
     *
     * @throws IllegalArgumentException 如果未注册
     */
    @SuppressWarnings("unchecked")
    static <T> BrickCapability<T> of(String name) {
        BrickCapability<?> cap = REGISTRY.get(name);
        if (cap == null) {
            throw new IllegalArgumentException("No capability registered with name: " + name);
        }
        return (BrickCapability<T>) cap;
    }

    ConcurrentMap<String, BrickCapability<?>> REGISTRY = new ConcurrentHashMap<>();
}
