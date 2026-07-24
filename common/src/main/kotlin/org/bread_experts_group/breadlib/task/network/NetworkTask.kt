package org.bread_experts_group.breadlib.task.network

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import org.bread_experts_group.breadlib.network.NetworkDirection
import org.bread_experts_group.breadlib.network.payload.PayloadHandler
import org.bread_experts_group.breadlib.network.payload.PayloadInfo
import org.bread_experts_group.breadlib.task.Task

class NetworkTask : Task() {
	private val payloads: MutableList<PayloadInfo<*, *>> = mutableListOf()

	private fun <B : ByteBuf, T : CustomPacketPayload> add(
		packetClass: Class<T>,
		bound: NetworkDirection,
		type: CustomPacketPayload.Type<T>,
		streamCodec: StreamCodec<B, T>,
		handler: PayloadHandler<T>
	) {
		payloads.add(PayloadInfo(packetClass, handler, type, streamCodec, bound))
	}

	fun <B : ByteBuf, T : CustomPacketPayload> addServerbound(
		packetClass: Class<T>,
		type: CustomPacketPayload.Type<T>,
		streamCodec: StreamCodec<B, T>,
		handler: PayloadHandler<T>
	) {
		this.add(packetClass, NetworkDirection.SERVER_BOUND, type, streamCodec, handler)
	}

	fun <B : ByteBuf, T : CustomPacketPayload> addClientbound(
		packetClass: Class<T>,
		type: CustomPacketPayload.Type<T>,
		streamCodec: StreamCodec<B, T>,
		handler: PayloadHandler<T>
	) = this.add(packetClass, NetworkDirection.CLIENT_BOUND, type, streamCodec, handler)

	fun clientboundPayloads(): Collection<PayloadInfo<*, out CustomPacketPayload>> =
		payloads.filter { (_, _, _, _, bound) -> bound == NetworkDirection.CLIENT_BOUND }

	fun serverboundPayloads(): Collection<PayloadInfo<*, out CustomPacketPayload>> =
		payloads.filter { (_, _, _, _, bound) -> bound == NetworkDirection.SERVER_BOUND }
}
