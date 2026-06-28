package com.arc_studio.brick_lib_api.core.data;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;

public class BrickResourceKey<T> extends ResourceKey<T> {
    public BrickResourceKey(ResourceID registryName, ResourceID location) {
        super(registryName,location);
    }

    public ResourceID key() {
        return ResourceID.of(this.registry().getNamespace(), this.registry().getPath());
    }

    public ResourceID location(){
        return ResourceID.of(this.identifier().getNamespace(), this.identifier().getPath());
    }

    public static <T> BrickResourceKey<T> create(BrickResourceKey<? extends Registry<T>> registryKey, ResourceID location) {
        return (BrickResourceKey<T>) create(registryKey.location(), location);
    }

    public static <T> BrickResourceKey<T> createRegistryKey(ResourceID location) {
        return (BrickResourceKey<T>) create(Registries.ROOT_REGISTRY_NAME, location);
    }

}
