package com.arc_studio.brick_lib_api.datagen;

import com.arc_studio.brick_lib_api.register.BrickRegistries;
import com.arc_studio.brick_lib_api.Constants;
import net.minecraft.SharedConstants;
import net.minecraft.data.DataGenerator;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class BrickDataGenerator {
    private static final List<Path> inputs = List.of(Path.of(Constants.versionDataFolder().toUri()));
    private static final Path output = Paths.get("generated");
    public static void run(boolean client, boolean server) throws IOException {
        DataGenerator dataGenerator = new DataGenerator(output, SharedConstants.getCurrentVersion(), true);
        DataGenerator.PackGenerator clientGenerator = dataGenerator.getVanillaPack(client);
        DataGenerator.PackGenerator serverGenerator = dataGenerator.getVanillaPack(server);
        BrickRegistries.DATA_GENERATE.forEach(entry -> {
            if (entry.side().isClient()) {
                clientGenerator.addProvider(entry.factory().apply(inputs));
            }
            if (entry.side().isServer()) {
                serverGenerator.addProvider(entry.factory().apply(inputs));
            }
        });
        dataGenerator.run();
    }
}
