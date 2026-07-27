package org.bread_experts_group.breadlib.task.command

import com.mojang.brigadier.CommandDispatcher
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import org.bread_experts_group.breadlib.task.Task

class ClientCommandTask(
	private val dispatcher: CommandDispatcher<CommandSourceStack>,
	private val context: CommandBuildContext
) : Task() {
	fun registerCommand(function: (CommandDispatcher<CommandSourceStack>, CommandBuildContext) -> Unit) {
		function(this.dispatcher, this.context)
	}
}
