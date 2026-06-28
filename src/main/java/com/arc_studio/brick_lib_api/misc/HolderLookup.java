package com.arc_studio.brick_lib_api.misc;

import com.arc_studio.brick_lib_api.core.data.BrickResourceKey;
import com.mojang.serialization.Lifecycle;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings({"deprecation", "removal", "unchecked"})
public interface HolderLookup<T> {
    Stream<Holder.Reference<T>> listElements();

    default Stream<BrickResourceKey<T>> listElementIds() {
        return this.listElements().map(tReference -> (BrickResourceKey<T>) tReference.key());
    }

    Stream<HolderSet.Named<T>> listTags();

    default Stream<TagKey<T>> listTagIds() {
        return this.listTags().map(HolderSet.Named::key);
    }

    interface RegistryLookup<T> extends HolderLookup<T> {
        BrickResourceKey<? extends Registry<? extends T>> key();

        Lifecycle registryLifecycle();
    }

    interface Provider  {
        Stream<BrickResourceKey<? extends Registry<?>>> listRegistryKeys();

        default Stream<RegistryLookup<?>> listRegistries() {
            return this.listRegistryKeys().map(this::lookupOrThrow);
        }

        <T> Optional<? extends RegistryLookup<T>> lookup(BrickResourceKey<? extends Registry<? extends T>> registryKey);

        default <T> RegistryLookup<T> lookupOrThrow(BrickResourceKey<? extends Registry<? extends T>> registryKey) {
            return this.lookup(registryKey).orElseThrow(() -> new IllegalStateException("Registry " + registryKey.location() + " not found"));
        }

        static Provider create(Stream<RegistryLookup<?>> lookupStream) {
            final Map<BrickResourceKey<? extends Registry<?>>, RegistryLookup<?>> map = lookupStream.collect(Collectors.toUnmodifiableMap(RegistryLookup::key, (registryLookup) -> registryLookup));
            return new Provider() {
                public Stream<BrickResourceKey<? extends Registry<?>>> listRegistryKeys() {
                    return map.keySet().stream();
                }

                public <T> Optional<RegistryLookup<T>> lookup(BrickResourceKey<? extends Registry<? extends T>> registryKey) {
                    return Optional.ofNullable((RegistryLookup)map.get(registryKey));
                }
            };
        }

        default Lifecycle allRegistriesLifecycle() {
            return this.listRegistries().map(RegistryLookup::registryLifecycle).reduce(Lifecycle.stable(), Lifecycle::add);
        }
    }
}
