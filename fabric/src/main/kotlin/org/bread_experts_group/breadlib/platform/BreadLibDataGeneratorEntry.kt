package org.bread_experts_group.breadlib.platform

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.core.RegistrySetBuilder
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.FabricHelper
import org.bread_experts_group.breadlib.task.TaskManager
import org.bread_experts_group.breadlib.task.data.RegistrySetBuilderTask

class BreadLibDataGeneratorEntry : DataGeneratorEntrypoint {
	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
		val pack = fabricDataGenerator.createPack()

//		TODO: figure out what htis is pack.addProvider(::BreadLibWorldGenProvider)
		FabricHelper.runDataGenerator(pack, BreadLib.MOD_ID, fabricDataGenerator.registries)
	}

	override fun buildRegistry(registryBuilder: RegistrySetBuilder) {
		TaskManager.runTasks(RegistrySetBuilderTask()).supplier(BreadLib.MOD_ID)?.invoke(registryBuilder)
	}
}