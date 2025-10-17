package com.arc_studio.brick_lib_api.core.register;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

abstract class RegistryType<T> {
    public abstract ResourceKey<? extends Registry<T>> getRegisterKey();
}
