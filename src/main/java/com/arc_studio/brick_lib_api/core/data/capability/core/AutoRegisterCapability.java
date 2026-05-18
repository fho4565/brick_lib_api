package com.arc_studio.brick_lib_api.core.data.capability.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标注在能力接口上，表示该能力应被自动注册到 {@link CapabilityManager}。
 * <p>
 * 使用示例：
 * <pre>{@code
 * @AutoRegisterCapability
 * public interface IEnergyStorage {
 *     // ...
 * }
 * }</pre>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AutoRegisterCapability {
}

