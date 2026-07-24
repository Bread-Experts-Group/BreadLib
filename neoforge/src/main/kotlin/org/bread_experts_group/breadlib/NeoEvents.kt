package org.bread_experts_group.breadlib

import net.minecraft.client.Minecraft
import net.minecraft.client.gui.LayeredDraw
import net.minecraft.resources.ResourceLocation
import net.neoforged.bus.api.Event
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.client.event.*
import net.neoforged.neoforge.client.gui.VanillaGuiLayers
import net.neoforged.neoforge.common.NeoForge
import net.neoforged.neoforge.event.RegisterCommandsEvent
import net.neoforged.neoforge.event.tick.ServerTickEvent
import org.bread_experts_group.breadlib.task.FireSide
import org.bread_experts_group.breadlib.task.TaskManager
import org.bread_experts_group.breadlib.task.command.ClientCommandTask
import org.bread_experts_group.breadlib.task.command.ServerCommandTask
import org.bread_experts_group.breadlib.task.input.KeyboardTask
import org.bread_experts_group.breadlib.task.input.MouseTasks
import org.bread_experts_group.breadlib.task.render.LayeredDrawTask
import org.bread_experts_group.breadlib.task.render.LevelRenderTask
import org.bread_experts_group.breadlib.task.render.RenderLevelStage
import org.bread_experts_group.breadlib.task.tick.ClientTickTask
import org.bread_experts_group.breadlib.task.tick.ServerTickTask
import java.util.function.Consumer

object NeoEvents {
	private fun <T : Event> addListener(task: Consumer<T>): Unit = NeoForge.EVENT_BUS.addListener(task)

	private fun getRenderLevelStage(stage: RenderLevelStageEvent.Stage): RenderLevelStage =
		when (stage) {
			RenderLevelStageEvent.Stage.AFTER_SKY -> RenderLevelStage.AFTER_SKY
			RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS -> RenderLevelStage.AFTER_SOLID_BLOCKS
			RenderLevelStageEvent.Stage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS -> RenderLevelStage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS
			RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS -> RenderLevelStage.AFTER_CUTOUT_BLOCKS
			RenderLevelStageEvent.Stage.AFTER_ENTITIES -> RenderLevelStage.AFTER_ENTITIES
			RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES -> RenderLevelStage.AFTER_BLOCK_ENTITIES
			RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS -> RenderLevelStage.AFTER_TRANSLUCENT_BLOCKS
			RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS -> RenderLevelStage.AFTER_TRIPWIRE_BLOCKS
			RenderLevelStageEvent.Stage.AFTER_PARTICLES -> RenderLevelStage.AFTER_PARTICLES
			RenderLevelStageEvent.Stage.AFTER_WEATHER -> RenderLevelStage.AFTER_WEATHER
			RenderLevelStageEvent.Stage.AFTER_LEVEL -> RenderLevelStage.AFTER_LEVEL
			else -> throw IllegalStateException("Failed to map NeoForge specific RenderLevelStageEvent.Stage: $stage")
		}

	fun registerEvents(eventBus: IEventBus) {
		this.addRLSETask()
		this.addMouseScrollTask()
		this.addMouseButtonTasks()
		this.addKeyboardTasks()
		this.addClientTickTasks()
		this.addServerTickTasks()
		this.addLayeredDrawTask(eventBus)
		this.addCommandTasks()
	}

	private fun addRLSETask() {
		this.addListener { event: RenderLevelStageEvent ->
			TaskManager.runTasks(
				LevelRenderTask(
					this.getRenderLevelStage(event.stage),
					event.levelRenderer,
					event.poseStack,
					event.projectionMatrix,
					event.partialTick,
					event.camera,
					event.frustum
				)
			)
		}
	}

	private fun addMouseScrollTask() {
		this.addListener { event: InputEvent.MouseScrollingEvent ->
			if (TaskManager.runTasks(
					MouseTasks.Scroll(
						Minecraft.getInstance().mouseHandler,
						event.scrollDeltaX,
						event.scrollDeltaY
					)
				).isCanceled
			) event.setCanceled(true)
		}
	}

	private fun addMouseButtonTasks() {
		this.addListener { event: InputEvent.MouseButton.Pre ->
			if (TaskManager.runTasks(
					MouseTasks.Button(
						Minecraft.getInstance().mouseHandler,
						event.button,
						event.action,
						event.modifiers,
						FireSide.PRE
					)
				).isCanceled
			) event.setCanceled(true)
		}
		this.addListener { event: InputEvent.MouseButton.Post ->
			TaskManager.runTasks(
				MouseTasks.Button(
					Minecraft.getInstance().mouseHandler,
					event.button,
					event.action,
					event.modifiers,
					FireSide.POST
				)
			)
		}
	}

	private fun addKeyboardTasks() {
		this.addListener { event: InputEvent.Key ->
			TaskManager.runTasks(
				KeyboardTask(event.key, event.scanCode, event.action, event.modifiers)
			)
		}
	}

	private fun addClientTickTasks() {
		val level = Minecraft.getInstance().level ?: return
		this.addListener { _: ClientTickEvent.Pre ->
			TaskManager.runTasks(ClientTickTask(level, FireSide.PRE))
		}
		this.addListener { _: ClientTickEvent.Post ->
			TaskManager.runTasks(ClientTickTask(level, FireSide.POST))
		}
	}

	private fun addServerTickTasks() {
		this.addListener { event: ServerTickEvent.Pre ->
			TaskManager.runTasks(ServerTickTask(event.server.overworld(), FireSide.PRE))
		}
		this.addListener { event: ServerTickEvent.Post ->
			TaskManager.runTasks(ServerTickTask(event.server.overworld(), FireSide.POST))
		}
	}

	private fun addLayeredDrawTask(eventBus: IEventBus) {
		eventBus.addListener { event: RegisterGuiLayersEvent ->
			val task = TaskManager.runTasks(LayeredDrawTask())
			task.layers.forEach { (location: ResourceLocation, layer: LayeredDraw.Layer) ->
				event.registerAbove(VanillaGuiLayers.DEBUG_OVERLAY, location, layer)
			}
		}
	}

	private fun addCommandTasks() {
		this.addListener { event: RegisterClientCommandsEvent ->
			TaskManager.runTasks(ClientCommandTask(event.dispatcher, event.buildContext))
		}
		this.addListener { event: RegisterCommandsEvent ->
			TaskManager.runTasks(ServerCommandTask(event.dispatcher, event.buildContext))
		}
	}
}