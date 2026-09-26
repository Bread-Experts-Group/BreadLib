package org.bread_experts_group.breadlib.task.data

import net.minecraft.core.RegistrySetBuilder
import org.bread_experts_group.breadlib.task.Task

class RegistrySetBuilderTask(private val modID: String) : Task() {
	private var suppliers: MutableMap<String, ((RegistrySetBuilder) -> Unit)> = mutableMapOf()

	fun addBuilder(modID: String, builder: (RegistrySetBuilder) -> Unit) {
		suppliers[modID] = builder
	}

	fun supplier(): ((RegistrySetBuilder) -> Unit)? = suppliers[modID]
}