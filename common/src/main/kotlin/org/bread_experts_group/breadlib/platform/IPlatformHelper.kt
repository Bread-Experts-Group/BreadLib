package org.bread_experts_group.breadlib.platform

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
}