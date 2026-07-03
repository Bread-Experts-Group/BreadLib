package org.bread_experts_group.breadlib;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ClientTickEvent;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.event.RenderLevelStageEvent;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.bread_experts_group.breadlib.network.context.ServerNetworkContext;
import org.bread_experts_group.breadlib.network.payload.PayloadInfo;
import org.bread_experts_group.breadlib.task.FireSide;
import org.bread_experts_group.breadlib.task.TaskManager;
import org.bread_experts_group.breadlib.task.input.MouseTasks;
import org.bread_experts_group.breadlib.task.network.NetworkTask;
import org.bread_experts_group.breadlib.task.render.LayeredDrawTask;
import org.bread_experts_group.breadlib.task.render.LevelRenderTask;
import org.bread_experts_group.breadlib.task.render.RenderLevelStage;
import org.bread_experts_group.breadlib.task.tick.ClientTickTask;
import org.bread_experts_group.breadlib.task.tick.ServerTickTask;

import java.util.function.Consumer;

import static org.bread_experts_group.breadlib.task.render.RenderLevelStage.*;

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

		throw new ClassCastException("Failed to map NeoForge specific RenderLevelStageEvent.Stage: " + stage);
	}

	public static void registerEvents(IEventBus eventBus) {
		addRLSETask();
		addMouseScrollTask();
		addMouseButtonTasks();
		addClientTickTasks();
		addServerTickTasks();
		addLayeredDrawTask(eventBus);
		addNetworkTasks(eventBus);
	}

	private static void addRLSETask() {
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

	private static void addMouseScrollTask() {
		addListener(InputEvent.MouseScrollingEvent.class, event -> {
			if (TaskManager.runTasks(
					new MouseTasks.Scroll(Minecraft.getInstance().mouseHandler, event.getScrollDeltaX(), event.getScrollDeltaY())
			).isCanceled()) event.setCanceled(true);
		});
	}

	private static void addMouseButtonTasks() {
		addListener(InputEvent.MouseButton.Pre.class, event -> {
			if (TaskManager.runTasks(
					new MouseTasks.Button(
							Minecraft.getInstance().mouseHandler,
							event.getButton(),
							event.getAction(),
							event.getModifiers(),
							FireSide.PRE
					)
			).isCanceled()) event.setCanceled(true);
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

	private static void addClientTickTasks() {
		addListener(ClientTickEvent.Pre.class, event -> TaskManager.runTasks(
				new ClientTickTask(Minecraft.getInstance().level, FireSide.PRE)
		));
		addListener(ClientTickEvent.Post.class, event -> TaskManager.runTasks(
				new ClientTickTask(Minecraft.getInstance().level, FireSide.POST)
		));
	}

	private static void addServerTickTasks() {
		addListener(ServerTickEvent.Pre.class, event -> TaskManager.runTasks(
				new ServerTickTask(event.getServer().overworld(), FireSide.PRE)
		));
		addListener(ServerTickEvent.Post.class, event -> TaskManager.runTasks(
				new ServerTickTask(event.getServer().overworld(), FireSide.POST)
		));
	}

	private static void addLayeredDrawTask(IEventBus eventBus) {
		eventBus.addListener(RegisterGuiLayersEvent.class, event -> {
			LayeredDrawTask task = TaskManager.runTasks(new LayeredDrawTask());
			task.getLayers().forEach((location, layer) ->
					event.registerAbove(VanillaGuiLayers.DEBUG_OVERLAY, location, layer)
			);
		});
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	private static void addNetworkTasks(IEventBus eventBus) {
		eventBus.addListener(RegisterPayloadHandlersEvent.class, event -> {
			NetworkTask task = TaskManager.runTasks(new NetworkTask());
			PayloadRegistrar registrar = event.registrar("1.4.0");

			for (PayloadInfo info : task.serverboundPayloads()) {
				registrar.playToServer(
						info.type(),
						info.streamCodec(),
						(payload, context) -> {
							info.handler().handle(payload, new ServerNetworkContext((ServerPlayer) context.player()));
				});
			}
		});
	}
}