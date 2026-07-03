package org.bread_experts_group.breadlib.network.payload;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.bread_experts_group.breadlib.network.context.NetworkContext;

public interface PayloadHandler<T extends CustomPacketPayload> {
	void handle(T data, NetworkContext context);
}
