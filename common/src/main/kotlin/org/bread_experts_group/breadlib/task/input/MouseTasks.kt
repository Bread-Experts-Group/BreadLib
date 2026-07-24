package org.bread_experts_group.breadlib.task.input

import net.minecraft.client.MouseHandler
import org.bread_experts_group.breadlib.task.FireSide
import org.bread_experts_group.breadlib.task.SidedTask
import org.bread_experts_group.breadlib.task.Task

class MouseTasks {
	class Scroll(val mouseHandler: MouseHandler, val scrollX: Double, val scrollY: Double) : Task()

	class Button(
		val mouseHandler: MouseHandler,
		val button: Int,
		val action: Int,
		val modifiers: Int,
		side: FireSide
	) : SidedTask(side)
}