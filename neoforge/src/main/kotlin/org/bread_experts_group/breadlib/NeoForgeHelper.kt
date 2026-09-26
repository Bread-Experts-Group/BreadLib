package org.bread_experts_group.breadlib

import net.minecraft.core.RegistrySetBuilder
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.neoforged.neoforge.registries.RegisterEvent
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.task.TaskManager
import org.bread_experts_group.breadlib.task.data.GenerateDataTask

object NeoForgeHelper {
	fun <T> registerContent(provider: RegistryProvider<T>, event: RegisterEvent) {
		event.register(provider.key) { helper ->
			provider.entries.forEach { (key, value) ->
				helper.register(key.name, value.get()!!)
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

	fun runDataGenerator(eventBus: IEventBus, modID: String) {
		eventBus.addListener { event: GatherDataEvent ->
			val task = TaskManager.runTasks(GenerateDataTask(modID))
			val dataGenerator = event.generator
			val packOutput = dataGenerator.packOutput
			for (generator in task.getGenerators()) {
				generator.setPackOutput(packOutput)
				dataGenerator.addProvider(true, generator)
			}

			task.getSetBuilder()?.let {
				val builder = RegistrySetBuilder()
				it.invoke(builder)
				val provider = DatapackBuiltinEntriesProvider(
					packOutput, event.lookupProvider, builder, setOf(modID)
				)
				dataGenerator.addProvider(true, provider)
			}
		}
	}
}