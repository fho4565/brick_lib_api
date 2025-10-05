package com.arc_studio.brick_lib_api.core.network.context;

import com.arc_studio.brick_lib_api.platform.Platform;

/**
 * 从服务端单向发往客户端的网络上下文
 * */
public class S2CNetworkContext extends NetworkContext {

    public S2CNetworkContext() {
        super(NetworkDirection.P2C, null);
    }
    public void enqueueWork(Runnable runnable){
        Platform.enqueueWork(this,runnable);
    }
}
