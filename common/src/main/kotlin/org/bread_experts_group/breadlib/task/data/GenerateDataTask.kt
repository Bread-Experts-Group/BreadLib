package org.bread_experts_group.breadlib.task.data

import net.minecraft.core.HolderLookup
import org.bread_experts_group.breadlib.data.DataGenerator
import org.bread_experts_group.breadlib.task.Task
import java.util.concurrent.CompletableFuture

class GenerateDataTask(private val modID: String, val registries: CompletableFuture<HolderLookup.Provider>) : Task() {
	private val generators: MutableMap<String, MutableList<DataGenerator>> = mutableMapOf()

	fun addGenerator(generator: DataGenerator) {
		val list = generators.getOrPut(generator.modID) { mutableListOf() }
		list.add(generator)
	}

	fun getGenerators(): Collection<DataGenerator> = this.generators[modID].orEmpty()
}