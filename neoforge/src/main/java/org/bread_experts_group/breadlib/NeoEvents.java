package org.bread_experts_group.breadlib;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.Event;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.common.NeoForge;
import org.bread_experts_group.breadlib.task.TaskManager;
import org.bread_experts_group.breadlib.task.input.MouseTask;
import org.bread_experts_group.breadlib.task.render.LevelRenderTask;
import org.bread_experts_group.breadlib.task.render.RenderLevelStage;

import java.util.function.Consumer;

import static org.bread_experts_group.breadlib.task.render.RenderLevelStage.*;
import static org.bread_experts_group.breadlib.task.render.RenderLevelStage.AFTER_BLOCK_ENTITIES;
import static org.bread_experts_group.breadlib.task.render.RenderLevelStage.AFTER_CUTOUT_BLOCKS;
import static org.bread_experts_group.breadlib.task.render.RenderLevelStage.AFTER_ENTITIES;
import static org.bread_experts_group.breadlib.task.render.RenderLevelStage.AFTER_LEVEL;
import static org.bread_experts_group.breadlib.task.render.RenderLevelStage.AFTER_PARTICLES;
import static org.bread_experts_group.breadlib.task.render.RenderLevelStage.AFTER_TRANSLUCENT_BLOCKS;
import static org.bread_experts_group.breadlib.task.render.RenderLevelStage.AFTER_TRIPWIRE_BLOCKS;
import static org.bread_experts_group.breadlib.task.render.RenderLevelStage.AFTER_WEATHER;

public class NeoEvents {
	private static <T extends Event> void addListener(Class<T> eventClass, Consumer<T> task) {
		NeoForge.EVENT_BUS.addListener(eventClass, task);
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

	public static void registerEvents() {
		addRLSETask();
		addMouseScrollTask();
	}

	public static void addRLSETask() {
		addListener(RenderLevelStageEvent.class, (event) -> TaskManager.runTasks(
				new LevelRenderTask(
						getRenderLevelStage(event.getStage()),
						event.getLevelRenderer(),
						event.getPoseStack(),
						event.getProjectionMatrix(),
						event.getPartialTick(),
						event.getCamera(),
						event.getFrustum()
				))
		);
	}

	public static void addMouseScrollTask() {
		addListener(InputEvent.MouseScrollingEvent.class, event -> TaskManager.runTasks(
				new MouseTask.Scroll(Minecraft.getInstance().mouseHandler, event.getScrollDeltaX(), event.getScrollDeltaY())
		));
	}
}
