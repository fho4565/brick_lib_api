package com.arc_studio.brick_lib_api.network;

import com.arc_studio.brick_lib_api.core.network.PacketContent;
import com.arc_studio.brick_lib_api.core.network.context.C2SNetworkContext;
import com.arc_studio.brick_lib_api.core.network.context.S2CNetworkContext;
import com.arc_studio.brick_lib_api.core.network.type.LoginPacket;
import com.arc_studio.brick_lib_api.platform.Platform;

public class LoginPacketDemo extends LoginPacket {
    String msg = "";

    public LoginPacketDemo(String msg) {
        this.msg = msg;
    }
    public LoginPacketDemo(PacketContent content){
        if(content.friendlyByteBuf().readableBytes() > 0){
            this.msg = content.readUTF();
        }
    }

    @Override
    public void clientHandle(S2CNetworkContext context) {
        System.out.println("LoginPacketDemo.clientHandle : msg = "+msg);
    }

    @Override
    public void encoder(PacketContent content) {
        content.writeUTF(msg);
    }

    @Override
    public void serverHandle(C2SNetworkContext context) {

    }
}
