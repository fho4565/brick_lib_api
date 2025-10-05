package com.arc_studio.brick_lib_api.platform;


//? if neoforge {
/*import com.arc_studio.brick_lib_api.register.BrickRegistries;
import net.neoforged.api.distmarker.Dist;

//? if < 1.20.4 {
//?} else if <1.20.6 {
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
//?} else {
/^import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.fml.common.EventBusSubscriber;
^///?}

//? if < 1.20.6 {
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
 //?} else {
/^@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD,value = Dist.CLIENT)
^///?}
public class NeoForgeClientPlatform {
    //? if neoforge {
    /^@SubscribeEvent
    public static void onRegisterKeyMappings(RegisterKeyMappingsEvent events) {
        BrickRegistries.KEY_MAPPING.foreachRegistered((resourceLocation, keyMapping) -> events.register(keyMapping));
    }
    ^///?}
}
*/