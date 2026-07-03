package org.bread_experts_group.breadlib.network.context;

import net.minecraft.server.level.ServerPlayer;

public class ServerNetworkContext implements NetworkContext {
	private final ServerPlayer player;

	public ServerNetworkContext(ServerPlayer player) {
		this.player = player;
	}

	@Override
	public ServerPlayer player() {
		return player;
	}
}
