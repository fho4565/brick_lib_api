package com.arc_studio.brick_lib_api.core.data.capability.event;

import com.arc_studio.brick_lib_api.core.data.capability.provider.CapabilityProvider;
import com.arc_studio.brick_lib_api.core.event.BaseEvent;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 能力附加事件 — 类似 Forge AttachCapabilitiesEvent
 * <p>
 * 允许通过事件系统为任意对象动态附加能力，实现解耦扩展。
 * </p>
 *
 * @param <T> 目标对象类型
 */
public class AttachCapabilitiesEvent<T> extends BaseEvent {

    private final T object;
    private final Map<ResourceLocation, CapabilityProvider> attached = new LinkedHashMap<>();
    private final Map<ResourceLocation, CompoundTag> serializedData = new HashMap<>();

    public AttachCapabilitiesEvent(T object) {
        this.object = object;
    }

    /**
     * 获取目标对象
     */
    public T getObject() {
        return object;
    }

    /**
     * 添加一个能力提供者，使用唯一标识符
     *
     * @param id       唯一标识符
     * @param provider 能力提供者
     */
    public void addProvider(ResourceLocation id, CapabilityProvider provider) {
        if (attached.containsKey(id)) {
            throw new IllegalArgumentException("Duplicate capability provider id: " + id);
        }
        attached.put(id, provider);
    }

    /**
     * 添加一个可序列化的能力提供者
     *
     * @param id       唯一标识符
     * @param provider 能力提供者
     * @param data     序列化数据
     */
    public void addSerializable(ResourceLocation id, CapabilityProvider provider, CompoundTag data) {
        addProvider(id, provider);
        serializedData.put(id, data);
    }

    /**
     * 获取已附加的提供者（只读）
     */
    public Map<ResourceLocation, CapabilityProvider> getAttached() {
        return Collections.unmodifiableMap(attached);
    }

    /**
     * 获取反序列化时需要恢复的数据
     */
    public Map<ResourceLocation, CompoundTag> getSerializedData() {
        return Collections.unmodifiableMap(serializedData);
    }
}

