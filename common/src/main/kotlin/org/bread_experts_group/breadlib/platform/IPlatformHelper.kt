package org.bread_experts_group.breadlib.platform

import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import java.nio.file.Path

interface IPlatformHelper {
	/**
	 * Gets the side of the current platform environment.
	 * @return The side of the current platform environment.
	 * @see ApplicationSide
	 */
	fun getSide(): ApplicationSide

	/**
	 * Gets the config directory for the current platform.
	 * @return The path of the mod configuration directory
	 */
	fun getConfigDir(): Path

	fun getGameDir(): Path

	/**
	 * Gets the name of the current platform.
	 * @return The name of the current platform
	 */
	fun getPlatformName(): String

	/**
	 * Checks if a mod with the given id is loaded.
	 * @param modId The mod to check if it is loaded.
	 * @return True if the mod is loaded, false otherwise.
	 */
	fun isModLoaded(modId: String): Boolean

	fun getModInfo(modId: String): ModInfo

	/**
	 * Gets the kind of environment this platform represents.
	 * @return The environment kind.
	 * @see EnvironmentKind
	 */
	fun getEnvironmentKind(): EnvironmentKind

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
