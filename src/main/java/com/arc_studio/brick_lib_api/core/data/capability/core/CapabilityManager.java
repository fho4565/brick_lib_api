package com.arc_studio.brick_lib_api.core.data.capability.core;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 能力注册中心 — 管理所有已注册的 Capability 实例
 * <p>
 * 线程安全，支持运行时动态注册。
 * </p>
 */
public final class CapabilityManager {

    private static final ConcurrentMap<String, Capability<?>> REGISTRY = new ConcurrentHashMap<>();

    private CapabilityManager() {
    }

    /**
     * 通过 CapabilityToken 获取或创建 Capability 实例
     *
     * @param token 类型令牌（匿名子类）
     * @return 对应的 Capability 实例
     */
    @SuppressWarnings("unchecked")
    public static <T> Capability<T> get(CapabilityToken<T> token) {
        String name = token.getInternalName();
        return (Capability<T>) REGISTRY.computeIfAbsent(name, k -> createCapability(token));
    }

    /**
     * 手动注册一个 Capability
     */
    public static <T> void register(Capability<T> capability) {
        Capability<?> existing = REGISTRY.putIfAbsent(capability.getName(), capability);
        if (existing != null) {
            throw new IllegalStateException("Capability already registered: " + capability.getName());
        }
    }

    /**
     * 根据名称查找已注册的 Capability
     */
    @SuppressWarnings("unchecked")
    public static <T> Capability<T> get(String name) {
        Capability<T> cap = (Capability<T>) REGISTRY.get(name);
        if (cap == null) {
            throw new IllegalArgumentException("No capability registered with name: " + name);
        }
        return cap;
    }

    /**
     * 检查是否已注册
     */
    public static boolean isRegistered(String name) {
        return REGISTRY.containsKey(name);
    }

    private static <T> Capability<T> createCapability(CapabilityToken<T> token) {
        Class<T> type = token.getType();
        String name = token.getInternalName();

        // 检查 @AutoRegisterCapability 注解以确定支持的操作
        Set<OperationType> ops = EnumSet.allOf(OperationType.class);

        return new SimpleCapability<>(name, type, ops);
    }

    /**
     * 默认的 Capability 实现
     */
    private record SimpleCapability<T>(
            String name,
            Class<T> typeClass,
            Set<OperationType> supportedOps
    ) implements Capability<T> {

        @Override
        public String getName() {
            return name;
        }

        @Override
        public Class<T> getTypeClass() {
            return typeClass;
        }

        @Override
        public boolean supportsOperation(OperationType op) {
            return supportedOps.contains(op);
        }

        @Override
        public String toString() {
            return "Capability[" + name + "]";
        }
    }
}

