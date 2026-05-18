package com.arc_studio.brick_lib_api.core.data.capability.event;

import com.arc_studio.brick_lib_api.core.data.capability.core.Capability;
import com.arc_studio.brick_lib_api.core.data.capability.core.CapabilityManager;
import com.arc_studio.brick_lib_api.core.data.capability.core.CapabilityToken;
import com.arc_studio.brick_lib_api.core.event.BaseEvent;
import com.arc_studio.brick_lib_api.core.event.IOneTimeEvent;

/**
 * 能力注册事件 — 在模组加载期间触发，用于注册所有能力
 */
public class RegisterCapabilitiesEvent extends BaseEvent implements IOneTimeEvent {

    /**
     * 注册一个能力，关联其默认行为
     *
     * @param token       类型令牌
     * @param initializer 初始化器，用于创建默认 Capability 实例
     */
    public <T> void register(CapabilityToken<T> token, CapabilityInitializer<T> initializer) {
        String name = token.getInternalName();
        Class<T> type = token.getType();
        Capability<T> capability = initializer.initialize(name, type);
        CapabilityManager.register(capability);
    }

    /**
     * 便捷方法：使用默认初始化器注册
     */
    public <T> Capability<T> register(CapabilityToken<T> token) {
        return CapabilityManager.get(token);
    }

    /**
     * 能力初始化器函数接口
     */
    @FunctionalInterface
    public interface CapabilityInitializer<T> {
        /**
         * 创建能力的默认实现
         */
        Capability<T> initialize(String name, Class<T> type);
    }
}

