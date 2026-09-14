package org.bread_experts_group.breadlib

import net.minecraft.core.Registry
import org.bread_experts_group.breadlib.registry.RegistryProvider

object FabricRegistrationHelper {
	@Suppress("TYPE_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
	private fun <T> registerContent(provider: RegistryProvider<T>) {
		provider.entries.forEach { (key, value) ->
			Registry.registerForHolder(provider.registry, key.name, value.get())
			key.bind()
		}
		provider.freeze()
	}

	fun registerContent(modID: String) {
		for ((_, provider) in RegistryProvider.getProvidersForID(modID))
			registerContent(provider)
	}
}