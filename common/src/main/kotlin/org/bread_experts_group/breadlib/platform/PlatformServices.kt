package org.bread_experts_group.breadlib.platform

import org.bread_experts_group.breadlib.BreadLib
import java.util.*

object PlatformServices {
	@JvmField
	val PLATFORM: IPlatformHelper = load(IPlatformHelper::class.java)
	val NETWORK: NetworkHelper = load(NetworkHelper::class.java)
	val CAPABILITY: ICapabilityHelper = load(ICapabilityHelper::class.java)

	private fun <T> load(clazz: Class<T>): T {
		val loadedService = ServiceLoader.load(clazz).findFirst()
			.orElseThrow { NullPointerException("Failed to load service for " + clazz.getName()) }
		BreadLib.LOGGER.debug("Loaded {} for service {}", loadedService, clazz)
		return loadedService
	}
}