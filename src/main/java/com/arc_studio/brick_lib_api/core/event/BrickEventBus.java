package com.arc_studio.brick_lib_api.core.event;

import net.minecraft.resources.ResourceLocation;

import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;

/**
 * 跨平台通用的事件总线
 * <p>事件总线会自动匹配事件类型和相应的处理器，高优先级的处理器会被优先调用。当优先级更高的处理器尝试取消事件时，其后面的事件处理器均不会被调用</p>
 */
public final class BrickEventBus {
    private static final HashMap<Class<?>, HashSet<EventWrapper<?>>[]> SERVER_LISTENERS = new HashMap<>();
    private static final HashMap<Class<?>, HashSet<EventWrapper<?>>[]> CLIENT_LISTENERS = new HashMap<>();
    private static final HashMap<Class<?>, HashSet<EventWrapper<?>>[]> COMMON_LISTENERS = new HashMap<>();

    private BrickEventBus() {
    }

    /**
     * 将事件监听器注册到对应事件总线上
     *
     * @param type     事件类型
     * @param listener 事件处理器
     */
    public static <E extends BaseEvent> void registerListener(Class<E> type, EventListener<E> listener) {
        if (IClientOnlyEvent.class.isAssignableFrom(type)) {
            registerListenerClient(type, listener);
        } else if (IServerOnlyEvent.class.isAssignableFrom(type)) {
            registerListenerServer(type, listener);
        } else {
            registerListenerCommon(type, listener);
        }
    }

    /**
     * 将带有唯一标识符的事件监听器注册到事件总线上
     *
     * @param type     事件类型
     * @param listener 事件处理器
     * @param id       标识符
     */
    public static <E extends BaseEvent> void registerListener(Class<E> type, EventListener<E> listener, ResourceLocation id) {
        if (IClientOnlyEvent.class.isAssignableFrom(type)) {
            CLIENT_LISTENERS.compute(type, (k, priorityTiers) -> {
                if (priorityTiers == null) {
                    priorityTiers = new HashSet[]{
                            new HashSet<>(),
                            new HashSet<>(),
                            new HashSet<>(),
                            new HashSet<>(),
                            new HashSet<>()
                    };
                }
                priorityTiers[listener.getPriority().priority - 1].add(new EventWrapper<>(id, listener));
                return priorityTiers;
            });
        } else if (IServerOnlyEvent.class.isAssignableFrom(type)) {
            SERVER_LISTENERS.compute(type, (k, priorityTiers) -> {
                if (priorityTiers == null) {
                    priorityTiers = new HashSet[]{
                            new HashSet<>(),
                            new HashSet<>(),
                            new HashSet<>(),
                            new HashSet<>(),
                            new HashSet<>()
                    };
                }
                priorityTiers[listener.getPriority().priority - 1].add(new EventWrapper<>(id, listener));
                return priorityTiers;
            });
        } else {
            COMMON_LISTENERS.compute(type, (k, priorityTiers) -> {
                if (priorityTiers == null) {
                    priorityTiers = new HashSet[]{
                            new HashSet<>(),
                            new HashSet<>(),
                            new HashSet<>(),
                            new HashSet<>(),
                            new HashSet<>()
                    };
                }
                priorityTiers[listener.getPriority().priority - 1].add(new EventWrapper<>(id, listener));
                return priorityTiers;
            });
        }
    }

    /**
     * 将事件监听器注册到服务端事件总线上
     *
     * @param type     事件类型
     * @param listener 事件处理器
     */
    public static <E extends BaseEvent> void registerListenerServer(Class<E> type, EventListener<E> listener) {
        SERVER_LISTENERS.compute(type, (k, priorityTiers) -> {
            if (priorityTiers == null) {
                priorityTiers = new HashSet[]{
                        new HashSet<>(),
                        new HashSet<>(),
                        new HashSet<>(),
                        new HashSet<>(),
                        new HashSet<>()
                };
            }
            priorityTiers[listener.getPriority().priority - 1].add(new EventWrapper<>(null, listener));
            return priorityTiers;
        });
    }

    /**
     * 将事件监听器注册到客户端事件总线上
     *
     * @param type     事件类型
     * @param listener 事件处理器
     */
    public static <E extends BaseEvent> void registerListenerClient(Class<E> type, EventListener<E> listener) {
        CLIENT_LISTENERS.compute(type, (k, priorityTiers) -> {
            if (priorityTiers == null) {
                priorityTiers = new HashSet[]{
                        new HashSet<>(),
                        new HashSet<>(),
                        new HashSet<>(),
                        new HashSet<>(),
                        new HashSet<>()
                };
            }
            priorityTiers[listener.getPriority().priority - 1].add(new EventWrapper<>(null, listener));
            return priorityTiers;
        });
    }

