package com.arc_studio.brick_lib_api.core.event;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * 事件包装器
 * */
public class EventWrapper<T extends BaseEvent> {
    EventListener<T> listener;
    private final @Nullable ResourceLocation name;

    public EventWrapper(@Nullable ResourceLocation name, EventListener<T> listener) {
        this.listener = listener;
        this.name = name;
    }

    @Nullable
    public ResourceLocation name() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof EventWrapper<?> that) {
            if (this.name != null && that.name != null) {
                return Objects.equals(name, that.name);
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name);
    }
}
