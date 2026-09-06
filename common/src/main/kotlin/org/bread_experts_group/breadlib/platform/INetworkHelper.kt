package org.bread_experts_group.breadlib.platform

import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos

interface INetworkHelper {
	/**
	 * Sends a packet to the server.
	 * @param payload The payload to be sent
	 */
	fun sendToServer(payload: CustomPacketPayload)

	/**
	 * Sends a packet to all players on the server.
	 * @param payload The payload to be sent
	 * @param level The level to get the minecraft server
	 */
	fun sendToAllPlayers(payload: CustomPacketPayload, level: ServerLevel)

	/**
	 * Sends a packet to all players tracking the specified ChunkPos.
	 * @param payload The payload to be sent
	 * @param level The current dimension to get the chunk
	 * @param pos The chunk position
	 */
	fun sendToPlayersTrackingChunk(payload: CustomPacketPayload, level: ServerLevel, pos: ChunkPos)

	/**
	 * Sends a packet to all players in the specified dimension
	 * @param payload The payload to be sent
	 * @param level The current dimension
	 */
	fun sendToPlayersInDimension(payload: CustomPacketPayload, level: ServerLevel)
}