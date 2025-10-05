package com.arc_studio.brick_lib_api.core.register;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class VanillaRegistry<T> extends RegistryType<T> {

    Registry<T> vanillaRegistry;

    public VanillaRegistry(Registry<T> vanillaRegistry) {
        this.vanillaRegistry = vanillaRegistry;
    }


    public Registry<T> getVanillaRegistry() {
        return vanillaRegistry;
    }

    @Override
    public ResourceKey<? extends Registry<T>> getKey() {
        return vanillaRegistry.key();
    }
}