    /**
     * 将事件监听器注册到双端共有事件总线上
     *
     * @param type     事件类型
     * @param listener 事件处理器
     */
    public static <E extends BaseEvent> void registerListenerCommon(Class<E> type, EventListener<E> listener) {
        COMMON_LISTENERS.compute(type, (k, priorityTiers) -> {
            if (priorityTiers == null) {
                priorityTiers = new HashSet[]{
                        new HashSet<>(),
                        new HashSet<>(),
                        new HashSet<>(),
                        new HashSet<>(),
                        new HashSet<>()
                };
            }
            priorityTiers[listener.getPriority().priority - 1].add(new EventWrapper<>(null, listener));
            return priorityTiers;
        });
    }


    /**
     * 在服务端事件总线上发送一个事件
     *
     * @param event 一个事件
     * @return true : 如果有事件被取消
     */
    public static <E extends BaseEvent> boolean postEventServer(E event) {
        postEventCommon(event);
        for (Class<?> extendClass : collectExtendClassesServer(event.getClass())) {
            return processEvent(event, SERVER_LISTENERS.get(extendClass));
        }
        return false;
    }

    /**
     * 在客户端事件总线上发送一个事件
     *
     * @param event 一个事件
     * @return true : 如果有事件被取消
     */
    public static <E extends BaseEvent> boolean postEventClient(E event) {
        postEventCommon(event);
        for (Class<?> extendClass : collectExtendClassesClient(event.getClass())) {
            return processEvent(event, CLIENT_LISTENERS.get(extendClass));
        }
        return false;
    }

    /**
     * 在双端共有事件总线上发送一个事件
     *
     * @param event 一个事件
     * @return true : 如果有事件被取消
     */
    public static <E extends BaseEvent> boolean postEventCommon(E event) {
        for (Class<?> extendClass : collectExtendClassesCommon(event.getClass())) {
            return processEvent(event, COMMON_LISTENERS.get(extendClass));
        }
        return false;
    }

    @SuppressWarnings("unchecked")
    private static <E extends BaseEvent> boolean processEvent(E event, HashSet<EventWrapper<?>>[] tiers) {
        for (int i = tiers.length - 1; i >= 0; i--) {
            for (EventWrapper<?> wrapper : tiers[i]) {
                try {
                    ((EventWrapper<E>) wrapper).listener.handle(event);
                } catch (ClassCastException ignored) {
                }
                if ((ICancelableEvent.class.isAssignableFrom(event.getClass())) && event.isCanceled()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 在事件总线上发送一个事件。如果同时有特定端和双端共有的事件处理器，则会返回特定端的结果，如果没有则返回双端共有的结果
     *
     * @param event 一个事件
     * @return true : 如果有事件被取消，即原版处理不应进行
     */
    public static <E extends BaseEvent> boolean postEvent(E event) {
        if (IClientOnlyEvent.class.isAssignableFrom(event.getClass())) {
            return postEventClient(event);
        } else if (IServerOnlyEvent.class.isAssignableFrom(event.getClass())) {
            return postEventServer(event);
        }else{
            return postEventCommon(event);
        }
    }

    private static Set<Class<?>> collectExtendClassesServer(Class<?> type) {
        HashSet<Class<?>> set = new HashSet<>();
        SERVER_LISTENERS.keySet().forEach(aClass -> {
            if (aClass.isAssignableFrom(type)) {
                set.add(aClass);
            }
        });
        return set;
    }

    private static Set<Class<?>> collectExtendClassesClient(Class<?> type) {
        HashSet<Class<?>> set = new HashSet<>();
        CLIENT_LISTENERS.keySet().forEach(aClass -> {
            if (aClass.isAssignableFrom(type)) {
                set.add(aClass);
            }
        });
        return set;
    }

    private static Set<Class<?>> collectExtendClassesCommon(Class<?> type) {
        HashSet<Class<?>> set = new HashSet<>();
        COMMON_LISTENERS.keySet().forEach(aClass -> {
            if (aClass.isAssignableFrom(type)) {
                set.add(aClass);
            }
        });
        return set;
    }
}