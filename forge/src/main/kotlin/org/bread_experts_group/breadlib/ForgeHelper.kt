package org.bread_experts_group.breadlib

import net.minecraft.core.RegistrySetBuilder
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider
import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.IEventBus
import net.minecraftforge.registries.RegisterEvent
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.task.TaskManager
import org.bread_experts_group.breadlib.task.data.GenerateDataTask
import org.bread_experts_group.breadlib.task.data.RegistrySetBuilderTask

object ForgeHelper {
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

	fun runDataGenerator(eventBus: IEventBus, modID: String) {
		eventBus.addListener { event: GatherDataEvent ->
			val dataGenerator = event.generator
			val packOutput = dataGenerator.packOutput

			val lookupProvider = TaskManager.runTasks(RegistrySetBuilderTask(modID)).supplier()?.let {
				val builder = RegistrySetBuilder().also { builder -> it.invoke(builder) }
				val provider = DatapackBuiltinEntriesProvider(
					packOutput, event.lookupProvider, builder, setOf(modID)
				)
				dataGenerator.addProvider(true, provider)
			}?.registryProvider ?: event.lookupProvider

			val task = TaskManager.runTasks(GenerateDataTask(modID, lookupProvider))
			for (generator in task.getGenerators()) {
				generator.setPackOutput(packOutput)
				dataGenerator.addProvider(true, generator)
			}
		}
	}
}