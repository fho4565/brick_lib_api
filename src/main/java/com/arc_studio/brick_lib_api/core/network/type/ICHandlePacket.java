package com.arc_studio.brick_lib_api.core.network.type;

import com.arc_studio.brick_lib_api.core.network.context.S2CNetworkContext;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface ICHandlePacket extends IHandleablePacket
{
    void clientHandle(S2CNetworkContext context);
}
