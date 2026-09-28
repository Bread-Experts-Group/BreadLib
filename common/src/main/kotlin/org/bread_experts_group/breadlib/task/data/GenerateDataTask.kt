package org.bread_experts_group.breadlib.task.data

import net.minecraft.core.HolderLookup
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import org.bread_experts_group.breadlib.data.DataGenerationProvider
import org.bread_experts_group.breadlib.task.Task
import java.util.concurrent.CompletableFuture

class GenerateDataTask(
	val registries: CompletableFuture<HolderLookup.Provider>,
	val packOutput: PackOutput
) : Task() {
	private val providers: MutableMap<String, MutableList<DataProvider>> = mutableMapOf()

	fun addProvider(provider: DataProvider, modID: String) {
		val list = providers.getOrPut(modID) { mutableListOf() }
		list.add(provider)
	}

	fun addProvider(provider: DataGenerationProvider) {
		this.addProvider(provider, provider.modID)
	}

	fun getProviders(modID: String): Collection<DataProvider> = this.providers[modID].orEmpty()
}