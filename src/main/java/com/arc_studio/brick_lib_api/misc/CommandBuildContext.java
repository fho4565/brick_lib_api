package com.arc_studio.brick_lib_api.misc;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;

import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;

public interface CommandBuildContext {
    static CommandBuildContext simple(HolderLookup.Provider provider) {
        return new CommandBuildContext() {
            public Stream<ResourceKey<? extends Registry<?>>> listRegistries() {
                return provider.listRegistries().map(HolderLookup.RegistryLookup::key);
            }

            public <T> Optional<HolderLookup.RegistryLookup<T>> lookup(ResourceKey<? extends Registry<? extends T>> registryKey) {
                return provider.lookup(registryKey).map(registryLookup -> registryLookup);
            }
        };
    }
}
