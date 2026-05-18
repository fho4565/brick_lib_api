package com.arc_studio.brick_lib_api.core.data.capability.core;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * 类型安全的 Token — 使用匿名子类捕获泛型类型信息
 * <p>
 * 通过创建匿名子类，在运行时保留泛型类型参数：
 * <pre>{@code
 * new CapabilityToken<IEnergyStorage>(){}
 * }</pre>
 * </p>
 *
 * @param <T> 能力接口类型
 */
public abstract class CapabilityToken<T> {

    private final Class<T> type;
    private final String internalName;

    @SuppressWarnings("unchecked")
    protected CapabilityToken() {
        Type superClass = getClass().getGenericSuperclass();
        if (superClass instanceof ParameterizedType parameterized) {
            Type typeArg = parameterized.getActualTypeArguments()[0];
            if (typeArg instanceof Class<?>) {
                this.type = (Class<T>) typeArg;
            } else if (typeArg instanceof ParameterizedType pt) {
                this.type = (Class<T>) pt.getRawType();
            } else {
                throw new IllegalArgumentException("Unsupported type argument: " + typeArg);
            }
        } else {
            throw new IllegalArgumentException(
                "CapabilityToken must be created as an anonymous subclass: new CapabilityToken<MyType>(){}"
            );
        }
        this.internalName = this.type.getName();
    }

    /**
     * 获取内部类型名称
     */
    public String getInternalName() {
        return internalName;
    }

    /**
     * 获取 Class 对象
     */
    public Class<T> getType() {
        return type;
    }
}

