package org.bread_experts_group.breadlib

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.Minecraft
import net.minecraftforge.client.event.AddGuiOverlayLayersEvent
import net.minecraftforge.client.event.InputEvent
import net.minecraftforge.client.event.RegisterClientCommandsEvent
import net.minecraftforge.client.event.RenderLevelStageEvent
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.RegisterCommandsEvent
import net.minecraftforge.event.TickEvent
import net.minecraftforge.eventbus.api.Event
import net.minecraftforge.eventbus.api.IEventBus
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
import org.bread_experts_group.breadlib.util.minecraft
import java.util.function.Consumer

object ForgeEvents {
	private fun <T : Event> addListener(task: Consumer<T>): Unit = MinecraftForge.EVENT_BUS.addListener(task)

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
			else -> throw IllegalStateException("Failed to map Forge specific RenderLevelStageEvent.Stage: $stage")
		}

	@JvmStatic
	fun registerEvents(eventBus: IEventBus) {
		this.addRLSETask()
		this.addKeyboardTasks()
		this.addMouseScrollTask()
		this.addMouseButtonTasks()
		this.addClientTickTasks()
		this.addServerTickTasks()
		this.addLayeredDrawTask(eventBus)
		this.addCommandTasks()
	}

	@Suppress("DEPRECATION", "removal")
	private fun addRLSETask() {
		this.addListener { event: RenderLevelStageEvent ->
			val poseStack = PoseStack()
			poseStack.mulPose(event.poseStack)
			TaskManager.runTasks(
				LevelRenderTask(
					this.getRenderLevelStage(event.stage),
					event.levelRenderer,
					poseStack,
					event.projectionMatrix,
					Minecraft.getInstance().timer,
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
						event.deltaX,
						event.deltaY
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
			TaskManager.runTasks(KeyboardTask(event.key, event.scanCode, event.action, event.modifiers))
		}
	}

	private fun addClientTickTasks() {
		val level = (minecraft ?: return).level ?: return
		this.addListener { _: TickEvent.ClientTickEvent.Pre ->
			TaskManager.runTasks(ClientTickTask(level, FireSide.PRE))
		}
		this.addListener { _: TickEvent.ClientTickEvent.Post ->
			TaskManager.runTasks(ClientTickTask(level, FireSide.PRE))
		}
	}

	private fun addServerTickTasks() {
		this.addListener { event: TickEvent.ServerTickEvent.Pre ->
			TaskManager.runTasks(ServerTickTask(event.server.overworld(), FireSide.PRE))
		}
		this.addListener { event: TickEvent.ServerTickEvent.Post ->
			TaskManager.runTasks(ServerTickTask(event.server.overworld(), FireSide.POST))
		}
	}

	private fun addLayeredDrawTask(eventBus: IEventBus) {
		eventBus.addListener { event: AddGuiOverlayLayersEvent ->
			val task = TaskManager.runTasks(LayeredDrawTask())
			task.layers.forEach(event.layeredDraw::add)
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
