package org.bread_experts_group.breadlib.platform

import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.neoforged.neoforge.network.PacketDistributor

class NeoForgeNetworkHelper : INetworkHelper {
	override fun sendToServer(payload: CustomPacketPayload) {
		PacketDistributor.sendToServer(payload)
	}

	override fun sendToAllPlayers(payload: CustomPacketPayload, level: ServerLevel) {
		PacketDistributor.sendToAllPlayers(payload)
	}

	override fun sendToPlayersTrackingChunk(payload: CustomPacketPayload, level: ServerLevel, pos: ChunkPos) {
		PacketDistributor.sendToPlayersTrackingChunk(level, pos, payload)
	}

	override fun sendToPlayersInDimension(payload: CustomPacketPayload, level: ServerLevel) {
		PacketDistributor.sendToPlayersInDimension(level, payload)
	}
}