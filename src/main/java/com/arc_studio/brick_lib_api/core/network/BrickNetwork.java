package com.arc_studio.brick_lib_api.core.network;

import com.arc_studio.brick_lib_api.core.network.type.*;
import com.arc_studio.brick_lib_api.core.network.type.C2SPacket;
import com.arc_studio.brick_lib_api.core.network.type.S2CPacket;
import com.arc_studio.brick_lib_api.Constants;
import com.arc_studio.brick_lib_api.platform.Platform;
import com.arc_studio.brick_lib_api.core.event.BrickEventBus;
import com.arc_studio.brick_lib_api.events.NetworkMessageEvent;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

/**
 * Brick Lib的网络快速操作
 */
public class BrickNetwork {
    /**
     * 先触发{@link NetworkMessageEvent.ServerSend}事件，如果事件没有被取消，则快速发送一个字符串到一些玩家的客户端
     * <p color = "red">你需要保证使用的id参数与其他模组不同，否则可能导致意想不到的事情发生，比如模组间的事件误触！</p>
     */
    public static void sendMessageToPlayer(String id, String message, ServerPlayer... players) {
        if (!BrickEventBus.postEvent(new NetworkMessageEvent.ServerSend(id, message, List.of(players)))) {
            sendToPlayer(new BuiltInPacket(id, message), players);
        }
    }

    /**
     * 先触发{@link NetworkMessageEvent.ServerSend}事件，如果事件没有被取消，则快速发送一个字符串到所有玩家的客户端
     * <p color = "red">你需要保证使用的id参数与其他模组不同，否则可能导致意想不到的事情发生，比如模组间的事件误触！</p>
     */
    public static void sendMessageToAllPlayers(String id, String message) {
        if (!BrickEventBus.postEvent(new NetworkMessageEvent.ServerSend(id, message, Constants.currentServer().getPlayerList().getPlayers()))) {
            sendToAllPlayers(new BuiltInPacket(id, message));
        }
    }

    /**
     * 先触发{@link NetworkMessageEvent.ClientSend}事件，如果事件没有被取消，则快速发送一个字符串到服务端
     * <p color = "red">你需要保证使用的id参数与其他模组不同，否则可能导致意想不到的事情发生，比如模组间的事件误触！</p>
     */
    public static void sendMessageToServer(String id, String message) {
        if (!BrickEventBus.postEvent(new NetworkMessageEvent.ClientSend(id, message))) {
            sendToServer(new BuiltInPacket(id, message));
        }
    }

    /**
     * 发送一个C2S网络包到服务器
     *
     * @param packet 网络包
     * @param <MSG>  网络包具体类型
     * @see C2SPacket
     */
    public static <MSG extends ISHandlePacket> void sendToServer(MSG packet) {
        System.out.println("BrickNetwork.sendToServer");
        Platform.sendToServer(packet);
    }

    /**
     * 发送一个S2C网络包到指定的玩家
     *
     * @param packet        网络包
     * @param serverPlayers 玩家
     * @param <MSG>         网络包具体类型
     * @see S2CPacket
     */
    public static <MSG extends ICHandlePacket> void sendToPlayer(MSG packet, ServerPlayer... serverPlayers) {
        Platform.sendToPlayer(packet, List.of(serverPlayers));
    }


    /**
     * 发送一个在客户端处理的网络包到所有玩家
     *
     * @param packet 网络包
     * @param <MSG>  网络包具体类型
     * @see S2CPacket
     */
    public static <MSG extends ICHandlePacket> void sendToAllPlayers(MSG packet) {
        Platform.sendToAllPlayers(packet);
    }
}
