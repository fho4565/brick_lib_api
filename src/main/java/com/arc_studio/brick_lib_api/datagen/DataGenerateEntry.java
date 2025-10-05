package com.arc_studio.brick_lib_api.datagen;

import com.arc_studio.brick_lib_api.core.EnvType;
import net.minecraft.data.DataProvider;

import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Function;

public record DataGenerateEntry(
        EnvType side,
        Function<Collection<Path>, DataProvider.Factory<?>> factory
) {
}
