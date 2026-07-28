package org.bread_experts_group.breadlib

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.minecraft.core.Registry
import org.bread_experts_group.breadlib.platform.PlatformInitialization
import org.bread_experts_group.breadlib.registry.RegistryProvider


class BreadLibFabric : ClientModInitializer, ModInitializer {
	companion object {
		@Suppress("TYPE_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
		private fun <T> registerContent(provider: RegistryProvider<T>) {
			provider.entries.forEach { (key, value) ->
				Registry.registerForHolder(provider.registry, key.name, value.get())
				key.bind()
			}
			provider.freeze()
		}

		fun registerContent() {
			for ((_, registries) in RegistryProvider.providers) {
				for ((_, registry) in registries) registerContent(registry)
			}
		}
	}

	override fun onInitialize() {
		BreadLib.LOGGER.info("Hello Fabric world!")
		BreadLib.init()
		registerContent()

		FabricEvents.registerEvents()
		FabricNetworking.registerPackets()

		PlatformInitialization.registerCapabilities(BreadLib.MOD_ID)
	}

	override fun onInitializeClient() {
	}
}