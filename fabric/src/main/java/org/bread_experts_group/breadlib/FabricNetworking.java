package org.bread_experts_group.breadlib;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import org.bread_experts_group.breadlib.network.payload.PayloadInfo;
import org.bread_experts_group.breadlib.task.TaskManager;
import org.bread_experts_group.breadlib.task.network.NetworkTask;

public class FabricNetworking {
	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void registerPackets() {
		NetworkTask task = TaskManager.runTasks(new NetworkTask());
		for (PayloadInfo info : task.serverboundPayloads()) {
			PayloadTypeRegistry.playC2S().register(info.type, info.streamCodec);
			ServerPlayNetworking.registerGlobalReceiver(info.type, (payload, context) ->
					info.handler.handle(payload, context.player())
			);
		}
		for (PayloadInfo info : task.clientboundPayloads()) {
			PayloadTypeRegistry.playS2C().register(info.type, info.streamCodec);
			ClientPlayNetworking.registerGlobalReceiver(info.type, (payload, context) ->
					info.handler.handle(payload, context.player())
			);
		}
	}
}
