package com.arc_studio.brick_lib_api.core.event;

//? if forge {
/*import net.minecraftforge.common.MinecraftForge;
*///?}

import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;

/**
 * 跨平台通用的事件总线
 * <p>事件总线会自动匹配事件类型和相应的处理器，高优先级的处理器会被优先调用。当优先级更高的处理器尝试取消事件时，其后面的事件处理器均不会被调用</p>
 */
@SuppressWarnings({"unchecked"})
public final class BrickEventBus {
    private static final HashMap<Class<?>, HashSet<EventListenerWrapper<?>>[]> SERVER_LISTENERS = new HashMap<>();
    private static final HashMap<Class<?>, HashSet<EventListenerWrapper<?>>[]> CLIENT_LISTENERS = new HashMap<>();
    private static final HashMap<Class<?>, HashSet<EventListenerWrapper<?>>[]> COMMON_LISTENERS = new HashMap<>();

    private BrickEventBus() {
    }

    /**
     * 将事件监听器注册到对应事件总线上
     *
     * @param type     事件类型
     * @param listener 事件处理器
     */
    public static <E extends BaseEvent> void registerListener(Class<E> type, EventListener<E> listener) {
        registerListener(type, null, listener);
    }

    /**
     * 将双端的事件监听器分别注册到对应事件总线上
     *
     * @param type     事件类型
     * @param clientListener 客户端的事件处理器
     * @param serverListener 服务端的事件处理器
     */
    public static <E extends BaseEvent> void registerListenerBoth(Class<E> type,
                                                                  EventListener<E> clientListener,
                                                                  EventListener<E> serverListener) {
        if (IClientOnlyEvent.class.isAssignableFrom(type)) {
            registerListenerClient(type, null, clientListener);
        } else if (IServerOnlyEvent.class.isAssignableFrom(type)) {
            registerListenerServer(type, null, serverListener);
        }
    }

    public static HashMap<Class<?>, HashSet<EventListenerWrapper<?>>[]> clientListeners() {
        return new HashMap<>(CLIENT_LISTENERS);
    }

    public static HashMap<Class<?>, HashSet<EventListenerWrapper<?>>[]> commonListeners() {
        return new HashMap<>(COMMON_LISTENERS);
    }

    public static HashMap<Class<?>, HashSet<EventListenerWrapper<?>>[]> serverListeners() {
        return new HashMap<>(SERVER_LISTENERS);
    }

    /**
     * 将带有唯一标识符的事件监听器注册到事件总线上
     *
     * @param type     事件类型
     * @param id       标识符
     * @param listener 事件处理器
     */
    public static <E extends BaseEvent> void registerListener(Class<E> type, String id, EventListener<E> listener) {
        if (IClientOnlyEvent.class.isAssignableFrom(type)) {
            registerListenerClient(type, id, listener);
        } else if (IServerOnlyEvent.class.isAssignableFrom(type)) {
            registerListenerServer(type, id, listener);
        } else {
            registerListenerCommon(type, id, listener);
        }
    }
    public static <E extends BaseEvent> void registerListener(Class<E> type, String id,EventListenerWrapper.Priority priority, EventListener<E> listener) {
        if (IClientOnlyEvent.class.isAssignableFrom(type)) {
            registerListenerClient(type, id,priority, listener);
        } else if (IServerOnlyEvent.class.isAssignableFrom(type)) {
            registerListenerServer(type, id,priority, listener);
        } else {
            registerListenerCommon(type, id,priority, listener);
        }
    }

    public static <E extends BaseEvent> void registerListenerServer(Class<E> type, EventListener<E> listener) {
        registerListenerInternal(type,null, EventListenerWrapper.Priority.NORMAL,SERVER_LISTENERS,listener);
    }
    /**
     * 将事件监听器注册到服务端事件总线上
     *
     * @param type     事件类型
     * @param listener 事件处理器
     */
    public static <E extends BaseEvent> void registerListenerServer(Class<E> type, String id, EventListener<E> listener) {
        registerListenerInternal(type,id, EventListenerWrapper.Priority.NORMAL,SERVER_LISTENERS,listener);
    }

    /**
     * 将事件监听器注册到服务端事件总线上
     *
     * @param type     事件类型
     * @param listener 事件处理器
     */
    public static <E extends BaseEvent> void registerListenerServer(Class<E> type, String id, EventListenerWrapper.Priority priority, EventListener<E> listener) {
        registerListenerInternal(type,id, priority,SERVER_LISTENERS,listener);
    }

    public static <E extends BaseEvent> void registerListenerClient(Class<E> type, EventListener<E> listener) {
        registerListenerInternal(type,null, EventListenerWrapper.Priority.NORMAL,CLIENT_LISTENERS,listener);
    }
    /**
     * 将事件监听器注册到客户端事件总线上
     *
     * @param type     事件类型
     * @param listener 事件处理器
     */
    public static <E extends BaseEvent> void registerListenerClient(Class<E> type, String id, EventListener<E> listener) {
        registerListenerInternal(type,id, EventListenerWrapper.Priority.NORMAL,CLIENT_LISTENERS,listener);
    }

