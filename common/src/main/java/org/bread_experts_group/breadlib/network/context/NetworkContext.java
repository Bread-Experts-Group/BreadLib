package org.bread_experts_group.breadlib.network.context;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface NetworkContext {
	Player player();

	default Level level() {
		return player().level();
	}
}
