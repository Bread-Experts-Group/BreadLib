package org.bread_experts_group.breadlib.network.payload

import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.world.entity.player.Player
import org.bread_experts_group.breadlib.network.NetworkContext

fun interface PayloadHandler<T : CustomPacketPayload> {
	fun handle(data: T, context: NetworkContext)

	fun handle(data: T, player: Player) {
		this.handle(data, NetworkContext(player))
	}
}
