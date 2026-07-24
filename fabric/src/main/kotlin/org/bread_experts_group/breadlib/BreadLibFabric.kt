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
		fun <T> registerContent(provider: RegistryProvider<T>) {
			provider.entries().forEach { (key, value) ->
				Registry.registerForHolder<T>(provider.registry, key.name, value.get())
				key.bind()
			}
		}

		fun registerContent() {
			for (provider in RegistryProvider.providers) Companion.registerContent(provider)
		}
	}
}