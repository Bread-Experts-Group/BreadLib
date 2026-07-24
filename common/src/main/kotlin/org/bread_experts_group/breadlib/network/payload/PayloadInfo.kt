package org.bread_experts_group.breadlib.network.payload

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import org.bread_experts_group.breadlib.network.NetworkDirection

@JvmRecord
data class PayloadInfo<B : ByteBuf, T : CustomPacketPayload>(
	@JvmField val packetClass: Class<T>,
	@JvmField val handler: PayloadHandler<T>,
	@JvmField val type: CustomPacketPayload.Type<T>,
	@JvmField val streamCodec: StreamCodec<B, T>,
	@JvmField val bound: NetworkDirection
) 