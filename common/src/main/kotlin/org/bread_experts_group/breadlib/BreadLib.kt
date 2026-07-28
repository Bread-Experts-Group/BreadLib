package org.bread_experts_group.breadlib

import net.minecraft.resources.ResourceLocation
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.bread_experts_group.breadlib.platform.PlatformServices

object BreadLib {
	const val MOD_ID: String = "breadlib"
	const val MOD_VERSION: String = "1.2.0"

	@JvmField
	val LOGGER: Logger = LogManager.getLogger("BreadLib")

	@JvmStatic
	fun modLoc(vararg path: String): ResourceLocation {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, path.joinToString("/"))
	}

	@JvmStatic
	fun init() {
		LOGGER.info(
			"Hello from Common init on {}! we are currently in a {} environment on the {}!",
			PlatformServices.PLATFORM.platformName,
			PlatformServices.PLATFORM.environmentKind,
			PlatformServices.PLATFORM.side
		)
		kExample()

		LOGGER.info(PlatformServices.PLATFORM.getModInfo("breadlib").hash)
	}
}