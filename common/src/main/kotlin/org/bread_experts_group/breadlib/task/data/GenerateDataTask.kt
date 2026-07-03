package org.bread_experts_group.breadlib.task.data

import org.bread_experts_group.breadlib.data.DataGenerator
import org.bread_experts_group.breadlib.task.Task

abstract class GenerateDataTask : Task() {
	abstract fun addGenerator(generator: DataGenerator)
}