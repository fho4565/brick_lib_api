package com.arc_studio.brick_lib_api.core.network.context;

import com.arc_studio.brick_lib_api.platform.Platform;
import net.minecraft.server.level.ServerPlayer;

import java.util.function.Consumer;

/**
 * 从客户端单向发往服务端的网络上下文
 * */
public class C2SNetworkContext extends NetworkContext {

    public C2SNetworkContext(ServerPlayer serverPlayer) {
        super(NetworkDirection.P2S, serverPlayer);
    }

    public void getSenderAnd(Consumer<ServerPlayer> consumer){
        consumer.accept(this.serverPlayer);
    }
    public ServerPlayer getSender(){
        return serverPlayer;
    }
    public void enqueueWork(Runnable runnable){
        Platform.enqueueWork(this,runnable);
    }
}
