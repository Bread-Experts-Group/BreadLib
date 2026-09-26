package org.bread_experts_group.breadlib.platform

import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import net.minecraft.core.RegistrySetBuilder
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.FabricHelper

class BreadLibDataGeneratorEntry : DataGeneratorEntrypoint {
	private var setBuilder: ((RegistrySetBuilder) -> Unit)? = null

	override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
		val pack = fabricDataGenerator.createPack()

//		TODO: figure out what htis is pack.addProvider(::BreadLibWorldGenProvider)
		setBuilder = FabricHelper.runDataGenerator(pack, BreadLib.MOD_ID).getSetBuilder()
	}

	override fun buildRegistry(registryBuilder: RegistrySetBuilder) {
		setBuilder?.invoke(registryBuilder)
	}
}