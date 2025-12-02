package com.arc_studio.brick_lib_api.core.register;

import com.arc_studio.brick_lib_api.platform.Platform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaceHolderRegistry<T> extends BrickRegistry<T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceHolderRegistry.class);
    public PlaceHolderRegistry(ResourceKey<? extends Registry<T>> key) {
        super(key);
    }

    @Override
    public T register(ResourceKey<T> key, T value) {
        LOGGER.warn("Registry {} is either obsolete or for future use; registering {} in {} does nothing in version {}!", key.registry(),value, key.registry(), Platform.gameVersionStr());
        return value;
    }
}
