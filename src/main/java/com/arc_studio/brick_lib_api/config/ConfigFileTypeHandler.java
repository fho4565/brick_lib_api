package com.arc_studio.brick_lib_api.config;

import com.arc_studio.brick_lib_api.core.event.BrickEventBus;
import com.arc_studio.brick_lib_api.events.ConfigEvent;
import com.arc_studio.brick_lib_api.Constants;
import com.electronwill.nightconfig.core.ConfigFormat;
import com.electronwill.nightconfig.core.file.CommentedFileConfig;
import com.electronwill.nightconfig.core.file.FileWatcher;
import com.electronwill.nightconfig.core.io.ParsingException;
import com.electronwill.nightconfig.core.io.WritingMode;
import com.mojang.logging.LogUtils;
import org.apache.commons.io.FilenameUtils;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.function.Function;

import static com.arc_studio.brick_lib_api.config.ConfigTracker.CONFIG;
/**
 * This class is a modified version of ConfigFileTypeHandler from the Minecraft Forge API,
 * which was developed by Forge Development LLC and contributors and licensed under the GNU Lesser General Public License v2.1.
 * <p>The original work is copyright (C) Forge Development LLC and contributors.</p>
 * <p>This modified version is also licensed under the terms of the GNU LGPL v2.1</p>
 *
 *
 * Modifications made by fho4565:
 *<ol>
 * <li>Modified to support cross-version requirements.</li>
 * <li>Refactor the code by changing instance fields and methods to static fields and methods and hiding the constructor</li>
 * </ol>
 */
public class ConfigFileTypeHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Path defaultConfigPath = Constants.defaultConfigFolderPath();

    public static Function<ModConfig, CommentedFileConfig> reader(Path configBasePath) {
        return (c) -> {
            try {
                return getConfig(configBasePath, c);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static @NotNull CommentedFileConfig getConfig(Path configBasePath, ModConfig c) throws IOException {
        final Path configPath = configBasePath.resolve(c.getFileName());
        final CommentedFileConfig configData = CommentedFileConfig.builder(configPath)
                .sync().
                preserveInsertionOrder().
                autosave().
                onFileNotFound((path, configFormat)-> setupConfigFile(c, path, configFormat)).
                writingMode(WritingMode.REPLACE).
                build();
        LOGGER.debug(CONFIG, "Built TOML config for {}", configPath);
        try
        {
            configData.load();
        }
        catch (ParsingException ex)
        {
            LOGGER.warn(CONFIG, "Attempting to recreate {}", configPath);
            try
            {
                backUpConfig(configData, 5);
                Files.delete(configData.getNioPath());

                configData.load();
            }
            catch (Throwable t)
            {
                ex.addSuppressed(t);

                throw new ConfigLoadingException(c, ex);
            }
        }
        LOGGER.debug(CONFIG, "Loaded TOML config file {}", configPath);
        FileWatcher.defaultInstance().addWatch(configPath, new ConfigWatcher(c, configData, Thread.currentThread().getContextClassLoader()));
        LOGGER.debug(CONFIG, "Watching TOML config file {} for changes", configPath);
        return configData;
    }

    public static void unload(Path configBasePath, ModConfig config) {
        Path configPath = configBasePath.resolve(config.getFileName());
        try {
            FileWatcher.defaultInstance().removeWatch(configBasePath.resolve(config.getFileName()));
        } catch (RuntimeException e) {
            LOGGER.error("Failed to remove config {} from tracker!", configPath, e);
        }
    }

    private static boolean setupConfigFile(final ModConfig modConfig, final Path file, final ConfigFormat<?> conf) throws IOException {
        Files.createDirectories(file.getParent());
        Path p = defaultConfigPath.resolve(modConfig.getFileName());
        if (Files.exists(p)) {
            LOGGER.info(CONFIG, "Loading default config file from path {}", p);
            Files.copy(p, file);
        } else {
            Files.createFile(file);
            conf.initEmptyFile(file);
        }
        return true;
    }

    public static void backUpConfig(final CommentedFileConfig commentedFileConfig)
    {
        backUpConfig(commentedFileConfig, 5);
    }

    public static void backUpConfig(final CommentedFileConfig commentedFileConfig, final int maxBackups)
    {
        Path bakFileLocation = commentedFileConfig.getNioPath().getParent();
        String bakFileName = FilenameUtils.removeExtension(commentedFileConfig.getFile().getName());
        String bakFileExtension = FilenameUtils.getExtension(commentedFileConfig.getFile().getName()) + ".bak";
        Path bakFile = bakFileLocation.resolve(bakFileName + "-1" + "." + bakFileExtension);
        try
        {
            for(int i = maxBackups; i > 0; i--)
            {
                Path oldBak = bakFileLocation.resolve(bakFileName + "-" + i + "." + bakFileExtension);
                if(Files.exists(oldBak))
                {
                    if(i >= maxBackups) {
                        Files.delete(oldBak);
                    } else {
                        Files.move(oldBak, bakFileLocation.resolve(bakFileName + "-" + (i + 1) + "." + bakFileExtension));
                    }
                }
            }
            Files.copy(commentedFileConfig.getNioPath(), bakFile);
        }
        catch (IOException exception)
        {
            LOGGER.warn(CONFIG, "Failed to back up config file {}", commentedFileConfig.getNioPath(), exception);
        }
    }

    private static class ConfigWatcher implements Runnable {
        private final ModConfig modConfig;
        private final CommentedFileConfig commentedFileConfig;
        private final ClassLoader realClassLoader;

        ConfigWatcher(final ModConfig modConfig, final CommentedFileConfig commentedFileConfig, final ClassLoader classLoader) {
            this.modConfig = modConfig;
            this.commentedFileConfig = commentedFileConfig;
            this.realClassLoader = classLoader;
        }

        @Override
        public void run() {
            // Force the regular classloader onto the special thread
            Thread.currentThread().setContextClassLoader(realClassLoader);
            if (!this.modConfig.getSpec().isCorrecting()) {
                try
                {
                    this.commentedFileConfig.load();
                    if(!this.modConfig.getSpec().isCorrect(commentedFileConfig))
                    {
                        LOGGER.warn(CONFIG, "Configuration file {} is not correct. Correcting", commentedFileConfig.getFile().getAbsolutePath());
                        ConfigFileTypeHandler.backUpConfig(commentedFileConfig);
                        this.modConfig.getSpec().correct(commentedFileConfig);
                        commentedFileConfig.save();
                    }
                }
                catch (ParsingException ex)
                {
                    throw new ConfigLoadingException(modConfig, ex);
                }
                LOGGER.debug(CONFIG, "Config file {} changed, sending notifies", this.modConfig.getFileName());
                this.modConfig.getSpec().afterReload();
                BrickEventBus.postEvent(new ConfigEvent.Reload(this.modConfig));
            }
        }
    }

    private static class ConfigLoadingException extends RuntimeException
    {
        public ConfigLoadingException(ModConfig config, Exception cause)
        {
            super("Failed loading config file %s of type %s for mod id %s".formatted(config.getFileName(), config.getType(), config.getModId()), cause);
        }
    }
}
