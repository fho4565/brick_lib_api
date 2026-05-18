package com.arc_studio.brick_lib_api.core.data.capability.provider;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

/**
 * Provider 工厂函数接口
 *
 * @param <O> 目标对象类型
 */
@FunctionalInterface
public interface CapabilityProviderFactory<O> {
    /**
     * 根据目标对象创建 CapabilityProvider
     *
     * @param target       目标对象（如 BlockEntity、Entity 等）
     * @param existingData 反序列化时的已有 NBT 数据，可为 null
     * @return 能力提供者
     */
    CapabilityProvider create(O target, @Nullable CompoundTag existingData);
}

