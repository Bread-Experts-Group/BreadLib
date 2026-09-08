package org.bread_experts_group.breadlib.platform

import net.minecraft.client.Minecraft
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos

abstract class NetworkHelper {
	/**
	 * Gets the [Minecraft] instance if the caller is running on a client.
	 * @return [Minecraft], if the caller is running on [ApplicationSide.CLIENT] (logical).
	 */
	val client: Minecraft
		get() = trueClient.get() ?: throw IllegalStateException("Not running on ${ApplicationSide.CLIENT}.")

	/**
	 * Gets the [MinecraftServer] instance if the caller is running on a server.
	 * @return The server, if the caller is running on [ApplicationSide.SERVER] (logical or physical).
	 */
	val server: MinecraftServer
		get() = trueServer.get() ?: throw IllegalStateException("Not running on ${ApplicationSide.SERVER}.")

	internal val trueSide: ThreadLocal<ApplicationSide?> = ThreadLocal.withInitial { null }
	internal val trueClient: ThreadLocal<Minecraft?> = ThreadLocal.withInitial { null }
	internal val trueServer: ThreadLocal<MinecraftServer?> = ThreadLocal.withInitial { null }

	/**
	 * Gets the side of the current network environment.
	 * @return The side of the current network environment.
	 * @see ApplicationSide
	 */
	val side: ApplicationSide
		get() = trueSide.get() ?: throw IllegalStateException("Side not initialized... network/level might not be setup")

	/**
	 * Sends a packet to the server.
	 * @param payload The payload to be sent
	 */
	abstract fun sendToServer(payload: CustomPacketPayload)

	/**
	 * Sends a packet to all players on the server.
	 * @param payload The payload to be sent
	 */
	abstract fun sendToAllPlayers(payload: CustomPacketPayload)

	/**
	 * Sends a packet to all players tracking the specified ChunkPos.
	 * @param payload The payload to be sent
	 * @param level The current dimension to get the chunk
	 * @param pos The chunk position
	 */
	abstract fun sendToPlayersTrackingChunk(payload: CustomPacketPayload, level: ServerLevel, pos: ChunkPos)

	/**
	 * Sends a packet to all players in the specified dimension
	 * @param payload The payload to be sent
	 * @param level The current dimension
	 */
	abstract fun sendToPlayersInDimension(payload: CustomPacketPayload, level: ServerLevel)
}