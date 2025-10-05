package com.arc_studio.brick_lib_api.config;

import com.arc_studio.brick_lib_api.core.event.BrickEventBus;
import com.arc_studio.brick_lib_api.events.ConfigEvent;
import com.electronwill.nightconfig.core.CommentedConfig;
import com.electronwill.nightconfig.core.file.FileConfig;
import com.electronwill.nightconfig.toml.TomlFormat;

import java.io.ByteArrayInputStream;
import java.nio.file.Path;
import java.util.Locale;

/**
 * This class is a modified version of ModConfig from the Minecraft Forge API,
 * which was developed by Forge Development LLC and contributors and licensed under the GNU Lesser General Public License v2.1.
 * <p>The original work is copyright (C) Forge Development LLC and contributors.</p>
 * <p>This modified version is also licensed under the terms of the GNU LGPL v2.1</p>
 *
 *
 * Modifications made by fho4565:
 *<ol>
 * <li>Using Brick Lib's event system instead of Forge's</li>
 * <li>Modified to support cross-version requirements.</li>
 * </ol>
 */
public class ModConfig {
    private final Type type;
    private final BrickConfigSpec spec;
    private final String fileName;
    private final String modId;
    private CommentedConfig configData;

    public ModConfig(Type type, BrickConfigSpec spec, String fileName,String modId) {
        this.type = type;
        this.spec = spec;
        this.fileName = fileName;
        this.modId = modId;
        ConfigTracker.trackConfig(this);
    }

    public static String defaultConfigName(Type type, String modId) {
        return String.format(Locale.ROOT, "%s-%s.toml", modId, type.extension());
    }
    public Type getType() {
        return type;
    }

    public String getFileName() {
        return fileName;
    }

    public <T extends BrickConfigSpec> BrickConfigSpec getSpec() {
        return spec;
    }

    public String getModId() {
        return modId;
    }

    public CommentedConfig getConfigData() {
        return this.configData;
    }

    void setConfigData(final CommentedConfig configData) {
        this.configData = configData;
        this.spec.acceptConfig(this.configData);
    }

    public void save() {
        ((FileConfig)this.configData).save();
    }

    public Path getFullPath() {
        return ((FileConfig)this.configData).getNioPath();
    }

    public void acceptSyncedConfig(byte[] bytes) {
        this.setConfigData((CommentedConfig)TomlFormat.instance().createParser().parse(new ByteArrayInputStream(bytes)));
        BrickEventBus.postEvent(new ConfigEvent.Reload(this));
    }

    public enum Type {
        /**
         * Common mod config for configuration that needs to be loaded on both environments.
         * Loaded on both servers and clients.
         * Stored in the global config directory.
         * Not synced.
         * Suffix is "-common" by default.
         */
        COMMON,
        /**
         * Client config is for configuration affecting the ONLY client state such as graphical options.
         * Only loaded on the client side.
         * Stored in the global config directory.
         * Not synced.
         * Suffix is "-client" by default.
         */
        CLIENT,
        /**
         * Server type config is configuration that is associated with a server instance.
         * Only loaded during server startup.
         * Stored in a server/save specific "serverconfig" directory.
         * Synced to clients during connection.
         * Suffix is "-server" by default.
         */
        SERVER;

        public String extension() {
            return name().toLowerCase();
        }
    }
}
