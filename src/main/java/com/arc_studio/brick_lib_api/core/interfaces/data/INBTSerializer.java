package com.arc_studio.brick_lib_api.core.interfaces.data;

import net.minecraft.nbt.Tag;

/**
 * 序列化结果为{@link Tag}的序列化器
 * @param <T> 序列化后的对象类型，必须是{@link Tag}的子类
 * @author arc_studio
 */
public interface INBTSerializer<T extends Tag> extends ISerializable<T>{

}
