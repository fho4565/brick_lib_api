package com.arc_studio.brick_lib_api.core.data;

import net.minecraft.nbt.CompoundTag;

public abstract class BaseAdditionalData {
    protected CompoundTag data = new CompoundTag();

    public CompoundTag getData() {
        return data;
    }
    public abstract void onDelete();
}
