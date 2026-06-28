package com.arc_studio.brick_lib_api.core.data.datagen;

import com.arc_studio.brick_lib_api.core.data.ResourceID;

import java.nio.file.Path;

public class PathProvider {
    private final Path root;
    private final String kind;

    PathProvider(Path root, String kind) {
        this.root = root;
        this.kind = kind;
    }

    public Path file(ResourceID location, String extension) {
        return this.root.resolve(location.getNamespace()).resolve(this.kind).resolve(location.getPath() + "." + extension);
    }

    public Path json(ResourceID location) {
        return this.root.resolve(location.getNamespace()).resolve(this.kind).resolve(location.getPath() + ".json");
    }
}
