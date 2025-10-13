/*
 * Copyright (c) 2016, 2017, 2018, 2019 FabricMC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.arc_studio.brick_lib_api.client.command;

import com.arc_studio.brick_lib_api.core.SideExecutor;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
//? if <= 1.18.2 {
/*import net.minecraft.network.chat.TextComponent;

*///?}
import org.jetbrains.annotations.Nullable;
/**
 * This class includes a modified version of Minecraft Fabric API, which is licensed under the Apache License, Version 2.0.
 * Modifications made by fho4565:
 * <ol>
 *  <li>Modified to support cross-version requirements.</li>
 *  <li>Class name changed to ClientCommands.</li>
 *  </ol>
 */
public final class ClientCommands {
	private ClientCommands() {
	}

    /**
     * 核心命令调度程序，用于注册、解析和执行客户端命令。
     * */
	public static @Nullable CommandDispatcher<ClientSuggestionProvider> getActiveDispatcher() {
		return ClientCommandInternals.getActiveDispatcher();
	}

    /**
     * 一个字面量
     * */
	public static LiteralArgumentBuilder<ClientSuggestionProvider> literal(String name) {
		return LiteralArgumentBuilder.literal(name);
	}
    /**
     * 一个参数
     * */
	public static <T> RequiredArgumentBuilder<ClientSuggestionProvider, T> argument(String name, ArgumentType<T> type) {
		return RequiredArgumentBuilder.argument(name, type);
	}

    /**
     * 发送命令反馈
     * */
	public static void sendFeedback(Component message){
        SideExecutor.runOnClient(() -> () -> {
            Minecraft client = Minecraft.getInstance();
            //? if > 1.18.2 {
            client.gui.getChat().addMessage(message);
            client.getNarrator().sayNow(message);
            //?} else {
            /*client.gui.handleChat(ChatType.SYSTEM,message, Util.NIL_UUID);
            *///?}
        });
	}

    /**
     * 发送错误反馈
     * */
	public static void sendError(Component message){
        SideExecutor.runOnClient(() -> () -> {
            Minecraft client = Minecraft.getInstance();
            //? if > 1.18.2 {
            client.gui.getChat().addMessage(Component.literal("").append(message).withStyle(ChatFormatting.RED));
            client.getNarrator().sayNow(Component.literal("").append(message).withStyle(ChatFormatting.RED));
            //?} else {
            /*client.gui.handleChat(ChatType.SYSTEM,new TextComponent("").append(message).withStyle(ChatFormatting.RED), Util.NIL_UUID);
            *///?}
        });
	}
}
