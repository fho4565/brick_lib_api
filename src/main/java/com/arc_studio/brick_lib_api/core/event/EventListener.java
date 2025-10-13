package com.arc_studio.brick_lib_api.core.event;

@FunctionalInterface
public interface EventListener<T extends BaseEvent> {
    /**
     * 事件处理方法
     * */
    void handle(T event);

    default Priority getPriority() {
        return Priority.NORMAL;
    }

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

        public Priority fromInteger(int priority) {
            return switch (priority) {
                case 1 -> LOWEST;
                case 2 -> LOW;
                case 4 -> HIGH;
                case 5 -> HIGHEST;
                default -> NORMAL;
            };
        }
    }
}
