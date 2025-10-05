package com.arc_studio.brick_lib_api.core.network.context;

import net.minecraft.server.level.ServerPlayer;

/**
 * 网络事件上下文的包装类
 * */
abstract class NetworkContext {
    protected final NetworkDirection direction;
    protected final ServerPlayer serverPlayer;

    public NetworkContext(NetworkDirection direction,ServerPlayer serverPlayer) {
        this.serverPlayer = serverPlayer;
        this.direction = direction;
    }

    public NetworkDirection direction() {
        return direction;
    }
}
