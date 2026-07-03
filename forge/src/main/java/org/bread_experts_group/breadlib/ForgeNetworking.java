package org.bread_experts_group.breadlib;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;
import org.bread_experts_group.breadlib.network.context.ClientNetworkContext;
import org.bread_experts_group.breadlib.network.context.ServerNetworkContext;
import org.bread_experts_group.breadlib.network.payload.PayloadInfo;
import org.bread_experts_group.breadlib.task.TaskManager;
import org.bread_experts_group.breadlib.task.network.NetworkTask;

// todo console is reporting unknown errors with our packets, but they still run
public class ForgeNetworking {
	public static SimpleChannel NETWORK_CHANNEL;

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static void setup() {
		NetworkTask task = TaskManager.runTasks(new NetworkTask());

		NETWORK_CHANNEL = ChannelBuilder
				.named("breadlib")
				.networkProtocolVersion(1)
				.simpleChannel()
				.play()
				.serverbound(consumer -> {
					for (PayloadInfo info : task.serverboundPayloads()) {
						consumer.addMain((info.packetClass()), info.streamCodec(), (payload, context) ->
								info.handler().handle((CustomPacketPayload) payload, new ServerNetworkContext(context.getSender()))
						);
					}
				})
				.clientbound(consumer -> {
					for (PayloadInfo info : task.clientboundPayloads()) {
						consumer.addMain(info.packetClass(), info.streamCodec(), ((payload, context) ->
								info.handler().handle((CustomPacketPayload) payload, new ClientNetworkContext(Minecraft.getInstance().player)))
						);
					}
				}).bidirectional().build();
	}
}
