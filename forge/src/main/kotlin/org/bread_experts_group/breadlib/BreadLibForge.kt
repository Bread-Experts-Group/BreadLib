package org.bread_experts_group.breadlib

import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.EventPriority
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.RegisterEvent
import org.bread_experts_group.breadlib.BreadLib.init
import org.bread_experts_group.breadlib.ForgeEvents.registerEvents
import org.bread_experts_group.breadlib.platform.ForgeGenerateDataTask
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.registry.objects.RegistryObject
import org.bread_experts_group.breadlib.task.TaskManager
import java.util.function.Supplier

@Mod(BreadLib.MOD_ID)
class BreadLibForge(context: FMLJavaModLoadingContext) {
	init {
		val eventBus = context.modEventBus
		eventBus.addListener { event: GatherDataEvent -> TaskManager.runTasks(ForgeGenerateDataTask(event)) }

		BreadLib.LOGGER.info("Hello Forge world!")
		init()
		registerContent(eventBus)
		registerEvents(eventBus)
		ForgeNetworking.setup()
	}

	companion object {
		fun <T> registerContent(provider: RegistryProvider<T>, event: RegisterEvent) {
			event.register<T>(
				provider.key
			) { helper: RegisterEvent.RegisterHelper<T> ->
				provider.entries.forEach { (key: RegistryObject<T, out T>, value: Supplier<T>) ->
					helper.register(key.name, value.get())
					key.bind()
				}
			}
			provider.freeze()
		}

		fun registerContent(eventBus: IEventBus) {
			eventBus.addListener(
				EventPriority.NORMAL,
				false,
				RegisterEvent::class.java
			) { event: RegisterEvent ->
				for ((_, registries) in RegistryProvider.providers) {
					for ((_, registry) in registries) registerContent(registry, event)
				}
			}
		}
	}
}
