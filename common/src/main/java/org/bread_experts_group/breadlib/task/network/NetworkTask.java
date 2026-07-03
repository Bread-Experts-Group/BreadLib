package org.bread_experts_group.breadlib.task.network;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.bread_experts_group.breadlib.network.NetworkDirection;
import org.bread_experts_group.breadlib.network.payload.PayloadHandler;
import org.bread_experts_group.breadlib.network.payload.PayloadInfo;
import org.bread_experts_group.breadlib.task.Task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class NetworkTask extends Task {
	private final List<PayloadInfo<?, ?>> payloads = new ArrayList<>();

	private <B extends ByteBuf, T extends CustomPacketPayload> void add(
			Class<T> packetClass,
			NetworkDirection bound,
			CustomPacketPayload.Type<T> type,
			StreamCodec<B, T> streamCodec,
			PayloadHandler<T> handler
	) {
		payloads.add(new PayloadInfo<>(packetClass, handler, type, streamCodec, bound));
	}

	public <B extends ByteBuf, T extends CustomPacketPayload> void addServerbound(
			Class<T> packetClass,
			CustomPacketPayload.Type<T> type,
			StreamCodec<B, T> streamCodec,
			PayloadHandler<T> handler
	) {
		this.add(packetClass, NetworkDirection.SERVER_BOUND, type, streamCodec, handler);
	}

	public <B extends ByteBuf, T extends CustomPacketPayload> void addClientbound(
			Class<T> packetClass,
			CustomPacketPayload.Type<T> type,
			StreamCodec<B, T> streamCodec,
			PayloadHandler<T> handler
	) {
		this.add(packetClass, NetworkDirection.CLIENT_BOUND, type, streamCodec, handler);
	}

	public Collection<PayloadInfo<?, ?>> clientboundPayloads() {
		return payloads.stream().filter(info -> info.bound() == NetworkDirection.CLIENT_BOUND).toList();
	}

	public Collection<PayloadInfo<?, ?>> serverboundPayloads() {
		return payloads.stream().filter(info -> info.bound() == NetworkDirection.SERVER_BOUND).toList();
	}
}
