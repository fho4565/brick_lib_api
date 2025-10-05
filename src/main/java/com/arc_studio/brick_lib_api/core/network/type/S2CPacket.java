package com.arc_studio.brick_lib_api.core.network.type;

import com.arc_studio.brick_lib_api.BrickLibAPI;
import com.arc_studio.brick_lib_api.core.network.context.S2CNetworkContext;
import com.arc_studio.brick_lib_api.core.SideExecutor;
//? if >= 1.20.4 {
/*import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
*///?}
import net.minecraft.resources.ResourceLocation;

/**
 * 从服务端单向发送到客户端的包
 */
public abstract class S2CPacket extends Packet implements ICHandlePacket {
    //? if > 1.20.4 {
    /*@Override
    public Type<? extends CustomPacketPayload> type() {
        return new Type<>(id());
    }
    *///?}
    public final void handler(S2CNetworkContext context) {
        context.enqueueWork(() -> SideExecutor.runOnClientOrException(() -> clientHandle(context)));
    }

    @Override
    public ResourceLocation id() {
        return BrickLibAPI.ofPath(this.getClass().getName().replace(".", "_").toLowerCase());
    }
}
