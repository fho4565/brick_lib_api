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

import com.google.common.collect.Iterables;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.arc_studio.brick_lib_api.client.command.ClientCommands.argument;
import static com.arc_studio.brick_lib_api.client.command.ClientCommands.literal;

/**
 * This class includes a modified version of Minecraft Fabric API, which is licensed under the Apache License, Version 2.0.
 * Modifications made by fho4565:
 * <ol>
 *  <li>Modified to support cross-version requirements.</li>
 *  </ol>
 */
final class ClientCommandInternals {
	private static final Logger LOGGER = LoggerFactory.getLogger(ClientCommandInternals.class);
    private static @Nullable CommandDispatcher<ClientSuggestionProvider> activeDispatcher;

	public static void setActiveDispatcher(@Nullable CommandDispatcher<ClientSuggestionProvider> dispatcher) {
		ClientCommandInternals.activeDispatcher = dispatcher;
	}

	public static @Nullable CommandDispatcher<ClientSuggestionProvider> getActiveDispatcher() {
		return activeDispatcher;
	}

	/**
	 * 执行客户端命令。
	 * 调用方应确保仅在带有斜杠前缀的消息上调用此函数，并且需要在调用前删除斜杠。
	 * （这与{@code ClientPlayerEntity#sendCommand}的要求相同)
	 *
	 * @param command 命令
	 * @return 如果命令不应发送到服务器，则为 true，否则为 false
	 */
	public static boolean executeCommand(String command) {
		if (command.startsWith("/")) {
			command = command.substring(1);
		}
		Minecraft client = Minecraft.getInstance();

		ClientSuggestionProvider commandSource = client.getConnection().getSuggestionsProvider();

		client.getProfiler().push("[CLIENT COMMAND]"+command);

		try {
            if (activeDispatcher != null) {
                activeDispatcher.execute(command, commandSource);
				return true;
            }else {
				LOGGER.error("Error when executeCommand,Dispatcher is null!");
				return false;
			}
		} catch (CommandSyntaxException e) {
			boolean ignored = isIgnoredException(e.getType());

			if (ignored) {
				LOGGER.debug("Syntax exception for client-sided command '{}'", command, e);
				return false;
			}
			LOGGER.warn("Syntax exception for client-sided command '{}'", command, e);
			ClientCommands.sendError(getErrorMessage(e));
			return true;
		} catch (RuntimeException e) {
			LOGGER.error("Error while executing client-sided command '{}'", command, e);
			ClientCommands.sendError(Component.nullToEmpty(e.getMessage()));
			return true;
		} finally {
			client.getProfiler().pop();
		}
	}

	/**
	 * 是否应忽略具有该类型的命令异常并将命令发送到服务器。
	 *
	 * @param type the exception type
	 * @return true if ignored, false otherwise
	 */
	private static boolean isIgnoredException(CommandExceptionType type) {
		BuiltInExceptionProvider builtins = CommandSyntaxException.BUILT_IN_EXCEPTIONS;
		return type == builtins.dispatcherUnknownCommand() || type == builtins.dispatcherParseException();
	}

	private static Component getErrorMessage(CommandSyntaxException e) {
		Component message = ComponentUtils.fromMessage(e.getRawMessage());
		String context = e.getContext();

		return context != null ? Component.translatable("command.context.parse_error", message, e.getCursor(), context) : message;
	}

	public static void finalizeInit() {
		if(activeDispatcher != null){
			if (!activeDispatcher.getRoot().getChildren().isEmpty()) {
				activeDispatcher.register(
						literal("brick_lib_client")
								.then(literal("help")
										.executes(ClientCommandInternals::executeRootHelp)
										.then(argument("command", StringArgumentType.greedyString())
												.executes(ClientCommandInternals::executeArgumentHelp)))
				);
			}


			activeDispatcher.findAmbiguities((parent, child, sibling, inputs) -> LOGGER.warn("Ambiguity between arguments {} and {} with inputs: {}", activeDispatcher.getPath(child), activeDispatcher.getPath(sibling), inputs));
		}else{
			LOGGER.error("Error when finalizeInit,Dispatcher is null!");
		}
	}

	private static int executeRootHelp(CommandContext<ClientSuggestionProvider> context) {
		return executeHelp(activeDispatcher.getRoot(), context);
	}

	private static int executeArgumentHelp(CommandContext<ClientSuggestionProvider> context) throws CommandSyntaxException {
		ParseResults<ClientSuggestionProvider> parseResults = activeDispatcher.parse(StringArgumentType.getString(context, "command"), context.getSource());
		List<ParsedCommandNode<ClientSuggestionProvider>> nodes = parseResults.getContext().getNodes();

		if (nodes.isEmpty()) {
			throw new SimpleCommandExceptionType(Component.literal("AbstractNode is empty!")).create();
		}

		return executeHelp(Iterables.getLast(nodes).getNode(), context);
	}

	private static int executeHelp(CommandNode<ClientSuggestionProvider> startNode, CommandContext<ClientSuggestionProvider> context) {
		Map<CommandNode<ClientSuggestionProvider>, String> commands = activeDispatcher.getSmartUsage(startNode, context.getSource());

		for (String command : commands.values()) {
			ClientCommands.sendFeedback(Component.literal("/" + command));
		}

		return commands.size();
	}

	public static void addCommands(CommandDispatcher<ClientSuggestionProvider> target, ClientSuggestionProvider source) {
		Map<CommandNode<ClientSuggestionProvider>, CommandNode<ClientSuggestionProvider>> originalToCopy = new HashMap<>();
		originalToCopy.put(activeDispatcher.getRoot(), target.getRoot());
		copyChildren(activeDispatcher.getRoot(), target.getRoot(), source, originalToCopy);
	}

	private static void copyChildren(
			CommandNode<ClientSuggestionProvider> origin,
			CommandNode<ClientSuggestionProvider> target,
			ClientSuggestionProvider source,
			Map<CommandNode<ClientSuggestionProvider>, CommandNode<ClientSuggestionProvider>> originalToCopy
	) {
		for (CommandNode<ClientSuggestionProvider> child : origin.getChildren()) {
			if (!child.canUse(source)) {
                continue;
            }
			ArgumentBuilder<ClientSuggestionProvider, ?> builder = child.createBuilder();
			builder.requires(s -> true);
			if (builder.getCommand() != null) {
				builder.executes(context -> 0);
			}
			if (builder.getRedirect() != null) {
				builder.redirect(originalToCopy.get(builder.getRedirect()));
			}
			CommandNode<ClientSuggestionProvider> result = builder.build();
			originalToCopy.put(child, result);
			target.addChild(result);
			if (!child.getChildren().isEmpty()) {
				copyChildren(child, result, source, originalToCopy);
			}
		}
	}
}
