package com.arc_studio.brick_lib_api.core.data.capability.provider;

import com.arc_studio.brick_lib_api.core.data.BrickLazyOptional;
import com.arc_studio.brick_lib_api.core.data.capability.core.BrickCapability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 提供者注册表 — 按类型注册 Provider 工厂
 * <p>
 * 解决 Forge 需要手动为每个对象附加能力的问题。
 * 支持按目标类型（BlockEntity/Entity/ItemStack 等）批量注册。
 * </p>
 */
public final class ProviderRegistry {

    private static final Map<Class<?>, List<CapabilityProviderFactory<?>>> FACTORIES = new ConcurrentHashMap<>();

    private ProviderRegistry() {
    }

    /**
     * 为目标类型注册 Provider 工厂
     *
     * @param targetType 目标对象类型（BlockEntity/Entity/ItemStack 等）
     * @param factory    根据目标对象创建 Provider 的工厂
     */
    @SuppressWarnings("unchecked")
    public static <O> void register(Class<O> targetType, CapabilityProviderFactory<O> factory) {
        FACTORIES.computeIfAbsent(targetType, k -> new ArrayList<>())
                .add(factory);
    }

    /**
     * 获取目标对象的所有 Provider，合并为 CompositeProvider
     * <p>
     * 自动遍历目标对象的类层次结构，合并所有匹配的工厂。
     * </p>
     *
     * @param target 目标对象
     * @return 聚合的能力提供者
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static CapabilityProvider getProviders(Object target) {
        List<CapabilityProvider> providers = new ArrayList<>();

        // 遍历类层次结构
        Class<?> clazz = target.getClass();
        while (clazz != null) {
            List<CapabilityProviderFactory<?>> factories = FACTORIES.get(clazz);
            if (factories != null) {
                for (CapabilityProviderFactory factory : factories) {
                    CapabilityProvider provider = factory.create(target, null);
                    if (provider != null) {
                        providers.add(provider);
                    }
                }
            }

            // 检查接口
            for (Class<?> iface : clazz.getInterfaces()) {
                List<CapabilityProviderFactory<?>> ifaceFactories = FACTORIES.get(iface);
                if (ifaceFactories != null) {
                    for (CapabilityProviderFactory factory : ifaceFactories) {
                        CapabilityProvider provider = factory.create(target, null);
                        if (provider != null) {
                            providers.add(provider);
                        }
                    }
                }
            }

            clazz = clazz.getSuperclass();
        }

        if (providers.isEmpty()) {
            return EmptyProvider.INSTANCE;
        }
        if (providers.size() == 1) {
            return providers.get(0);
        }
        return CompositeProvider.of(providers);
    }

    /**
     * 空提供者单例
     */
    private enum EmptyProvider implements CapabilityProvider {
        INSTANCE;

        @Override
        public <T> BrickLazyOptional<T> getCapability(BrickCapability<T> cap, net.minecraft.core.Direction side) {
            return BrickLazyOptional.empty();
        }
    }
}

