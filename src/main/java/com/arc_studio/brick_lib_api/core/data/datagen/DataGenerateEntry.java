package com.arc_studio.brick_lib_api.core.data.datagen;

import com.arc_studio.brick_lib_api.core.PlatformInfo;

import java.nio.file.Path;
import java.util.Collection;
import java.util.function.Function;

public record DataGenerateEntry(PlatformInfo side,
                                Function<Collection<Path>, BrickDataGenerator.Factory<?>> factory) {

}
