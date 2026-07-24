package org.bread_experts_group.breadlib;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;
import org.bread_experts_group.breadlib.network.payload.PayloadInfo;
import org.bread_experts_group.breadlib.task.TaskManager;
import org.bread_experts_group.breadlib.task.network.NetworkTask;

public class ForgeNetworking {
	public static SimpleChannel NETWORK_CHANNEL;

	public static void checkChannelNotNull() {
		if (NETWORK_CHANNEL == null) throw new NullPointerException(
				"Breadlib's network channel was not initialized before accessing, this shouldn't be possible!"
		);
	}

	@SuppressWarnings({"rawtypes", "unchecked"})
	public static void setup() {
		NetworkTask task = TaskManager.runTasks(new NetworkTask());

		NETWORK_CHANNEL = ChannelBuilder
				.named("breadlib_network")
				.networkProtocolVersion(1)
				.simpleChannel()
				.play((ctx) -> {
					for (PayloadInfo info : task.serverboundPayloads()) {
						ctx.serverbound().add(info.packetClass, info.streamCodec, (payload, context) ->
								info.handler.handle((CustomPacketPayload) payload, context.getSender())
						);
					}
					for (PayloadInfo info : task.clientboundPayloads()) {
						ctx.clientbound().add(info.packetClass, info.streamCodec, (payload, context) ->
								info.handler.handle((CustomPacketPayload) payload, Minecraft.getInstance().player)
						);
					}
				}).play().bidirectional().build();
	}
}