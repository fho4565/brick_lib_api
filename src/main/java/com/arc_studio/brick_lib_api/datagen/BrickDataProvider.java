package com.arc_studio.brick_lib_api.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import net.minecraft.data.DataProvider;
//? if > 1.19.2 {
import net.minecraft.data.PackOutput;
import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
//?} elif > 1.18.2 {
/*import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataGenerator;
*///?}
import net.minecraft.data.HashCache;
import org.apache.commons.lang3.tuple.Pair;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

public abstract class BrickDataProvider implements DataProvider {
    //? if > 1.19.2 {
    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        List<CompletableFuture<?>> list = new ArrayList<>();
        for (Pair<JsonElement, Path> pair : contents(new PathProvider(map.get(type()),registerName()))) {
            list.add(DataProvider.saveStable(output, pair.getLeft(), pair.getRight()));
        }
        return CompletableFuture.allOf(list.toArray(CompletableFuture[]::new));
    }

    //?} else {


    /*//? if > 1.18.2 {
    @Override
    public void run(CachedOutput output) throws IOException {
        PathProvider provider = new PathProvider(map.get(type()), registerName());
        for (Pair<JsonElement, Path> pair : contents(provider)) {
            DataProvider.saveStable(output, pair.getLeft(), pair.getRight());
        }
    }

    //?} else {
    /^private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    @Override
    public void run(HashCache output) throws IOException {
        PathProvider provider = new PathProvider(map.get(type()), registerName());
        for (Pair<JsonElement, Path> pair : contents(provider)) {
            DataProvider.save(GSON,output, pair.getLeft(), pair.getRight());
        }
    }

    ^///?}

    *///?}
    private final Map<BrickDataGenerator.TargetType,Path> map;

    public BrickDataProvider(Map<BrickDataGenerator.TargetType,Path> map) {
        this.map = map;
    }
    public abstract List<Pair<JsonElement, Path>> contents(PathProvider output);

    public abstract BrickDataGenerator.TargetType type();
    public abstract String registerName();
    @Override
    public String getName() {
        return this.getClass().getName().replace(".", "_").toLowerCase();
    }
}
