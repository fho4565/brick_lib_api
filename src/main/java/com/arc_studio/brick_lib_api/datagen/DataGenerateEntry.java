package com.arc_studio.brick_lib_api.datagen;

import com.arc_studio.brick_lib_api.core.PlatformInfo;
import net.minecraft.data.DataProvider;

import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.function.Function;

public record DataGenerateEntry(PlatformInfo side,
                                Function<Collection<Path>, BrickDataGenerator.Factory<?>> factory) {

}
