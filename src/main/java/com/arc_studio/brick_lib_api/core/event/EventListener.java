package com.arc_studio.brick_lib_api.core.event;

/**
 * 事件监听器在对应事件触发时调用
 * */
@FunctionalInterface
public interface EventListener<T extends BaseEvent> {
    /**
     * 事件处理方法
     * */
    void handle(T event);
}
