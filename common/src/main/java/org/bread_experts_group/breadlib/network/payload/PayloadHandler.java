package org.bread_experts_group.breadlib.network.payload;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.bread_experts_group.breadlib.network.NetworkContext;

public interface PayloadHandler<T extends CustomPacketPayload> {
	void handle(T data, NetworkContext context);

	default void handle(T data, Player player) {
		this.handle(data, new NetworkContext(player));
	}
}
