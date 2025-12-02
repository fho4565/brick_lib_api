package com.arc_studio.brick_lib_api.core.register;

import com.arc_studio.brick_lib_api.BrickLibAPI;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * Brick Lib注册管理器，提供统一的注册接口
 *
 * @author fho4565
 */
public class BrickRegisterManager {
    private static final Map<Registry<?>, VanillaRegistryData<?>> VANILLA_REGISTRY_DATA = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LoggerFactory.getLogger(BrickRegisterManager.class);

    private BrickRegisterManager() {
        throw new AssertionError("Instantiation of tool classes is not allowed");
    }

    /**
     * 原版注册表数据容器
     */
    private static class VanillaRegistryData<T> {
        private final Registry<T> registry;
        private final Map<ResourceLocation, Supplier<T>> entries;

        public VanillaRegistryData(Registry<T> registry) {
            this.registry = Objects.requireNonNull(registry, "registry cannot be null");
            this.entries = new LinkedHashMap<>();
        }

        public boolean register(ResourceLocation id, Supplier<T> supplier) {
            Objects.requireNonNull(id, "id cannot be null");
            Objects.requireNonNull(supplier, "supplier cannot be null");

            return entries.putIfAbsent(id, supplier) == null;
        }

        public Registry<T> getRegistry() {
            return registry;
        }

        public Map<ResourceLocation, Supplier<T>> getEntries() {
            return Collections.unmodifiableMap(entries);
        }
    }

    /**
     * 注册到原版注册表中
     *
     * @param registry 原版注册表
     * @param id 资源标识符
     * @param supplier 注册对象提供者
     * @return 注册成功返回true，如果已存在返回false
     */
    public static <T> boolean register(Registry<T> registry, ResourceLocation id, Supplier<T> supplier) {
        Objects.requireNonNull(registry, "registry cannot be null");
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(supplier, "supplier cannot be null");

        @SuppressWarnings("unchecked")
        VanillaRegistryData<T> registryData = (VanillaRegistryData<T>) VANILLA_REGISTRY_DATA
                .computeIfAbsent(registry, k -> new VanillaRegistryData<>(registry));

        return registryData.register(id, supplier);
    }

    /**
     * 注册到原版注册表中（使用VanillaRegistry包装器）
     */
    public static <T> boolean register(VanillaRegistry<T> key, ResourceLocation id, Supplier<T> value) {
        return register(key.getVanillaRegistry(), id, value);
    }

    /**
     * 注册到自定义注册表中
     *
     * @param registry 自定义注册表
     * @param id 资源标识符
     * @param supplier 注册对象提供者
     * @return 注册成功返回true，如果已存在返回false
     */
    public static <T> boolean register(BrickRegistry<T> registry, ResourceLocation id, Supplier<T> supplier) {
        Objects.requireNonNull(registry, "registry cannot be null");
        Objects.requireNonNull(id, "id cannot be null");
        Objects.requireNonNull(supplier, "supplier cannot be null");

        if (registry.get(id) != null) {
            LOGGER.error("Duplicated key {} in registry {}",id,registry.getRegisterKey());
            return false;
        }

        registry.register(id, supplier.get());
        return true;
    }

    /**
     * 使用自动生成的ID注册到自定义注册表
     */
    public static <T> boolean register(BrickRegistry<T> registry, Supplier<T> supplier) {
        Objects.requireNonNull(registry, "registry cannot be null");
        Objects.requireNonNull(supplier, "supplier cannot be null");

        ResourceLocation autoId = generateAutoId(registry);
        return register(registry, autoId, supplier);
    }


    private static ResourceLocation generateAutoId(BrickRegistry<?> registry) {
        //? if > 1.18.2 {
        String key = registry.getRegisterKey().location().toLanguageKey();
        //?} else {
        /*ResourceLocation location = registry.getRegisterKey().location();
        String key = location.getNamespace() + "." + location.getPath();
        *///?}
        int count = registry.count();
        return BrickLibAPI.ofPath(key + count);
    }

    /**
     * 获取所有原版注册表条目（只读视图）
     */
    public static Map<Registry<?>, Map<ResourceLocation, Supplier<?>>> getVanillaEntries() {
        Map<Registry<?>, Map<ResourceLocation, Supplier<?>>> result = new HashMap<>();

        VANILLA_REGISTRY_DATA.forEach((registry, data) -> {
            result.put(registry, Collections.unmodifiableMap(data.getEntries()));
        });

        return Collections.unmodifiableMap(result);
    }

    /**
     * 获取特定原版注册表的条目
     */
    public static <T> Optional<Map<ResourceLocation, Supplier<T>>> getVanillaEntries(Registry<T> registry) {
        @SuppressWarnings("unchecked")
        VanillaRegistryData<T> data = (VanillaRegistryData<T>) VANILLA_REGISTRY_DATA.get(registry);

        if (data != null) {
            return Optional.of(data.getEntries());
        }

        return Optional.empty();
    }

    /**
     * 清空所有注册数据（主要用于测试）
     */
    public static void clear() {
        VANILLA_REGISTRY_DATA.clear();
    }
}
