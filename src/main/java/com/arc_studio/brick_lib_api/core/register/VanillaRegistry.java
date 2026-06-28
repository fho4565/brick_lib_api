package com.arc_studio.brick_lib_api.core.register;

import com.arc_studio.brick_lib_api.core.data.BrickResourceKey;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;

public final class VanillaRegistry<T> extends RegistryType<T> {

    Registry<T> vanillaRegistry;

    public VanillaRegistry(Registry<T> vanillaRegistry) {
        this.vanillaRegistry = vanillaRegistry;
    }


    public Registry<T> getVanillaRegistry() {
        return vanillaRegistry;
    }

    @Override
    public BrickResourceKey<? extends Registry<T>> getRegisterKey() {
        return (BrickResourceKey<? extends Registry<T>>) vanillaRegistry.key();
    }
}
