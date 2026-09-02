package org.bread_experts_group.breadlib.platform

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import org.bread_experts_group.breadlib.task.TaskManager

class BreadLibDataGeneratorEntry : DataGeneratorEntrypoint {
	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
		val pack = fabricDataGenerator.createPack()
		val task = FabricGenerateDataTask(pack)

		pack.addProvider(::BreadLibWorldGenProvider)
		TaskManager.runTasks(task)
	}

//	override fun buildRegistry(registryBuilder: RegistrySetBuilder) {
//		TaskManager.runTasks(BootstrapDatapackEntriesTask())
//	}
//
//	fun registerOtherBuilders(list: MutableList<RegistrySetBuilder>) {
//		TaskManager.runTasks(BootstrapDatapackEntriesTask()).getSuppliers().forEach { (_, supplier) ->
//			list.add(RegistrySetBuilder().also { supplier(it) })
//		}
//	}
}