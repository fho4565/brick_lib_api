package com.arc_studio.brick_lib_api.core.data.saved_data;

//? if >= 1.20.6 {
/*import net.minecraft.core.HolderLookup;
*///?}
//? if >= 1.20.2 {
/*import com.arc_studio.brick_lib_api.core.data.capability.builtin.example.FurnaceEnergyData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.datafix.DataFixTypes;
import java.util.function.Function;
import java.util.function.Supplier;
*///?}
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public abstract class BrickSavedData extends SavedData {
    public BrickSavedData() {
    }

    public abstract CompoundTag saveData(CompoundTag tag);

    //? if >= 1.21.5 {
    /*public CompoundTag saveToTag() {
        CompoundTag tag = new CompoundTag();
        return saveData(tag);
    }*/
    //?} else if >= 1.20.6 {
    /*@Override
    public @NotNull CompoundTag save(CompoundTag tag, HolderLookup.Provider registries) {
        return saveData(tag);
    }

    *///?} else {
    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        return saveData(tag);
    }
    //?}
}
