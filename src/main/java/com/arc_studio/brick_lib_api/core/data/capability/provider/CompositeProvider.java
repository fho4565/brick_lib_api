package com.arc_studio.brick_lib_api.core.data.capability.provider;

import com.arc_studio.brick_lib_api.core.data.BrickLazyOptional;
import com.arc_studio.brick_lib_api.core.data.capability.core.BrickCapability;
import com.arc_studio.brick_lib_api.core.data.capability.storage.Storage;
import net.minecraft.core.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 聚合提供者 — 优化 Forge CapabilityDispatcher
 * <p>
 * 使用 ConcurrentHashMap 缓存实现 O(1) 平均查询时间。
 * 支持运行时动态添加/移除 Provider，线程安全。
 * </p>
 */
public class CompositeProvider implements CapabilityProvider {

    private final CopyOnWriteArrayList<CapabilityProvider> providers;

    /**
     * 缓存：(BrickCapability.name + ":" + side) -> BrickLazyOptional
     * 在添加/移除 provider 时清除缓存
     */
    private final ConcurrentHashMap<String, BrickLazyOptional<?>> cache = new ConcurrentHashMap<>();

    private CompositeProvider(List<CapabilityProvider> providers) {
        this.providers = new CopyOnWriteArrayList<>(providers);
    }

    /**
     * 构建聚合提供者
     *
     * @param providers 按优先级排序的提供者列表
     */
    public static CompositeProvider of(List<CapabilityProvider> providers) {
        return new CompositeProvider(providers);
    }

    /**
     * 从单个提供者创建
     */
    public static CompositeProvider of(CapabilityProvider provider) {
        return new CompositeProvider(List.of(provider));
    }

    @Override
    public <T> BrickLazyOptional<T> getCapability(BrickCapability<T> cap, @Nullable Direction side) {
        String key = cap.getName() + ":" + side;

        @SuppressWarnings("unchecked")
        BrickLazyOptional<T> cached = (BrickLazyOptional<T>) cache.get(key);
        if (cached != null && cached.isPresent()) {
            return cached;
        }

        // 遍历所有提供者，找到第一个能提供此能力的
        for (CapabilityProvider provider : providers) {
            BrickLazyOptional<T> result = provider.getCapability(cap, side);
            if (result.isPresent()) {
                cache.put(key, result);
                // 当该值失效时清除缓存
                result.addListener(() -> cache.remove(key));
                return result;
            }
        }

        return BrickLazyOptional.empty();
    }

    @Override
    public Map<BrickCapability<?>, Storage<?>> getAllCapabilities() {
        Map<BrickCapability<?>, Storage<?>> all = new LinkedHashMap<>();
        for (CapabilityProvider provider : providers) {
            all.putAll(provider.getAllCapabilities());
        }
        return Collections.unmodifiableMap(all);
    }

    @Override
    public void invalidate() {
        cache.clear();
        for (CapabilityProvider provider : providers) {
            provider.invalidate();
        }
    }

    /**
     * 添加提供者（运行时动态）
     */
    public void addProvider(CapabilityProvider provider) {
        providers.add(provider);
        cache.clear();
    }

    /**
     * 移除提供者
     */
    public boolean removeProvider(CapabilityProvider provider) {
        boolean removed = providers.remove(provider);
        if (removed) {
            cache.clear();
        }
        return removed;
    }
}

