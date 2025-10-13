package com.arc_studio.brick_lib_api.datagen;

import com.arc_studio.brick_lib_api.register.BrickRegistries;
import com.arc_studio.brick_lib_api.Constants;
import net.minecraft.SharedConstants;
//? if > 1.18.2 {
import net.minecraft.data.CachedOutput;
//?}
//? if > 1.19.2 {
import net.minecraft.data.PackOutput;
//?}
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;


import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class BrickDataGenerator {
    private static final List<Path> inputs = List.of(Path.of(Constants.versionDataFolder().toUri()));
    private static final Path output = Paths.get("generated");

    //? if > 1.19.2 {
    public static void run(boolean client, boolean server) throws IOException {
        DataGenerator dataGenerator = new DataGenerator(output, SharedConstants.getCurrentVersion(), true);
        DataGenerator.PackGenerator clientGenerator = dataGenerator.getVanillaPack(client);
        DataGenerator.PackGenerator serverGenerator = dataGenerator.getVanillaPack(server);
        BrickRegistries.DATA_GENERATE.forEach(entry -> {
            DataProvider.Factory<?> factory = arg -> entry.factory().apply(inputs).create(
                Arrays.stream(PackOutput.Target.values()).collect(Collectors.toMap(TargetType::from, arg::getOutputFolder))
            );
            if (entry.side().isClient()) {
                clientGenerator.addProvider(factory);
            }
            if (entry.side().isServer()) {
                serverGenerator.addProvider(factory);
            }
        });
        dataGenerator.run();
    }
    //?} elif > 1.18.2 {
    /*public static void run(boolean client, boolean server) throws IOException {
        DataGenerator dataGenerator = new DataGenerator(output,inputs, SharedConstants.getCurrentVersion(), true);
        BrickRegistries.DATA_GENERATE.forEach(entry -> {
            HashMap<TargetType,Path> map = new HashMap<>();
            for (TargetType type : TargetType.values()) {
                map.put(type,dataGenerator.getOutputFolder(BrickDataGenerator.TargetType.transfer(type)));
            }
            if (client) {
                dataGenerator.addProvider(entry.side().isClient(), entry.factory().apply(inputs).create(map));
            }
            if (server) {
                dataGenerator.addProvider(entry.side().isServer(), entry.factory().apply(inputs).create(map));
            }
        });
        dataGenerator.run();
    }
    *///?} else {
    /*public static void run(boolean client, boolean server) throws IOException {
        DataGenerator dataGenerator = new DataGenerator(output,inputs);
        BrickRegistries.DATA_GENERATE.forEach(entry -> {
            HashMap<TargetType,Path> map = new HashMap<>();
            for (TargetType type : TargetType.values()) {
                map.put(type,dataGenerator.getOutputFolder());
            }
            if (client && entry.side().isClient()) {
                dataGenerator.addProvider(entry.factory().apply(inputs).create(map));
            }
            if (server && entry.side().isServer()) {
                dataGenerator.addProvider(entry.factory().apply(inputs).create(map));
            }
        });
        dataGenerator.run();
    }
    *///?}
    @FunctionalInterface
    public interface Factory<T extends DataProvider> {
        T create(Map<TargetType,Path> output);
    }
    public enum TargetType {
        DATA_PACK("data"),
        RESOURCE_PACK("assets"),
        REPORTS("reports");

        final String directory;

        TargetType(String directory) {
            this.directory = directory;
        }

        //? if > 1.18.2 && < 1.19.4 {
        /*public static DataGenerator.Target transfer(TargetType type) {
            return switch (type) {
                case DATA_PACK -> DataGenerator.Target.DATA_PACK;
                case RESOURCE_PACK -> DataGenerator.Target.RESOURCE_PACK;
                case REPORTS -> DataGenerator.Target.REPORTS;
            };
        }

        public static TargetType from(DataGenerator.Target target) {
            return switch (target) {
                case DATA_PACK -> TargetType.DATA_PACK;
                case RESOURCE_PACK -> TargetType.RESOURCE_PACK;
                case REPORTS -> TargetType.REPORTS;
            };
        }
        *///?} elif >= 1.19.4 {
        public static PackOutput.Target transfer(TargetType type) {
            return switch (type) {
                case DATA_PACK -> PackOutput.Target.DATA_PACK;
                case RESOURCE_PACK -> PackOutput.Target.RESOURCE_PACK;
                case REPORTS -> PackOutput.Target.REPORTS;
            };
        }

        public static TargetType from(PackOutput.Target target) {
            return switch (target) {
                case DATA_PACK -> TargetType.DATA_PACK;
                case RESOURCE_PACK -> TargetType.RESOURCE_PACK;
                case REPORTS -> TargetType.REPORTS;
            };
        }
        //?}
    }

}
