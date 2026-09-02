package org.bread_experts_group.breadlib.task.data

import net.minecraft.core.RegistrySetBuilder
import org.bread_experts_group.breadlib.task.Task

// todo i think it'll be better to just have implementing mods do their own bootstrap stuff, and we just provide helper methods for them
//  (the loaders don't seem to support bootstrapping data for other mods while this one adds the tasks for them)
class BootstrapDatapackEntriesTask : Task() {
	private val suppliers: MutableMap<String, (RegistrySetBuilder) -> Unit> = mutableMapOf()

	fun addBuilder(modID: String, supplier: (RegistrySetBuilder) -> Unit) {
		require(this.suppliers.putIfAbsent(modID, supplier) == null) { "Supplier for $modID already registered." }
	}

	fun getSuppliers(): Map<String, (RegistrySetBuilder) -> Unit> = this.suppliers
}