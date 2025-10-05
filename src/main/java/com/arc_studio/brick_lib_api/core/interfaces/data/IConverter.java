package com.arc_studio.brick_lib_api.core.interfaces.data;

/**
 * 转换器接口，定义了对象序列化和反序列化的标准
 *
 * <p>此接口提供了一种通用的方式，可以让{@link I}的对象和{@link O}的对象互相转换</p>
 *
 * @param <I> 被序列化的对象类型
 * @param <O> 序列化后的对象类型
 */
public interface IConverter<I, O> {

    /**
     * 将{@link O}的对象反序列化为{@link I}的对象。
     *
     * @param object 需要被反序列化的对象，类型为 O。
     * @return 反序列化后的对象，类型为 I。
     */
    I from(O object);

    /**
     * 将{@link I}的对象序列化为{@link O}的对象。
     *
     * @param object 需要被序列化的对象，类型为 I。
     * @return 序列化后的对象，类型为 O。
     */
    O to(I object);
}

