package com.arc_studio.brick_lib_api.core.network.type;


import com.arc_studio.brick_lib_api.BrickLibAPI;
import com.arc_studio.brick_lib_api.core.network.PacketContent;

//? if >= 1.20.4 {
/*import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
*///?} else {
//? if fabric {
//?}
//?}

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

interface IHandleablePacket
        //? if >=1.20.4 {
         /*extends CustomPacketPayload
        *///?}
{
    void encoder(PacketContent content);

    //? if =1.20.4 {
    /*/^@Override^/
    default void write(FriendlyByteBuf arg){
        encoder(new PacketContent(arg));
    }
    *///?}


    @ApiStatus.Internal
    default PacketContent getEncodedPacketContent(PacketContent content) {
        encoder(content);
        return content;
    }

    //? if = 1.20.4 {
    /*@Override
    *///?}
    default ResourceLocation id() {
        return BrickLibAPI.ofPath(this.getClass().getName().replace(".", "_").toLowerCase());

    }

}
