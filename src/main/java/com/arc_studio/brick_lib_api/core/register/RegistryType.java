package com.arc_studio.brick_lib_api.core.register;

import com.arc_studio.brick_lib_api.core.data.BrickResourceKey;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

abstract class RegistryType<T> {
    public abstract BrickResourceKey<? extends Registry<T>> getRegisterKey();
}
