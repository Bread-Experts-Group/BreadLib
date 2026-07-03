package org.bread_experts_group.breadlib.network.payload;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.bread_experts_group.breadlib.network.NetworkDirection;

public record PayloadInfo <B extends ByteBuf, T extends CustomPacketPayload>(
		Class<T> packetClass,
		PayloadHandler<T> handler,
		CustomPacketPayload.Type<T> type,
		StreamCodec<B, T> streamCodec,
		NetworkDirection bound
) {
}