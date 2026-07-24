package org.bread_experts_group.breadlib.task

import com.mojang.brigadier.Command
import com.mojang.brigadier.context.CommandContext
import net.minecraft.client.Minecraft
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.player.LocalPlayer
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.extensions.item.IKeyboardItem
import org.bread_experts_group.breadlib.extensions.item.IMouseItem
import org.bread_experts_group.breadlib.task.command.ServerCommandTask
import org.bread_experts_group.breadlib.task.input.KeyboardTask
import org.bread_experts_group.breadlib.task.input.MouseTasks

object BreadLibTasks {
	fun setupInputTasks() {
		TaskManager.newTask(MouseTasks.Scroll::class.java) { task: MouseTasks.Scroll ->
			val minecraft = Minecraft.getInstance()
			val player: LocalPlayer = minecraft.player ?: return@newTask
			val heldStack = player.mainHandItem
			val item = heldStack.item as? IMouseItem ?: return@newTask
			if (item.onMouseScroll(heldStack, player.level() as ClientLevel, player)) task.cancel()
		}

		TaskManager.newTask(MouseTasks.Button::class.java) { task: MouseTasks.Button ->
			val minecraft = Minecraft.getInstance()
			val player: LocalPlayer = minecraft.player ?: return@newTask
			val heldStack = player.mainHandItem
			val item = heldStack.item as? IMouseItem ?: return@newTask
			if (task.isPre) {
				if (item.onMouseInputPre(heldStack, player.level() as ClientLevel, player)) task.cancel()
			} else if (task.isPost) item.onMouseInputPost(heldStack, player.level() as ClientLevel, player)
		}

		TaskManager.newTask(KeyboardTask::class.java) { task: KeyboardTask ->
			val minecraft = Minecraft.getInstance()
			val player: LocalPlayer = minecraft.player ?: return@newTask
			val heldStack = player.mainHandItem
			val item = heldStack.item as? IKeyboardItem ?: return@newTask
			item.onKeyPress(
				task.button,
				task.scanCode,
				task.action,
				task.modifiers,
				heldStack,
				player.level() as ClientLevel,
				player
			)
		}
	}

	fun setupCommandTasks() {
		TaskManager.newTask(ServerCommandTask::class.java) { task: ServerCommandTask ->
			task.registerCommand { dispatcher, context ->
				dispatcher.register(
					Commands.literal(BreadLib.MOD_ID)
						.then(
							Commands.literal("test")
								.executes { subContext: CommandContext<CommandSourceStack> ->
									println("test")
									Command.SINGLE_SUCCESS
								}
						)
				)
			}
		}
	}
}