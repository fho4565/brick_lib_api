package com.arc_studio.brick_lib_api.config;

import java.util.EnumMap;

import static com.arc_studio.brick_lib_api.BrickLibAPI.LOGGER;

/**
 * This class is a modified version of ModLoadingContext from the Minecraft Forge API,
 * which was developed by Forge Development LLC and contributors and licensed under the GNU Lesser General Public License v2.1.
 * <p>The original work is copyright (C) Forge Development LLC and contributors.</p>
 * <p>This modified version is also licensed under the terms of the GNU LGPL v2.1</p>
 *
 *
 * Modifications made by fho4565:
 *<ol>
 * <li>Modified to support cross-version requirements.</li>
 * <li>Extracts the {@code registerConfig} and {@code addConfig} methods.</li>
 * </ol>
 */
public class ConfigManager {
    protected static final EnumMap<ModConfig.Type, ModConfig> configs = new EnumMap<>(ModConfig.Type.class);

    private static void addConfig(final ModConfig modConfig) {
        configs.put(modConfig.getType(), modConfig);
    }

    public static void registerConfig(ModConfig.Type type, BrickConfigSpec spec,String modId) {
        if (spec.isEmpty())
        {
            // This handles the case where a mod tries to register a config, without any options configured inside it.
            LOGGER.debug("Attempted to register an empty config for type {} on mod {}", type, modId);
            return;
        }
        addConfig(new ModConfig(type, spec,ModConfig.defaultConfigName(type,modId),modId));
    }

    public static void registerConfig(ModConfig.Type type, BrickConfigSpec spec, String fileName,String modId) {
        if (spec.isEmpty())
        {
            // This handles the case where a mod tries to register a config, without any options configured inside it.
            LOGGER.debug("Attempted to register an empty config for type {} on mod {} using file name {}", type, modId, fileName);
            return;
        }
        addConfig(new ModConfig(type, spec, fileName,modId));
    }
}
