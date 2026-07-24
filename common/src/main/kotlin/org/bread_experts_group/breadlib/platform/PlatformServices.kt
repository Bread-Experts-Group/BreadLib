package org.bread_experts_group.breadlib.platform

import org.bread_experts_group.breadlib.BreadLib
import java.util.*
import java.util.function.Supplier

object PlatformServices {
	@JvmField
	val PLATFORM: IPlatformHelper = PlatformServices.load(IPlatformHelper::class.java)

	fun <T> load(clazz: Class<T>): T {
		val loadedService = ServiceLoader.load(clazz).findFirst()
			.orElseThrow(Supplier { NullPointerException("Failed to load service for " + clazz.getName()) })
		BreadLib.LOGGER.debug("Loaded {} for service {}", loadedService, clazz)
		return loadedService
	}
}