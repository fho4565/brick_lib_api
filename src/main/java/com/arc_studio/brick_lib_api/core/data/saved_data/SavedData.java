package com.arc_studio.brick_lib_api.core.data.saved_data;

//? if >= 1.20.6 {
/*import net.minecraft.core.HolderLookup;
*///?}
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.NotNull;

public abstract class SavedData extends net.minecraft.world.level.saveddata.SavedData {
    public SavedData() {
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
    }*/

    //?} else {
    @Override
    public @NotNull CompoundTag save(CompoundTag tag) {
        return saveData(tag);
    }
    //?}
}
