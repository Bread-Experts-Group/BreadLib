package org.bread_experts_group.breadlib.task.command;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import org.bread_experts_group.breadlib.task.Task;

import java.util.function.BiFunction;

public class ServerCommandTask extends Task {
	private final CommandDispatcher<CommandSourceStack> dispatcher;
	private final CommandBuildContext context;

	public ServerCommandTask(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context) {
		this.dispatcher = dispatcher;
		this.context = context;
	}

	public void registerCommand(BiFunction<CommandDispatcher<CommandSourceStack>, CommandBuildContext, Object> function) {
		function.apply(this.dispatcher, this.context);
	}
}
