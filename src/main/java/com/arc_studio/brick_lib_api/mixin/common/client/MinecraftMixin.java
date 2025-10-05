package com.arc_studio.brick_lib_api.mixin.common.client;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

/**
 * @author fho4565
 */
@Mixin(Minecraft.class)
public abstract class MinecraftMixin {


    @Unique
    private Minecraft getThis(){
        return (Minecraft) (Object) this;
    }
}
