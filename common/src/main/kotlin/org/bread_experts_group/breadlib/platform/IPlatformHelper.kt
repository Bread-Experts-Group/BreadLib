package org.bread_experts_group.breadlib.platform

import net.minecraft.core.Direction
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.entity.BlockEntity
import org.bread_experts_group.breadlib.capability.base.BlockCapability
import org.bread_experts_group.breadlib.capability.base.Capability
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlockEntity
import java.nio.file.Path

interface IPlatformHelper {
	/**
	 * Gets the side of the current platform environment.
	 * @return The side of the current platform environment.
	 * @see ApplicationSide
	 */
	val side: ApplicationSide

	/**
	 * Gets the config directory for the current platform.
	 * @return The path of the mod configuration directory
	 */
	val configDir: Path

	val gameDir: Path

	/**
	 * Gets the name of the current platform.
	 * @return The name of the current platform
	 */
	val platformName: String

	/**
	 * Gets the kind of environment this platform represents.
	 * @return The environment kind.
	 * @see EnvironmentKind
	 */
	val environmentKind: EnvironmentKind

	/**
	 * Checks if a mod with the given id is loaded.
	 * @param modId The mod to check if it is loaded.
	 * @return True if the mod is loaded, false otherwise.
	 */
	fun isModLoaded(modId: String): Boolean

	fun getModInfo(modId: String): ModInfo

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

	fun capabilitiesChanged(blockEntity: BreadLibBlockEntity)

	fun <C : Capability<*>> capability(blockEntity: BlockEntity, side: Direction? = null, clazz: Class<C>): C?

	fun <T : Capability<*>> installCapabilityConverter(forC: Class<T>, to: (BlockEntity, Direction?) -> T?)
}

inline fun <reified C : Capability<*>> BlockEntity.capability(side: Direction? = null): C? {
	return PlatformServices.PLATFORM.capability(this, side, C::class.java)
}