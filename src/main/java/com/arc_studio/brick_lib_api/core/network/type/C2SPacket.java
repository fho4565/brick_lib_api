package com.arc_studio.brick_lib_api.core.network.type;


import com.arc_studio.brick_lib_api.BrickLibAPI;
import com.arc_studio.brick_lib_api.core.network.PacketContent;
import com.arc_studio.brick_lib_api.core.network.context.C2SNetworkContext;
//? if >= 1.20.4 {
/*import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
*///?}
import net.minecraft.resources.ResourceLocation;

/**
 * 从客户端单向发送到服务端的包
 */
public abstract class C2SPacket extends Packet implements ISHandlePacket {
    //? if >1.20.4 {
    /*@Override
    public Type<? extends CustomPacketPayload> type() {
        return new Type<>(id());
    }
    *///?}
    public final void handler(C2SNetworkContext context) {
        context.enqueueWork(() -> serverHandle(context));
    }

    @Override
    public ResourceLocation id() {
        return BrickLibAPI.ofPath(this.getClass().getName().replace(".", "_").toLowerCase());
    }
}
