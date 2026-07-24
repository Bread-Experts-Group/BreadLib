package org.bread_experts_group.breadlib;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.bread_experts_group.breadlib.network.payload.PayloadInfo;
import org.bread_experts_group.breadlib.task.TaskManager;
import org.bread_experts_group.breadlib.task.network.NetworkTask;

public class NeoForgeNetworking {
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void registerPackets(IEventBus eventBus) {
		eventBus.addListener((RegisterPayloadHandlersEvent event) -> {
			NetworkTask task = TaskManager.runTasks(new NetworkTask());
			PayloadRegistrar registrar = event.registrar("1.4.0");

			for (PayloadInfo info : task.serverboundPayloads()) {
				registrar.playToServer(info.type, info.streamCodec, (payload, context) ->
						info.handler.handle(payload, context.player())
				);
			}
			for (PayloadInfo info : task.clientboundPayloads()) {
				registrar.playToClient(info.type, info.streamCodec, (payload, context) ->
						info.handler.handle(payload, context.player())
				);
			}
		});
	}
}
