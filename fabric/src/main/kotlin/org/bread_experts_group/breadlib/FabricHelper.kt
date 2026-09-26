package org.bread_experts_group.breadlib

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.task.TaskManager
import org.bread_experts_group.breadlib.task.data.GenerateDataTask
import java.util.concurrent.CompletableFuture

object FabricHelper {
	private fun <T> registerContent(provider: RegistryProvider<T>) {
		provider.entries.forEach { (key, value) ->
			Registry.registerForHolder(provider.registry, key.name, value.get()!!)
			key.bind()
		}
		provider.freeze()
	}

	fun registerContent(modID: String) {
		for ((_, provider) in RegistryProvider.getProvidersForID(modID))
			registerContent(provider)
	}

	fun runDataGenerator(
		pack: FabricDataGenerator.Pack,
		modID: String,
		registries: CompletableFuture<HolderLookup.Provider>
	): GenerateDataTask {
		val task = TaskManager.runTasks(GenerateDataTask(modID, registries))
		for (generator in task.getGenerators()) {
			pack.addProvider { packOutput ->
				generator.setPackOutput(packOutput)
				generator
			}
		}
		return task
	}
}