    /**
     * 将事件监听器注册到客户端事件总线上
     *
     * @param type     事件类型
     * @param listener 事件处理器
     */
    public static <E extends BaseEvent> void registerListenerClient(Class<E> type, String id, EventListenerWrapper.Priority priority, EventListener<E> listener) {
        registerListenerInternal(type,id, priority,CLIENT_LISTENERS,listener);
    }

    public static <E extends BaseEvent> void registerListenerCommon(Class<E> type, EventListener<E> listener) {
        registerListenerInternal(type,null, EventListenerWrapper.Priority.NORMAL,COMMON_LISTENERS,listener);
    }
    /**
     * 将事件监听器注册到双端共有事件总线上
     *
     * @param type     事件类型
     * @param listener 事件处理器
     */
    public static <E extends BaseEvent> void registerListenerCommon(Class<E> type, String id, EventListener<E> listener) {
        registerListenerInternal(type,id, EventListenerWrapper.Priority.NORMAL,COMMON_LISTENERS,listener);
    }

    /**
     * 将事件监听器注册到双端共有事件总线上
     *
     * @param type     事件类型
     * @param listener 事件处理器
     */
    public static <E extends BaseEvent> void registerListenerCommon(Class<E> type, String id, EventListenerWrapper.Priority priority, EventListener<E> listener) {
        registerListenerInternal(type,id, priority,COMMON_LISTENERS,listener);
    }

    private static <E extends BaseEvent> void registerListenerInternal(Class<E> type, String id, EventListenerWrapper.Priority priority, HashMap<Class<?>, HashSet<EventListenerWrapper<?>>[]> map, EventListener<E> listener) {
        map.compute(type, (k, priorityTiers) -> {
            if (priorityTiers == null) {
                priorityTiers = new HashSet[]{
                    new HashSet<>(),
                    new HashSet<>(),
                    new HashSet<>(),
                    new HashSet<>(),
                    new HashSet<>()
                };
            }
            priorityTiers[priority.priority - 1].add(new EventListenerWrapper<>(id,priority,listener));
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
        boolean cancelled = false;
        for (Class<?> extendClass : collectExtendClassesServer(event.getClass())) {
            HashSet<EventListenerWrapper<?>>[] listeners = SERVER_LISTENERS.get(extendClass);
            if (listeners != null) {
                if (processEvent(event, listeners)) {
                    cancelled = true;
                    break;
                }
            }
        }
        return cancelled;
    }

    /**
     * 在客户端事件总线上发送一个事件
     *
     * @param event 一个事件
     * @return true : 如果有事件被取消
     */
    public static <E extends BaseEvent> boolean postEventClient(E event) {
        postEventCommon(event);
        boolean cancelled = false;
        for (Class<?> extendClass : collectExtendClassesClient(event.getClass())) {
            HashSet<EventListenerWrapper<?>>[] listeners = CLIENT_LISTENERS.get(extendClass);
            if (listeners != null) {
                if (processEvent(event, listeners)) {
                    cancelled = true;
                    break;
                }
            }
        }
        return cancelled;
    }

    /**
     * 在双端共有事件总线上发送一个事件
     *
     * @param event 一个事件
     * @return true : 如果有事件被取消
     */
    public static <E extends BaseEvent> boolean postEventCommon(E event) {
        boolean cancelled = false;
        for (Class<?> extendClass : collectExtendClassesCommon(event.getClass())) {
            HashSet<EventListenerWrapper<?>>[] listeners = COMMON_LISTENERS.get(extendClass);
            if (listeners != null) {
                if (processEvent(event, listeners)) {
                    cancelled = true;
                    break;
                }
            }
        }
        return cancelled;
    }

    @SuppressWarnings("unchecked")
    private static <E extends BaseEvent> boolean processEvent(E event, HashSet<EventListenerWrapper<?>>[] tiers) {
        for (int i = tiers.length - 1; i >= 0; i--) {
            for (EventListenerWrapper<?> wrapper : tiers[i]) {
                try {
                    ((EventListenerWrapper<E>) wrapper).listener.handle(event);
                    if(event instanceof IOneTimeEvent){
                        SERVER_LISTENERS.remove(event.getClass());
                        CLIENT_LISTENERS.remove(event.getClass());
                        COMMON_LISTENERS.remove(event.getClass());
                    }
                } catch (ClassCastException ignored) {
                }
                if ((event instanceof ICancelableEvent) && event.isCanceled()) {
                    return true;
                }
                //? if forge {
                /*else {
                    return MinecraftForge.EVENT_BUS.post(wrapper);
                }
                *///?}
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
