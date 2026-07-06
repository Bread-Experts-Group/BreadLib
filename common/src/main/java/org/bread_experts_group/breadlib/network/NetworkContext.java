package org.bread_experts_group.breadlib.network;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.bread_experts_group.breadlib.platform.ApplicationSide;

public record NetworkContext(Player player) {
	public Level level() {
		return player.level();
	}

	public ServerLevel serverLevel() {
		if (this.getSide() != ApplicationSide.SERVER) throw new IllegalStateException("Current dist is not server.");
		return (ServerLevel) this.level();
	}

	public ClientLevel clientLevel() {
		if (this.getSide() != ApplicationSide.CLIENT) throw new IllegalStateException("Current dist is not client.");
		return (ClientLevel) this.level();
	}

	public ApplicationSide getSide() {
		return level().isClientSide ? ApplicationSide.CLIENT : ApplicationSide.SERVER;
	}
}