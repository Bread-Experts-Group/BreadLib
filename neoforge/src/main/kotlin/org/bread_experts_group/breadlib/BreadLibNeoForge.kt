package org.bread_experts_group.breadlib

import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.neoforged.neoforge.registries.RegisterEvent
import org.bread_experts_group.breadlib.BreadLib.init
import org.bread_experts_group.breadlib.platform.NeoForgeGenerateDataTask
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.task.TaskManager

@Mod(BreadLib.MOD_ID)
class BreadLibNeoForge(eventBus: IEventBus) {
	companion object {
		@JvmStatic
		fun <T> registerContent(provider: RegistryProvider<T>, event: RegisterEvent) {
			if (provider.frozen == null) return
			event.register(
				provider.key
			) { helper ->
				provider.entries.forEach { (key, value) ->
					helper.register(key.name, value.get())
					key.bind()
				}
			}
		}

		@JvmStatic
		fun registerContent(eventBus: IEventBus) {
			eventBus.addListener(RegisterEvent::class.java) { event ->
				for ((_, registries) in RegistryProvider.providers) {
					for ((_, registry) in registries) registerContent(registry, event)
				}
			}
		}
	}

	init {
		eventBus.addListener { event: GatherDataEvent -> TaskManager.runTasks(NeoForgeGenerateDataTask(event)) }

		BreadLib.LOGGER.info("Hello NeoForge world!")
		init()
		registerContent(eventBus)
		NeoEvents.registerEvents(eventBus)
		NeoForgeNetworking.registerPackets(eventBus)
	}
}
