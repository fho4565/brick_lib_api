package com.arc_studio.brick_lib_api.core.data;

import net.minecraft.core.Registry;
//? if > 1.19.2 {
import net.minecraft.core.registries.Registries;
//? }
import net.minecraft.resources.ResourceKey;

public class BrickResourceKey<T> extends ResourceKey<T> {
    public BrickResourceKey(ResourceID registryName, ResourceID location) {
        super(registryName,location);
    }

    public ResourceID key() {
        return ResourceID.of(this.registry().getNamespace(), this.registry().getPath());
    }

    public ResourceID location(){
        //? if >= 1.21.8 {
        /*return ResourceID.of(this.identifier().getNamespace(), this.identifier().getPath());*/
        //? } else {
        return ResourceID.of(super.location().getNamespace(),super.location().getPath());
        //? }
    }

    public static <T> BrickResourceKey<T> create(BrickResourceKey<? extends Registry<T>> registryKey, ResourceID location) {
        return (BrickResourceKey<T>) ResourceKey.create(registryKey, location);
    }

    public static <T> BrickResourceKey<T> createRegistryKey(ResourceID location) {
        return (BrickResourceKey<T>) ResourceKey.createRegistryKey(location);
    }

}
