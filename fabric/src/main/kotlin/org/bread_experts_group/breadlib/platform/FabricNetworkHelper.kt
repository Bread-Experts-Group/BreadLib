package org.bread_experts_group.breadlib.platform

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos

class FabricNetworkHelper : INetworkHelper {
	override fun sendToServer(payload: CustomPacketPayload) {
		ClientPlayNetworking.send(payload)
	}

	override fun sendToAllPlayers(payload: CustomPacketPayload, level: ServerLevel) {
		for (player in PlayerLookup.all(level.server)) ServerPlayNetworking.send(player, payload)
	}

	override fun sendToPlayersTrackingChunk(payload: CustomPacketPayload, level: ServerLevel, pos: ChunkPos) {
		for (player in PlayerLookup.tracking(level, pos)) ServerPlayNetworking.send(player, payload)
	}

	override fun sendToPlayersInDimension(payload: CustomPacketPayload, level: ServerLevel) {
		for (player in PlayerLookup.world(level)) ServerPlayNetworking.send(player, payload)
	}
}