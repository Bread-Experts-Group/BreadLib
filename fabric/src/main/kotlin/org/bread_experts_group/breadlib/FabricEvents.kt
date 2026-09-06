package org.bread_experts_group.breadlib

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import com.mojang.brigadier.tree.ArgumentCommandNode
import com.mojang.brigadier.tree.CommandNode
import com.mojang.brigadier.tree.LiteralCommandNode
import com.mojang.brigadier.tree.RootCommandNode
import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.culling.Frustum
import net.minecraft.commands.CommandSourceStack
import net.minecraft.server.level.ServerLevel
import org.bread_experts_group.breadlib.task.FireSide
import org.bread_experts_group.breadlib.task.TaskManager
import org.bread_experts_group.breadlib.task.command.ClientCommandTask
import org.bread_experts_group.breadlib.task.command.ServerCommandTask
import org.bread_experts_group.breadlib.task.render.LevelRenderTask
import org.bread_experts_group.breadlib.task.render.RenderLevelStage
import org.bread_experts_group.breadlib.task.tick.ClientTickTask
import org.bread_experts_group.breadlib.task.tick.ServerTickTask
import org.joml.Matrix4f

object FabricEvents {
	private fun renderWorldEvent(context: WorldRenderContext, vararg stage: RenderLevelStage) {
		val poseStack = context.matrixStack() ?: PoseStack()
		for (currentStage in stage) {
			TaskManager.runTasks(
				LevelRenderTask(
					currentStage,
					context.worldRenderer(),
					poseStack,
					context.projectionMatrix(),
					context.tickCounter(),
					context.camera(),
					context.frustum() ?: Frustum(Matrix4f(), Matrix4f())
				)
			)
		}
	}

	fun registerEvents() {
		val envType = FabricLoader.getInstance().environmentType
		if (envType == EnvType.CLIENT) {
			addWorldRenderTasks()
			addClientTickTasks()
		}
		addServerTickTasks()
		addCommandTasks(envType)
	}

	private fun addWorldRenderTasks() {
		WorldRenderEvents.START.register(WorldRenderEvents.Start { context: WorldRenderContext ->
			renderWorldEvent(
				context,
				RenderLevelStage.AFTER_SKY
			)
		})
		WorldRenderEvents.AFTER_SETUP.register(WorldRenderEvents.AfterSetup { context: WorldRenderContext ->
			renderWorldEvent(
				context,
				RenderLevelStage.AFTER_SKY
			)
		})
		WorldRenderEvents.BEFORE_ENTITIES.register(WorldRenderEvents.BeforeEntities { context: WorldRenderContext ->
			renderWorldEvent(
				context,
				RenderLevelStage.AFTER_SOLID_BLOCKS,
				RenderLevelStage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS,
				RenderLevelStage.AFTER_CUTOUT_BLOCKS
			)
		})
		WorldRenderEvents.AFTER_ENTITIES.register(WorldRenderEvents.AfterEntities { context: WorldRenderContext ->
			renderWorldEvent(
				context, RenderLevelStage.AFTER_ENTITIES, RenderLevelStage.AFTER_BLOCK_ENTITIES
			)
		}
		)
		WorldRenderEvents.AFTER_TRANSLUCENT.register(WorldRenderEvents.AfterTranslucent { context: WorldRenderContext ->
			renderWorldEvent(
				context, RenderLevelStage.AFTER_TRANSLUCENT_BLOCKS, RenderLevelStage.AFTER_TRIPWIRE_BLOCKS
			)
		})
		WorldRenderEvents.LAST.register(WorldRenderEvents.Last { context: WorldRenderContext ->
			renderWorldEvent(
				context,
				RenderLevelStage.AFTER_PARTICLES,
				RenderLevelStage.AFTER_WEATHER
			)
		})
		WorldRenderEvents.END.register(WorldRenderEvents.End { context: WorldRenderContext ->
			renderWorldEvent(context, RenderLevelStage.AFTER_LEVEL)
		})
	}

	private fun addServerTickTasks() {
		ServerTickEvents.START_WORLD_TICK.register(ServerTickEvents.StartWorldTick { level: ServerLevel ->
			TaskManager.runTasks(ServerTickTask(level, FireSide.PRE))
		})
		ServerTickEvents.END_WORLD_TICK.register(ServerTickEvents.EndWorldTick { level: ServerLevel ->
			TaskManager.runTasks(ServerTickTask(level, FireSide.POST))
		})
	}

	private fun addClientTickTasks() {
		ClientTickEvents.START_WORLD_TICK.register(ClientTickEvents.StartWorldTick { level: ClientLevel ->
			TaskManager.runTasks(ClientTickTask(level, FireSide.PRE))
		})
		ClientTickEvents.END_WORLD_TICK.register(ClientTickEvents.EndWorldTick { level: ClientLevel ->
			TaskManager.runTasks(ClientTickTask(level, FireSide.POST))
		})
	}

	private fun parseNodes(node: CommandNode<CommandSourceStack>, builder: LiteralArgumentBuilder<FabricClientCommandSource>) {
		for (node in node.children) {
			when (node) {
				is LiteralCommandNode<CommandSourceStack> -> builder.then(parseLiteralNode(node, builder))
				is ArgumentCommandNode<CommandSourceStack, *> -> builder.then(parseArgumentNode(node, builder))
				is RootCommandNode<CommandSourceStack> -> builder.then(parseRootNode(node, builder))
			}
		}
	}

	private fun addCommandTasks(envType: EnvType) {
		if (envType == EnvType.CLIENT) ClientCommandRegistrationCallback.EVENT.register(
			ClientCommandRegistrationCallback { dispatcher, context ->
				val sourceStackDispatcher = CommandDispatcher<CommandSourceStack>()
				TaskManager.runTasks(ClientCommandTask(sourceStackDispatcher, context))
				val root = sourceStackDispatcher.getRoot()
				val builder = LiteralArgumentBuilder.literal<FabricClientCommandSource>(root.name)
				for (node in root.getChildren()) this.parseNodes(node, builder)
				dispatcher.register(builder)
			}
		)
		else CommandRegistrationCallback.EVENT.register(CommandRegistrationCallback { dispatcher, context, env ->
			TaskManager.runTasks(ServerCommandTask(dispatcher, context))
		})
	}

	// todo i have no idea if this is gonna work, or why fabric has a separate dedicated command source class in the first place.
	private fun parseLiteralNode(
		node: LiteralCommandNode<CommandSourceStack>,
		builder: LiteralArgumentBuilder<FabricClientCommandSource>
	): LiteralCommandNode<FabricClientCommandSource> {
		this.parseNodes(node, builder)
		return node as LiteralCommandNode<FabricClientCommandSource>
	}

	private fun parseArgumentNode(
		node: CommandNode<CommandSourceStack>,
		builder: LiteralArgumentBuilder<FabricClientCommandSource>
	): ArgumentCommandNode<FabricClientCommandSource, *> {
		this.parseNodes(node, builder)
		return node as ArgumentCommandNode<FabricClientCommandSource, *>
	}

	private fun parseRootNode(
		node: RootCommandNode<CommandSourceStack>,
		builder: LiteralArgumentBuilder<FabricClientCommandSource>
	): RootCommandNode<FabricClientCommandSource> {
		this.parseNodes(node, builder)
		return node as RootCommandNode<FabricClientCommandSource>
	}
}