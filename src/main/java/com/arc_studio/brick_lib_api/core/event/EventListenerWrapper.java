package com.arc_studio.brick_lib_api.core.event;

//? if forge {
import net.minecraftforge.eventbus.api.Event;
//?}
import java.util.Objects;
import org.jetbrains.annotations.NotNull;
/**
 * 事件包装器
 *
 */
public class EventListenerWrapper<T extends BaseEvent> /*? if forge {*/ extends Event /*?}*/ implements Comparable<EventListenerWrapper<?>> {
    String name = null;
    Priority priority = Priority.NORMAL;
    EventListener<T> listener;

    public EventListenerWrapper(EventListener<T> listener) {
        this.listener = listener;
    }

    public EventListenerWrapper(String name, EventListener<T> listener) {
        this.name = name;
        this.listener = listener;
    }

    public EventListenerWrapper(String name, Priority priority, EventListener<T> listener) {
        this.name = name;
        this.priority = priority;
        this.listener = listener;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof EventListenerWrapper<?> that) {
            if(this.priority.equals(that.priority)) {
                if (this.name != null && that.name != null) {
                    return this.name.equals(that.name);
                }
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.name,this.priority);
    }

    @Override
    public int compareTo(@NotNull EventListenerWrapper<?> o) {
        return this.priority.compareTo(o.priority);
    }

    /**
     * 事件监听器的优先级
     * */
    public enum Priority {
        LOWEST(1),
        LOW(2),
        NORMAL(3),
        HIGH(4),
        HIGHEST(5);
        final int priority;

        Priority(int priority) {
            this.priority = priority;
        }

        public int compare(Priority priority) {
            return priority.priority - this.priority;
        }
        /**
         * 将整数优先级转换为Priority枚举类型
         * <ui>
         *     <li>priority小于等于1时，返回{@link Priority#LOWEST}</li>
         *     <li>priority大于等于5时，返回{@link Priority#HIGHEST}</li>
         * </ui>
         * @param priority 整数形式的优先级值
         * @return 对应的Priority枚举值
         */
        public Priority fromInteger(int priority) {
            if (priority <= 1) {
                return LOWEST;
            } else if (priority == 2) {
                return LOW;
            } else if (priority == 3) {
                return NORMAL;
            } else if (priority == 4) {
                return HIGH;
            } else {
                return HIGHEST;
            }
        }
    }
}
