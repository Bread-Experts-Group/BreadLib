package org.bread_experts_group.breadlib;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import org.bread_experts_group.breadlib.task.FireSide;
import org.bread_experts_group.breadlib.task.TaskManager;
import org.bread_experts_group.breadlib.task.input.MouseTasks;
import org.bread_experts_group.breadlib.task.render.LevelRenderTask;
import org.bread_experts_group.breadlib.task.render.RenderLevelStage;
import org.bread_experts_group.breadlib.task.tick.ClientTickTask;
import org.bread_experts_group.breadlib.task.tick.ServerTickTask;

import java.util.function.Consumer;

import static org.bread_experts_group.breadlib.task.render.RenderLevelStage.*;

public class ForgeEvents {
	private static <T extends Event> void addListener(Class<T> eventClass, Consumer<T> task) {
		MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, eventClass, task);
	}

	private static RenderLevelStage getRenderLevelStage(RenderLevelStageEvent.Stage stage) {
		if (stage == RenderLevelStageEvent.Stage.AFTER_SKY) {
			return AFTER_SKY;
		} else if (stage == RenderLevelStageEvent.Stage.AFTER_SOLID_BLOCKS) {
			return AFTER_SOLID_BLOCKS;
		} else if (stage == RenderLevelStageEvent.Stage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS) {
			return AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS;
		} else if (stage == RenderLevelStageEvent.Stage.AFTER_CUTOUT_BLOCKS) {
			return AFTER_CUTOUT_BLOCKS;
		} else if (stage == RenderLevelStageEvent.Stage.AFTER_ENTITIES) {
			return AFTER_ENTITIES;
		} else if (stage == RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) {
			return AFTER_BLOCK_ENTITIES;
		} else if (stage == RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) {
			return AFTER_TRANSLUCENT_BLOCKS;
		} else if (stage == RenderLevelStageEvent.Stage.AFTER_TRIPWIRE_BLOCKS) {
			return AFTER_TRIPWIRE_BLOCKS;
		} else if (stage == RenderLevelStageEvent.Stage.AFTER_PARTICLES) {
			return AFTER_PARTICLES;
		} else if (stage == RenderLevelStageEvent.Stage.AFTER_WEATHER) {
			return AFTER_WEATHER;
		} else if (stage == RenderLevelStageEvent.Stage.AFTER_LEVEL) {
			return AFTER_LEVEL;
		}

		throw new NullPointerException();
	}

	public static void registerEvents(IEventBus eventBus) {
		addRLSETask();
		addMouseScrollTask();
		addMouseButtonTasks();
		addClientTickTasks();
		addServerTickTasks();
	}

	@SuppressWarnings({"NewExpressionSideOnly", "removal"})
	public static void addRLSETask() {
		addListener(RenderLevelStageEvent.class, (event) -> {
					PoseStack poseStack = new PoseStack();
					poseStack.mulPose(event.getPoseStack());
					TaskManager.runTasks(
							new LevelRenderTask(
									getRenderLevelStage(event.getStage()),
									event.getLevelRenderer(),
									poseStack,
									event.getProjectionMatrix(),
									Minecraft.getInstance().getTimer(),
									event.getCamera(),
									event.getFrustum()
							));
				}
		);
	}

	public static void addMouseScrollTask() {
		addListener(InputEvent.MouseScrollingEvent.class, event -> {
			if (TaskManager.runTasks(
					new MouseTasks.Scroll(Minecraft.getInstance().mouseHandler, event.getDeltaX(), event.getDeltaY())
			)) event.setCanceled(true);
		});
	}

	public static void addMouseButtonTasks() {
		addListener(InputEvent.MouseButton.Pre.class, event -> {
			if (TaskManager.runTasks(
					new MouseTasks.Button(
							Minecraft.getInstance().mouseHandler,
							event.getButton(),
							event.getAction(),
							event.getModifiers(),
							FireSide.PRE
					))) event.setCanceled(true);
		});
		addListener(InputEvent.MouseButton.Post.class, event -> TaskManager.runTasks(
				new MouseTasks.Button(
						Minecraft.getInstance().mouseHandler,
						event.getButton(),
						event.getAction(),
						event.getModifiers(),
						FireSide.POST
				)
		));
	}

	public static void addClientTickTasks() {
		addListener(TickEvent.ClientTickEvent.Pre.class, event -> TaskManager.runTasks(
				new ClientTickTask(Minecraft.getInstance().level, FireSide.PRE)
		));
		addListener(TickEvent.ClientTickEvent.Post.class, event -> TaskManager.runTasks(
				new ClientTickTask(Minecraft.getInstance().level, FireSide.POST)
		));
	}

	public static void addServerTickTasks() {
		addListener(TickEvent.ServerTickEvent.Pre.class, event -> TaskManager.runTasks(
				new ServerTickTask(event.getServer().overworld(), FireSide.PRE)
		));
		addListener(TickEvent.ServerTickEvent.Post.class, event -> TaskManager.runTasks(
				new ServerTickTask(event.getServer().overworld(), FireSide.POST)
		));
	}
}
