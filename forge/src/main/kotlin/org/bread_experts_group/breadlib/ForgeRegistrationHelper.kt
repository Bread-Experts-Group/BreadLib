package org.bread_experts_group.breadlib

import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.RegisterEvent
import org.bread_experts_group.breadlib.registry.RegistryProvider

object ForgeRegistrationHelper {
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
				registerContent(provider, event)
		}
	}
}