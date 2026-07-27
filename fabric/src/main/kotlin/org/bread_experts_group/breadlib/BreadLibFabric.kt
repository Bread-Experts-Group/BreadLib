package org.bread_experts_group.breadlib

import net.fabricmc.api.ModInitializer
import net.minecraft.core.Registry
import org.bread_experts_group.breadlib.registry.RegistryProvider

class BreadLibFabric : ModInitializer {
	override fun onInitialize() {
		BreadLib.LOGGER.info("Hello Fabric world!")
		BreadLib.init()
		registerContent()

		FabricEvents.registerEvents()
		FabricNetworking.registerPackets()
	}

	companion object {
		private fun <T> registerContent(provider: RegistryProvider<T>) {
			if (provider.frozen == null) return
			provider.entries.forEach { (key, value) ->
				Registry.registerForHolder<T>(provider.registry, key.name, value.get())
				key.bind()
			}
		}

		fun registerContent() {
			for ((_, registries) in RegistryProvider.providers) {
				for ((_, registry) in registries) registerContent(registry)
			}
		}
	}
}