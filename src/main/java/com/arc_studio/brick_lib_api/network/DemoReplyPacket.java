package com.arc_studio.brick_lib_api.network;

import com.arc_studio.brick_lib_api.core.network.PacketContent;
import com.arc_studio.brick_lib_api.core.network.context.C2SNetworkContext;
import com.arc_studio.brick_lib_api.core.network.context.S2CNetworkContext;
import com.arc_studio.brick_lib_api.core.network.type.LoginPacket;

public class DemoReplyPacket extends LoginPacket {
    public DemoReplyPacket() {
        System.out.println("DemoReplyPacket.DemoReplyPacket");
    }

    public DemoReplyPacket(PacketContent content){

    }
    @Override
    public void serverHandle(C2SNetworkContext context) {
        System.out.println("Reply Server");
    }

    @Override
    public void encoder(PacketContent content) {

    }

    @Override
    public void clientHandle(S2CNetworkContext context) {
        System.out.println("Reply Client");
    }
}
