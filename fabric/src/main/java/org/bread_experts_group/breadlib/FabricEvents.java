package org.bread_experts_group.breadlib;

import com.mojang.blaze3d.vertex.PoseStack;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import org.bread_experts_group.breadlib.task.TaskManager;
import org.bread_experts_group.breadlib.task.render.LevelRenderTask;
import org.bread_experts_group.breadlib.task.render.RenderLevelStage;

public class FabricEvents {
	private static void renderWorldEvent(WorldRenderContext context, RenderLevelStage... stage) {
		PoseStack poseStack = context.matrixStack() != null ? context.matrixStack() : new PoseStack();
		for (RenderLevelStage currentStage : stage) {
			TaskManager.runTasks(new LevelRenderTask(
					currentStage,
					context.worldRenderer(),
					poseStack,
					context.projectionMatrix(),
					context.tickCounter(),
					context.camera(),
					context.frustum()
			));
		}
	}

	public static void registerEvents() {
		addWorldRenderTasks();
	}

	public static void addWorldRenderTasks() {
		WorldRenderEvents.START.register(context ->
				renderWorldEvent(context, RenderLevelStage.AFTER_SKY)
		);
		WorldRenderEvents.AFTER_SETUP.register(context ->
				renderWorldEvent(context, RenderLevelStage.AFTER_SKY)
		);
		WorldRenderEvents.BEFORE_ENTITIES.register(context -> renderWorldEvent(
				context,
				RenderLevelStage.AFTER_SOLID_BLOCKS,
				RenderLevelStage.AFTER_CUTOUT_MIPPED_BLOCKS_BLOCKS,
				RenderLevelStage.AFTER_CUTOUT_BLOCKS)
		);
		WorldRenderEvents.AFTER_ENTITIES.register(context -> renderWorldEvent(
				context, RenderLevelStage.AFTER_ENTITIES, RenderLevelStage.AFTER_BLOCK_ENTITIES)
		);
		WorldRenderEvents.AFTER_TRANSLUCENT.register(context -> renderWorldEvent(
				context, RenderLevelStage.AFTER_TRANSLUCENT_BLOCKS, RenderLevelStage.AFTER_TRIPWIRE_BLOCKS)
		);
		WorldRenderEvents.LAST.register(context ->
				renderWorldEvent(context, RenderLevelStage.AFTER_PARTICLES, RenderLevelStage.AFTER_WEATHER)
		);
		WorldRenderEvents.END.register(context ->
				renderWorldEvent(context, RenderLevelStage.AFTER_LEVEL)
		);
	}
}
