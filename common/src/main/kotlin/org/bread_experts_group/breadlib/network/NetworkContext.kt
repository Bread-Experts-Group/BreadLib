package org.bread_experts_group.breadlib.network

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import org.bread_experts_group.breadlib.platform.ApplicationSide

data class NetworkContext(val player: Player) {
	val side: ApplicationSide
		get() = if (level().isClientSide) ApplicationSide.CLIENT else ApplicationSide.SERVER

	fun level(): Level {
		return player.level()
	}

	fun serverLevel(): ServerLevel {
		check(this.side == ApplicationSide.SERVER) { "Current dist is not server." }
		return this.level() as ServerLevel
	}

	fun clientLevel(): ClientLevel {
		check(this.side == ApplicationSide.CLIENT) { "Current dist is not client." }
		return this.level() as ClientLevel
	}
}