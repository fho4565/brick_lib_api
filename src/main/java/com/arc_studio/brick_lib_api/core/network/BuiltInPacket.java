package com.arc_studio.brick_lib_api.core.network;

import com.arc_studio.brick_lib_api.core.event.BrickEventBus;
import com.arc_studio.brick_lib_api.core.network.type.SACPacket;
import com.arc_studio.brick_lib_api.events.NetworkMessageEvent;
import com.arc_studio.brick_lib_api.core.network.context.C2SNetworkContext;
import com.arc_studio.brick_lib_api.core.network.context.S2CNetworkContext;
import org.jetbrains.annotations.ApiStatus;

/**
 * @author fho4565
 */
@ApiStatus.Internal
public final class BuiltInPacket extends SACPacket {
    final String id,message;

    public BuiltInPacket(String id,String message) {
        this.id = id;
        this.message = message;
    }

    public BuiltInPacket(PacketContent content){
        id = content.readUTF();
        message = content.readUTF();
    }

    @Override
    public void serverHandle(C2SNetworkContext context) {
        System.out.println("BuiltInPacket.serverHandle");
        BrickEventBus.postEvent(new NetworkMessageEvent.ServerReceive(id,message,context.getSender()));
    }

    @Override
    public void clientHandle(S2CNetworkContext context) {
        System.out.println("BuiltInPacket.clientHandle");
        BrickEventBus.postEvent(new NetworkMessageEvent.ClientReceive(id,message));
    }

    @Override
    public void encoder(PacketContent content) {
        content.writeUTF(id);
        content.writeUTF(message);
    }
}
