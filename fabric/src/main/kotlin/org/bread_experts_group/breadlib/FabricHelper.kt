package org.bread_experts_group.breadlib

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.Util
import net.minecraft.core.HolderLookup
import net.minecraft.core.Registry
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
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
	) {
		pack.addProvider { packOutput ->
			val task = TaskManager.runTasks(GenerateDataTask(registries, packOutput))
			for (provider in task.getProviders(modID)) pack.addProvider { provider }
			object : DataProvider {
				override fun run(output: CachedOutput): CompletableFuture<*> =
					CompletableFuture.runAsync({}, Util.backgroundExecutor())
				override fun getName(): String = "Fabric Dummy Provider"
			}
		}
	}
}