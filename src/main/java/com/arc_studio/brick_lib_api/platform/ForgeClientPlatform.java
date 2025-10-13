package com.arc_studio.brick_lib_api.platform;

//? if forge {
 import com.arc_studio.brick_lib_api.register.BrickRegistries;
 import net.minecraft.client.KeyMapping;
 import net.minecraftforge.api.distmarker.Dist;
//? if > 1.18.2 {
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
//?} else {
/*import net.minecraftforge.client.ClientRegistry;
 *///?}
 import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
 import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;


@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
//?}
public class ForgeClientPlatform {
    //? if forge {
    //? if > 1.18.2 {
    @SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent event) {
        BrickRegistries.KEY_MAPPING.foreachRegistered((resourceLocation, keyMapping) -> event.register(keyMapping));
    }

    //?} else {
    /*@SubscribeEvent
    public static void onRegisterKeyMappings(FMLClientSetupEvent event) {
        BrickRegistries.KEY_MAPPING.foreachRegistered((resourceLocation, keyMapping) -> ClientRegistry.registerKeyBinding(keyMapping));
    }
    *///?}
    //?}
}
