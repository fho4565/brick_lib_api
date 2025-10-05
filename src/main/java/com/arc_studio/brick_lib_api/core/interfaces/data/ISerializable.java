package com.arc_studio.brick_lib_api.core.interfaces.data;

/**
 * 可序列化和反序列化的接口
 * <p>值得注意的是，反序列化方法不能由"类名.deserialize"来调用，而需要先提供一个类的实例</p>
 * @param <T> 序列化后的对象类型
 * @author Arc Studio
 * */
public interface ISerializable<T> {
    T serialize();
    /**
     * 反序列化对象
     * @return 是否成功反序列化
     * */
    boolean deserialize(T object);
}
