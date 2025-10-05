package com.arc_studio.brick_lib_api.datagen;

import com.google.gson.JsonElement;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import org.apache.commons.lang3.tuple.Pair;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public abstract class BrickDataProvider implements DataProvider {
    private final PackOutput packOutput;

    protected BrickDataProvider(PackOutput packOutput) {
        this.packOutput = packOutput;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> list = new ArrayList<>();
        PackOutput.PathProvider provider = packOutput.createPathProvider(type(), registerName());
        for (Pair<JsonElement, Path> pair : contents(provider)) {
            list.add(DataProvider.saveStable(output, pair.getLeft(), pair.getRight()));
        }
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    public abstract List<Pair<JsonElement, Path>> contents(PackOutput.PathProvider provider);

    public abstract PackOutput.Target type();
    public abstract String registerName();

    @Override
    public String getName() {
        return this.getClass().getName().replace(".", "_").toLowerCase();
    }
}
