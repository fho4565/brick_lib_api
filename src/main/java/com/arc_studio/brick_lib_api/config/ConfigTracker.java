package com.arc_studio.brick_lib_api.config;

import com.arc_studio.brick_lib_api.core.event.BrickEventBus;
import com.arc_studio.brick_lib_api.events.ConfigEvent;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.mojang.logging.LogUtils;
import org.slf4j.Logger;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class is a modified version of ConfigTracker from the Minecraft Forge API,
 * which was developed by Forge Development LLC and contributors and licensed under the GNU Lesser General Public License v2.1.
 * <p>The original work is copyright (C) Forge Development LLC and contributors.</p>
 * <p>This modified version is also licensed under the terms of the GNU LGPL v2.1</p>
 *
 *
 * Modifications made by fho4565:
 *<ol>
 * <li>Using Brick Lib's event system instead of Forge's</li>
 * <li>Refactor the code by changing instance fields to static fields and hiding the constructor</li>
 * </ol>
 */
public class ConfigTracker {
    private static final Logger LOGGER = LogUtils.getLogger();
    static final Marker CONFIG = MarkerFactory.getMarker("CONFIG");
    private static final ConcurrentHashMap<String, ModConfig> fileMap = new ConcurrentHashMap<>();
    private static final EnumMap<ModConfig.Type, Set<ModConfig>> configSets = new EnumMap<>(ModConfig.Type.class);
    private static final ConcurrentHashMap<String, Map<ModConfig.Type, ModConfig>> configsByMod = new ConcurrentHashMap<>();

    static  {
        configSets.put(ModConfig.Type.CLIENT, Collections.synchronizedSet(new LinkedHashSet<>()));
        configSets.put(ModConfig.Type.COMMON, Collections.synchronizedSet(new LinkedHashSet<>()));
        configSets.put(ModConfig.Type.SERVER, Collections.synchronizedSet(new LinkedHashSet<>()));
    }

    private ConfigTracker(){

    }

    static void trackConfig(final ModConfig config) {
        if (fileMap.containsKey(config.getFileName())) {
            LOGGER.error(CONFIG,
                    "Detected config file conflict {} between {} and {}",
                    config.getFileName(),
                    fileMap.get(config.getFileName()).getModId(),
                    config.getModId());
            throw new RuntimeException("Config conflict detected!");
        }
        fileMap.put(config.getFileName(), config);
        configSets.get(config.getType()).add(config);
        configsByMod.computeIfAbsent(config.getModId(),
                (k)->new EnumMap<>(ModConfig.Type.class))
                .put(config.getType(), config);
        LOGGER.debug(CONFIG, "Config file {} for {} tracking", config.getFileName(), config.getModId());
    }

    public static void loadConfigs(ModConfig.Type type, Path configBasePath) {
        LOGGER.debug(CONFIG, "Loading configs type {}", type);
        configSets.get(type).forEach(config -> {
            openConfig(config, configBasePath);
        });
    }

    public static void unloadConfigs(ModConfig.Type type, Path configBasePath) {
        LOGGER.debug(CONFIG, "Unloading configs type {}", type);
        configSets.get(type).forEach(config -> closeConfig(config, configBasePath));
    }

    private static void openConfig(final ModConfig config, final Path configBasePath) {
        LOGGER.trace(CONFIG,
                "Loading config file type {} at {} for {}",
                config.getType(),
                config.getFileName(),
                config.getModId());
        CommentedFileConfig configData = ConfigFileTypeHandler.reader(configBasePath).apply(config);
        config.setConfigData(configData);
        BrickEventBus.postEvent(new ConfigEvent.Load(config));
        config.save();
    }

    private static void closeConfig(final ModConfig config, final Path configBasePath) {
        if (config.getConfigData() != null) {
            LOGGER.trace(CONFIG,
                    "Closing config file type {} at {} for {}",
                    config.getType(),
                    config.getFileName(),
                    config.getModId());
            ConfigFileTypeHandler.unload(configBasePath, config);
            BrickEventBus.postEvent(new ConfigEvent.Unload(config));
            config.save();
            config.setConfigData(null);
        }
    }

    public static void loadDefaultServerConfigs() {
        configSets.get(ModConfig.Type.SERVER).forEach(modConfig -> {
            final CommentedConfig commentedConfig = CommentedConfig.inMemory();
            modConfig.getSpec().correct(commentedConfig);
            modConfig.setConfigData(commentedConfig);
            BrickEventBus.postEvent(new ConfigEvent.Load(modConfig));
        });
    }

    public static String getConfigFileName(String modId, ModConfig.Type type) {
        Map<ModConfig.Type, ModConfig> typeConfigMap = configsByMod.get(modId);
        if (typeConfigMap != null) {
            ModConfig config = typeConfigMap.get(type);
            if (config != null) {
                return config.getFullPath().toString();
            }
        }
        return null;
    }


    public static Map<ModConfig.Type, Set<ModConfig>> configSets() {
        return Collections.unmodifiableMap(configSets);
    }

    public static Map<String, ModConfig> fileMap() {
        return Collections.unmodifiableMap(fileMap);
    }
}
