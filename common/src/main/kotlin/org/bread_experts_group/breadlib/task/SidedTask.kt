package org.bread_experts_group.breadlib.task

open class SidedTask(val side: FireSide) : Task() {
	val isPre: Boolean
		get() = this.side == FireSide.PRE

	val isPost: Boolean
		get() = this.side == FireSide.POST
}
