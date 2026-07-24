package org.bread_experts_group.breadlib.task

import java.util.function.Consumer

object TaskManager {
	val tasks: HashMap<Class<*>, ArrayList<Consumer<out Task>>> = hashMapOf()

	fun <T : Task> newTask(tClass: Class<T>, task: Consumer<T>) {
		val list = tasks.getOrPut(tClass) { ArrayList() }
		list.add(task)
	}

	/**
	 * Runs all consumers registered to the specified task, then returns the task.
	 */
	@JvmStatic
	@Suppress("UNCHECKED_CAST")
	fun <T : Task> runTasks(task: T): T {
		var list: ArrayList<Consumer<out Task>> = arrayListOf()
		for ((key, value) in tasks) {
			if (key.isAssignableFrom(task.javaClass)) {
				list = value
				break
			}
		}
		if (list.isNotEmpty()) for (consumer in list) (consumer as Consumer<T>).accept(task)
		return task
	}

	inline fun <reified T : Task> newTask(noinline task: (T) -> Unit): Unit =
		TaskManager.newTask(T::class.java, task)
}