package org.bread_experts_group.breadlib.platform

import net.neoforged.neoforge.data.event.GatherDataEvent
import org.bread_experts_group.breadlib.data.DataGenerator
import org.bread_experts_group.breadlib.task.data.GenerateDataTask

internal class NeoForgeGenerateDataTask(private val event: GatherDataEvent) : GenerateDataTask() {
	override fun addGenerator(generator: DataGenerator) {
		if (
			(event.includeClient() && generator.generateForClient) ||
			(event.includeServer() && generator.generateForServer)
		) event.createProvider {
			generator.setPackOutput(event.generator.packOutput)
			generator
		}
	}
}