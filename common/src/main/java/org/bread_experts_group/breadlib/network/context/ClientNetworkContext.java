package org.bread_experts_group.breadlib.network.context;

import net.minecraft.client.player.LocalPlayer;

public class ClientNetworkContext implements NetworkContext {
	private final LocalPlayer player;

	public ClientNetworkContext(LocalPlayer player) {
		this.player = player;
	}

	@Override
	public LocalPlayer player() {
		return player;
	}
}
