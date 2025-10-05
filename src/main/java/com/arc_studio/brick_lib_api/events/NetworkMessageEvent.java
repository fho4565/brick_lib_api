package com.arc_studio.brick_lib_api.events;

import com.arc_studio.brick_lib_api.core.event.BaseEvent;
import com.arc_studio.brick_lib_api.core.event.ICancelableEvent;
import com.arc_studio.brick_lib_api.core.network.BrickNetwork;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collection;

/**
 * 每当发生涉及调用{@link BrickNetwork}的快速发送方法时，都会触发NetworkMessageEvent 。
 *
 * @author fho4565*/
public abstract class NetworkMessageEvent extends BaseEvent {
    protected final String id;
    protected String message;

    public NetworkMessageEvent(String id,String message) {
        this.id = id;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }


    public static class ClientSend extends NetworkMessageEvent implements ICancelableEvent {
        public ClientSend(String id,String message) {
            super(id,message);
        }
    }


    public static class ServerSend extends NetworkMessageEvent implements ICancelableEvent {
        private final Collection<ServerPlayer> targets;

        public ServerSend(String id, String message, Collection<ServerPlayer> targets) {
            super(id,message);
            this.targets = targets;
        }

        public Collection<ServerPlayer> getTargets() {
            return targets;
        }
    }

    /**
     * 客户端接收事件
     */
    public static class ClientReceive extends NetworkMessageEvent {
        public ClientReceive(String id,String message) {
            super(id,message);
        }
    }

    /**
     * 服务端接收事件
     */
    public static class ServerReceive extends NetworkMessageEvent {
        private final ServerPlayer sender;
        public ServerReceive(String id, String message, ServerPlayer sender) {
            super(id,message);
            this.sender = sender;
        }

        public ServerPlayer getSender() {
            return sender;
        }
    }
}
