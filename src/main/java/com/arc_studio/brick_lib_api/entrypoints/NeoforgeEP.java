//? if neoforge {
/*package com.arc_studio.brick_lib_api.entrypoints;

import com.arc_studio.brick_lib_api.BrickLibAPI;
import com.arc_studio.brick_lib_api.core.data.capability.builtin.example.CrossPlatformEvents;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;

/^*
 * NeoForge模组的入口点
 ^/
@Mod(BrickLibAPI.MOD_ID)
public class NeoforgeEP {
    public NeoforgeEP(IEventBus modBus) {
        CrossPlatformEvents.registerModBus(modBus);
        CommonEP.init();
    }
}
*///?}
