package org.bread_experts_group.breadlib.task.data

import net.minecraft.core.RegistrySetBuilder
import org.bread_experts_group.breadlib.data.DataGenerator
import org.bread_experts_group.breadlib.task.Task

class GenerateDataTask(private val modID: String) : Task() {
	private val generators: MutableMap<String, MutableList<DataGenerator>> = mutableMapOf()
	private val setBuilders: MutableMap<String, (RegistrySetBuilder) -> Unit> = mutableMapOf()

	fun addGenerator(generator: DataGenerator) {
		val list = generators.getOrPut(generator.modID) { mutableListOf() }
		list.add(generator)
	}

	// todo block loot and recipes need the registry builder, look into integrating this into the data generators
	//  maybe pass it as an arg into the data task for generators to pull from?
	//  Also this is only like this cause fabric provides it's own builder, for some reason.
	fun addRegistrySetBuilder(builder: (RegistrySetBuilder) -> Unit) {
		setBuilders[modID] = builder
	}

	fun getGenerators(): Collection<DataGenerator> = this.generators[modID].orEmpty()
	fun getSetBuilder(): ((RegistrySetBuilder) -> Unit)? = this.setBuilders[modID]
}