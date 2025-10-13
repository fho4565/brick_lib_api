package com.arc_studio.brick_lib_api.datagen;

import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;

public class PathProvider {
    private final Path root;
    private final String kind;

    PathProvider(Path root, String kind) {
        this.root = root;
        this.kind = kind;
    }

    public Path file(ResourceLocation location, String extension) {
        return this.root.resolve(location.getNamespace()).resolve(this.kind).resolve(location.getPath() + "." + extension);
    }

    public Path json(ResourceLocation location) {
        return this.root.resolve(location.getNamespace()).resolve(this.kind).resolve(location.getPath() + ".json");
    }
}
