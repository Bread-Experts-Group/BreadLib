package org.bread_experts_group.breadlib.task

open class Task {
	var isCanceled: Boolean = false
		private set

	fun cancel() {
		this.isCanceled = true
	}
}