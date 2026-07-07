package org.bread_experts_group.breadlib;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderContext;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import org.bread_experts_group.breadlib.network.payload.PayloadInfo;
import org.bread_experts_group.breadlib.task.FireSide;
import org.bread_experts_group.breadlib.task.TaskManager;
import org.bread_experts_group.breadlib.task.command.ClientCommandTask;
import org.bread_experts_group.breadlib.task.command.ServerCommandTask;
import org.bread_experts_group.breadlib.task.network.NetworkTask;
import org.bread_experts_group.breadlib.task.render.LevelRenderTask;
import org.bread_experts_group.breadlib.task.render.RenderLevelStage;
import org.bread_experts_group.breadlib.task.tick.ClientTickTask;
import org.bread_experts_group.breadlib.task.tick.ServerTickTask;

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
		EnvType envType = FabricLoader.getInstance().getEnvironmentType();
		if (envType == EnvType.CLIENT) {
			addWorldRenderTasks();
			addClientTickTasks();
		}
		addServerTickTasks();
		addPacketsTask(envType);
		addCommandTasks(envType);
	}

	private static void addWorldRenderTasks() {
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

	private static void addServerTickTasks() {
		ServerTickEvents.START_WORLD_TICK.register(level ->
				TaskManager.runTasks(new ServerTickTask(level, FireSide.PRE))
		);
		ServerTickEvents.END_WORLD_TICK.register(level ->
				TaskManager.runTasks(new ServerTickTask(level, FireSide.POST))
		);
	}

	private static void addClientTickTasks() {
		ClientTickEvents.START_WORLD_TICK.register(level ->
				TaskManager.runTasks(new ClientTickTask(level, FireSide.PRE))
		);
		ClientTickEvents.END_WORLD_TICK.register(level ->
				TaskManager.runTasks(new ClientTickTask(level, FireSide.POST))
		);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	private static void addPacketsTask(EnvType envType) {
		NetworkTask task = TaskManager.runTasks(new NetworkTask());
		for (PayloadInfo info : task.serverboundPayloads()) {
			PayloadTypeRegistry.playC2S().register(info.type(), info.streamCodec());
			ServerPlayNetworking.registerGlobalReceiver(info.type(), (payload, context) ->
					info.handler().handle(payload, context.player())
			);
		}
		if (envType == EnvType.CLIENT) for (PayloadInfo info : task.clientboundPayloads()) {
			PayloadTypeRegistry.playS2C().register(info.type(), info.streamCodec());
			ClientPlayNetworking.registerGlobalReceiver(info.type(), (payload, context) ->
				info.handler().handle(payload, context.player())
			);
		}
	}

	// todo just doesn't work
	private static void addCommandTasks(EnvType envType) {
		if (envType == EnvType.CLIENT) ClientCommandRegistrationCallback.EVENT.register((dispatcher, context) ->
				// todo why does fabric use it's own source for client commands???
				TaskManager.runTasks(new ClientCommandTask((CommandDispatcher) dispatcher, context))
		);
		else CommandRegistrationCallback.EVENT.register((dispatcher, context, env) ->
				TaskManager.runTasks(new ServerCommandTask(dispatcher, context))
		);
	}
}
