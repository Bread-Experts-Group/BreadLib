package org.bread_experts_group.breadlib.task

open class Task(val side: FireSide = FireSide.NONE) {
	var isCanceled: Boolean = false
		private set

	val isPre: Boolean
		get() = this.side == FireSide.PRE || this.side == FireSide.NONE

	val isPost: Boolean
		get() = this.side == FireSide.POST || this.side == FireSide.NONE

	fun cancel() {
		this.isCanceled = true
	}
}