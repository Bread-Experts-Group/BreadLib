package org.bread_experts_group.breadlib.platform

import net.neoforged.neoforge.data.event.GatherDataEvent
import org.bread_experts_group.breadlib.data.DataGenerator
import org.bread_experts_group.breadlib.task.data.GenerateDataTask
import org.bread_experts_group.breadlib.util.info

class NeoForgeGenerateDataTask(private val event: GatherDataEvent) : GenerateDataTask() {
	override fun addGenerator(generator: DataGenerator) {
		info("$generator added for $event")
	}
}