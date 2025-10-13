package com.arc_studio.brick_lib_api.core.network.type;

import com.arc_studio.brick_lib_api.core.network.PacketContent;
import com.arc_studio.brick_lib_api.core.network.context.C2SNetworkContext;
import com.arc_studio.brick_lib_api.core.network.context.S2CNetworkContext;
//? if >= 1.20.4 {
/*import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;
import org.jetbrains.annotations.Nullable;
*///?}

import java.util.function.IntSupplier;

public abstract class LoginPacket extends Packet implements IntSupplier , IHandleablePacket
{
    private int loginIndex;

    //? if >= 1.20.6 {
    /*@Override
    public Type<? extends CustomPacketPayload> type() {
        return new Type<>(id());
    }
    *///?}

    public void setLoginIndex(int loginIndex) {
        this.loginIndex = loginIndex;
    }

    public int getLoginIndex() {
        return loginIndex;
    }

    @Override
    public int getAsInt() {
        return getLoginIndex();
    }

    public abstract void serverHandle(C2SNetworkContext context);

    public abstract void clientHandle(S2CNetworkContext context);
}
