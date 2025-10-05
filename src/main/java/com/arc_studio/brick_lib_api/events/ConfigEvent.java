package com.arc_studio.brick_lib_api.events;

import com.arc_studio.brick_lib_api.core.event.BaseEvent;
import com.arc_studio.brick_lib_api.config.ModConfig;

public class ConfigEvent extends BaseEvent {
    private final ModConfig config;

    public ConfigEvent(ModConfig config) {
        this.config = config;
    }

    public ModConfig config() {
        return config;
    }

    public static class Load extends ConfigEvent {

        public Load(ModConfig config) {
            super(config);
        }
    }

    public static class Unload extends ConfigEvent {

        public Unload(ModConfig config) {
            super(config);
        }
    }

    public static class Reload extends ConfigEvent {

        public Reload(ModConfig config) {
            super(config);
        }
    }
}
