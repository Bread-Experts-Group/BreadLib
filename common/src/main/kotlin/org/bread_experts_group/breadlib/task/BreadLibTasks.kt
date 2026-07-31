package org.bread_experts_group.breadlib.task

import com.mojang.blaze3d.vertex.DefaultVertexFormat
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
import org.bread_experts_group.breadlib.platform.ApplicationSide
import org.bread_experts_group.breadlib.platform.PlatformServices
import org.bread_experts_group.breadlib.rendering.BreadLibRenderTypes
import org.bread_experts_group.breadlib.task.command.ServerCommandTask
import org.bread_experts_group.breadlib.task.input.KeyboardTask
import org.bread_experts_group.breadlib.task.input.MouseTasks
import org.bread_experts_group.breadlib.task.render.ShaderTask

object BreadLibTasks {
	fun setup() {
		when (PlatformServices.PLATFORM.side) {
			ApplicationSide.CLIENT -> {
				this.setupInputs()
				this.setupShaders()
			}
			ApplicationSide.SERVER -> {
				this.setupServerCommands()
			}
		}
	}

	private fun setupInputs() {
		TaskManager.newTask { task: MouseTasks.Scroll ->
			val minecraft = Minecraft.getInstance()
			val player: LocalPlayer = minecraft.player ?: return@newTask
			val heldStack = player.mainHandItem
			val item = heldStack.item as? IMouseItem ?: return@newTask
			if (item.onMouseScroll(heldStack, player.level() as ClientLevel, player)) task.cancel()
		}

		TaskManager.newTask { task: MouseTasks.Button ->
			val minecraft = Minecraft.getInstance()
			val player: LocalPlayer = minecraft.player ?: return@newTask
			val heldStack = player.mainHandItem
			val item = heldStack.item as? IMouseItem ?: return@newTask
			if (task.isPre) {
				if (item.onMouseInputPre(heldStack, player.level() as ClientLevel, player)) task.cancel()
			} else if (task.isPost) item.onMouseInputPost(heldStack, player.level() as ClientLevel, player)
		}

		TaskManager.newTask { task: KeyboardTask ->
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

	private fun setupServerCommands() {
		TaskManager.newTask { task: ServerCommandTask ->
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

	private fun setupShaders() {
		TaskManager.newTask { task: ShaderTask ->
			task.registerShader(
				BreadLib.modLoc("translucent_tex"),
				DefaultVertexFormat.BLOCK
			) { BreadLibRenderTypes.TRANSLUCENT_TEX_INSTANCE = it }
		}
	}
}