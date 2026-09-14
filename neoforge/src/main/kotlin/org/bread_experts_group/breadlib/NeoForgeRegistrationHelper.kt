package org.bread_experts_group.breadlib

import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.registries.RegisterEvent
import org.bread_experts_group.breadlib.registry.RegistryProvider

object NeoForgeRegistrationHelper {
	@Suppress("TYPE_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
	fun <T> registerContent(provider: RegistryProvider<T>, event: RegisterEvent) {
		event.register(provider.key) { helper ->
			provider.entries.forEach { (key, value) ->
				helper.register(key.name, value.get())
				key.bind()
			}
			provider.freeze()
		}
	}

	fun registerContent(eventBus: IEventBus, modID: String) {
		eventBus.addListener { event: RegisterEvent ->
			for ((_, provider) in RegistryProvider.getProvidersForID(modID))
				this.registerContent(provider, event)
		}
	}
}