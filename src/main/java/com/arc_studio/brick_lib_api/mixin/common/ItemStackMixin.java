package com.arc_studio.brick_lib_api.mixin.common;

import com.arc_studio.brick_lib_api.core.data.ItemAdditionalData;
//? if >=1.20.6 {
/*import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.PatchedDataComponentMap;
*///?}

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Shadow
    public abstract boolean isEmpty();

    @Shadow
    public abstract Item getItem();

    //? if >=1.20.6 {
    /*@Inject(method = "isSameItemSameComponents", at = @At("HEAD"), cancellable = true)
    private static void isSame(ItemStack stack, ItemStack other, CallbackInfoReturnable<Boolean> cir) {
        if (!stack.is(other.getItem())) {
            cir.setReturnValue(false);
        } else {
            if (stack.isEmpty() && other.isEmpty()) {
                cir.setReturnValue(true);
            }
            PatchedDataComponentMap otherTag = new PatchedDataComponentMap(other.getComponents());
            PatchedDataComponentMap thisTag = new PatchedDataComponentMap(stack.getComponents());
            thisTag.remove(ItemAdditionalData.DATA_COMPONENT_TYPE);
            otherTag.remove(ItemAdditionalData.DATA_COMPONENT_TYPE);
            cir.setReturnValue(Objects.equals(thisTag, otherTag));
        }
    }
    *///?} else {
    @Inject(method = "isSameItemSameTags", at = @At("HEAD"), cancellable = true)
    private static void isSame(ItemStack stack, ItemStack other, CallbackInfoReturnable<Boolean> cir) {
        if (!stack.is(other.getItem())) {
            cir.setReturnValue(false);
        } else {
            if (stack.isEmpty() && other.isEmpty()) {
                cir.setReturnValue(true);
            }
            CompoundTag thisTag = stack.getOrCreateTag();
            CompoundTag otherTag = other.getOrCreateTag();
            thisTag.remove(ItemAdditionalData.KEY_DATA);
            otherTag.remove(ItemAdditionalData.KEY_DATA);
            cir.setReturnValue(Objects.equals(thisTag, otherTag));
        }
    }
    //?}

    @Unique
    private ItemStack getThis(){
        return (ItemStack) (Object) this;
    }
}
