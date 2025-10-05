package com.arc_studio.brick_lib_api.core.container;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

/**
 * <font color="red">Neoforge Lazy类的移植</font>
 * <p>Lazy类的对象在第一次访问时初始化，而不是在类加载时就初始化</p>
 */
public final class Lazy<T> implements Supplier<T> {

    private final Supplier<T> delegate;
    @Nullable
    private volatile T cache;

    private Lazy(Supplier<T> delegate) {
        this.delegate = delegate;
    }

    /**
     * 创建一个延迟初始化的对象。
     *
     * @param supplier 值的委托，在第一次需要值时或缓存失效时调用。
     */
    public static <T> Lazy<T> of(Supplier<T> supplier) {
        return new Lazy<>(supplier);
    }

    /**
     * 清除缓存，在下次访问时再次调用委托。
     */
    public synchronized void clear() {
        this.cache = null;
    }

    /**
     * 获取缓存的值，如果缓存未初始化则通过委托获取并初始化缓存
     *
     * @return 缓存的值，该值不会为null
     * @throws IllegalStateException 当委托返回null时抛出
     */
    @Override
    public T get() {
        T value = cache;
        if (value == null) {
            synchronized (this) {
                value = cache;
                if (value == null) {
                    value = delegate.get();
                    if (value == null) {
                        throw new IllegalStateException("Delegate returned null: " + delegate);
                    }
                    cache = value;
                }
            }
        }
        return value;
    }
}