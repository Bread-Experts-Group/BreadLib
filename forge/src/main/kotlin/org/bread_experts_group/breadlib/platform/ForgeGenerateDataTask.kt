package org.bread_experts_group.breadlib.platform

import net.minecraftforge.data.event.GatherDataEvent
import org.bread_experts_group.breadlib.data.DataGenerator
import org.bread_experts_group.breadlib.task.data.GenerateDataTask

internal class ForgeGenerateDataTask(private val event: GatherDataEvent) : GenerateDataTask() {
	override fun addGenerator(generator: DataGenerator) {
		event.generator.addProvider(
			(event.includeClient() && generator.generateForClient) ||
					(event.includeServer() && generator.generateForServer)
		) {
			generator.setPackOutput(event.generator.packOutput)
			generator
		}
	}
}