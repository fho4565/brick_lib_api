package com.arc_studio.brick_lib_api.core.network.type;

import com.arc_studio.brick_lib_api.core.network.context.C2SNetworkContext;
import org.jetbrains.annotations.ApiStatus;

public interface ISHandlePacket extends IHandleablePacket {
    void serverHandle(C2SNetworkContext context);
}
