package org.bread_experts_group.breadlib

import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import net.minecraftforge.registries.RegisterEvent
import org.bread_experts_group.breadlib.BreadLib.init
import org.bread_experts_group.breadlib.ForgeEvents.registerEvents
import org.bread_experts_group.breadlib.platform.ForgeGenerateDataTask
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.task.TaskManager

@Mod(BreadLib.MOD_ID)
class BreadLibForge(context: FMLJavaModLoadingContext) {
	companion object {
		fun <T> registerContent(provider: RegistryProvider<T>, event: RegisterEvent) {
			event.register(provider.key) { helper ->
				provider.entries.forEach { (key, value) ->
					helper.register(key.name, value.get())
					key.bind()
				}
				provider.freeze()
			}
		}

		fun registerContent(eventBus: IEventBus) {
			eventBus.addListener { event: RegisterEvent ->
				for ((_, registries) in RegistryProvider.providers) {
					for ((_, registry) in registries) registerContent(registry, event)
				}
			}
		}
	}

	init {
		val eventBus = context.modEventBus
		eventBus.addListener { event: GatherDataEvent ->
			TaskManager.runTasks(ForgeGenerateDataTask(event))

//			val generator = event.generator
//			val packOutput = generator.packOutput
//			TaskManager.runTasks(BootstrapDatapackEntriesTask()).getSuppliers().forEach { (modID, supplier) ->
//				val builder = RegistrySetBuilder().also { supplier(it) }
//				val provider = DatapackBuiltinEntriesProvider(
//					packOutput,
//					event.lookupProvider,
//					builder,
//					setOf(modID)
//				)
//				generator.addProvider(true, provider)
//			}
		}

		BreadLib.LOGGER.info("Hello Forge world!")
		init()
		registerContent(eventBus)
		registerEvents(eventBus)
		ForgeNetworking.setup()
	}
}
