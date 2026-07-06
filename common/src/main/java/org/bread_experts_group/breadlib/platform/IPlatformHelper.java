package org.bread_experts_group.breadlib.platform;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;

import java.nio.file.Path;

public interface IPlatformHelper {
	/**
	 * Gets the side of the current platform environment.
	 * @return The side of the current platform environment.
	 * @see ApplicationSide
	 */
	ApplicationSide getSide();

	/**
	 * Gets the config directory for the current platform.
	 * @return The path of the mod configuration directory
	 */
	Path getConfigDir();

	/**
	 * Gets the name of the current platform.
	 * @return The name of the current platform
	 */
	String getPlatformName();

	/**
	 * Checks if a mod with the given id is loaded.
	 * @param modId The mod to check if it is loaded.
	 * @return True if the mod is loaded, false otherwise.
	 */
	boolean isModLoaded(String modId);

	/**
	 * Gets the kind of environment this platform represents.
	 * @return The environment kind.
	 * @see EnvironmentKind
	 */
	EnvironmentKind getEnvironmentKind();

	/**
	 * Sends a packet to the server.
	 * @param payload The payload to be sent
	 */
	void sendToServer(CustomPacketPayload payload);

	/**
	 * Sends a packet to all players on the server.
	 * @param payload The payload to be sent
	 * @param level The level to get the minecraft server
	 */
	void sendToAllPlayers(CustomPacketPayload payload, ServerLevel level);

	/**
	 * Sends a packet to all players tracking the specified ChunkPos.
	 * @param payload The payload to be sent
	 * @param level The current dimension to get the chunk
	 * @param pos The chunk position
	 */
	void sendToPlayersTrackingChunk(CustomPacketPayload payload, ServerLevel level, ChunkPos pos);

	/**
	 * Sends a packet to all players in the specified dimension
	 * @param payload The payload to be sent
	 * @param level The current dimension
	 */
	void sendToPlayersInDimension(CustomPacketPayload payload, ServerLevel level);
}
