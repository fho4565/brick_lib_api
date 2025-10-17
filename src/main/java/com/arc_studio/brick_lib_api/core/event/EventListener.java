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

    /**
     * 事件监听器的优先级
     * */
    default Priority getPriority() {
        return Priority.NORMAL;
    }

    /**
     * 包装事件监听器并加入优先级
     * */
    static <E extends BaseEvent> EventListener<E> create(Priority priority, EventListener<E> listener) {
        return new EventListener<>() {
            @Override
            public void handle(E event) {
                listener.handle(event);
            }

            @Override
            public Priority getPriority() {
                return priority;
            }
        };
    }

    /**
     * 事件监听器的优先级
     * */
    enum Priority {
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
