package com.arc_studio.brick_lib_api.core.data;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 惰性值 — 类似 Forge BrickLazyOptional，但增加事务感知
 * <p>
 * 支持惰性求值、失效通知和类型安全转换。
 * </p>
 *
 * @param <T> 值类型
 */
public final class BrickLazyOptional<T> {

    private static final BrickLazyOptional<?> EMPTY = new BrickLazyOptional<>(null);

    @Nullable
    private final Supplier<T> supplier;
    @Nullable
    private T value;
    private boolean resolved = false;
    private boolean valid = true;
    private final List<Runnable> invalidateListeners = new ArrayList<>();

    private BrickLazyOptional(@Nullable Supplier<T> supplier) {
        this.supplier = supplier;
    }

    /**
     * 创建已解析的实例
     */
    public static <T> BrickLazyOptional<T> of(T instance) {
        Objects.requireNonNull(instance);
        BrickLazyOptional<T> opt = new BrickLazyOptional<>(null);
        opt.value = instance;
        opt.resolved = true;
        return opt;
    }

    /**
     * 创建惰性实例，提供者工厂在首次访问时调用
     */
    public static <T> BrickLazyOptional<T> of(Supplier<T> supplier) {
        Objects.requireNonNull(supplier);
        return new BrickLazyOptional<>(supplier);
    }

    /**
     * 创建空实例
     */
    @SuppressWarnings("unchecked")
    public static <T> BrickLazyOptional<T> empty() {
        return (BrickLazyOptional<T>) EMPTY;
    }

    /**
     * 检查是否存在有效值
     */
    public boolean isPresent() {
        return valid && (resolved ? value != null : supplier != null);
    }

    /**
     * 如果存在则消费
     */
    public void ifPresent(Consumer<T> consumer) {
        T val = resolve();
        if (val != null && valid) {
            consumer.accept(val);
        }
    }

    /**
     * 获取值（不存在则抛异常）
     */
    public T orElseThrow() {
        T val = resolve();
        if (val == null || !valid) {
            throw new IllegalStateException("BrickLazyOptional is empty or invalidated.");
        }
        return val;
    }

    /**
     * 获取值或返回默认值
     */
    public T orElse(T defaultValue) {
        T val = resolve();
        return (val != null && valid) ? val : defaultValue;
    }

    /**
     * 转换类型（无检查 cast）
     */
    @SuppressWarnings("unchecked")
    public <R> BrickLazyOptional<R> cast() {
        return (BrickLazyOptional<R>) this;
    }

    /**
     * 注册失效监听器
     */
    public void addListener(Runnable onInvalidate) {
        if (!valid) {
            onInvalidate.run();
        } else {
            invalidateListeners.add(onInvalidate);
        }
    }

    /**
     * 使当前实例失效
     */
    public void invalidate() {
        if (!valid) return;
        valid = false;
        for (Runnable listener : invalidateListeners) {
            listener.run();
        }
        invalidateListeners.clear();
    }

    @Nullable
    private T resolve() {
        if (!valid) return null;
        if (!resolved) {
            resolved = true;
            if (supplier != null) {
                value = supplier.get();
            }
        }
        return value;
    }
}

