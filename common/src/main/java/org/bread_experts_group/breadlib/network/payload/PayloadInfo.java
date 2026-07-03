package org.bread_experts_group.breadlib.network.payload;

import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.bread_experts_group.breadlib.network.NetworkDirection;

public record PayloadInfo<T extends CustomPacketPayload>(
		PayloadHandler<?> handler,
		CustomPacketPayload.Type<T> type,
		StreamCodec<?, T> streamCodec,
		NetworkDirection bound
) {
}