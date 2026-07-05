package org.bread_experts_group.breadlib.platform.services;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import org.bread_experts_group.breadlib.platform.ApplicationSide;
import org.bread_experts_group.breadlib.platform.EnvironmentKind;

public interface IPlatformHelper {
	/**
	 * Gets the side of the current platform environment.
	 * @return The side of the current platform environment.
	 * @see ApplicationSide
	 */
	ApplicationSide getSide();

	/**
	 * Gets the name of the current platform
	 *
	 * @return The name of the current platform.
	 */
	String getPlatformName();

	/**
	 * Checks if a mod with the given id is loaded.
	 *
	 * @param modId The mod to check if it is loaded.
	 * @return True if the mod is loaded, false otherwise.
	 */
	boolean isModLoaded(String modId);

	/**
	 * Gets the kind of environment this platform represents.
	 *
	 * @return The environment kind.
	 * @see EnvironmentKind
	 */
	EnvironmentKind getEnvironmentKind();

	// todo expand to include players tracking chunks instead of sending to all players at once
	<T extends CustomPacketPayload> void sendServerboundPacket(T payload);
	<T extends CustomPacketPayload> void sendClientboundPacket(T payload, ServerLevel level);
}
