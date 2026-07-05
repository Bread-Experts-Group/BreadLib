package org.bread_experts_group.breadlib.platform

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator
import org.bread_experts_group.breadlib.data.DataGenerator
import org.bread_experts_group.breadlib.task.data.GenerateDataTask

internal class FabricGenerateDataTask(private val pack: FabricDataGenerator.Pack) : GenerateDataTask() {
	override fun addGenerator(generator: DataGenerator) {
		pack.addProvider { packOutput ->
			generator.setPackOutput(packOutput)
			generator
		}
	}
}