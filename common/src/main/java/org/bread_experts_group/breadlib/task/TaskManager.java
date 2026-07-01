package org.bread_experts_group.breadlib.task;

import org.bread_experts_group.breadlib.util.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Consumer;

public class TaskManager {
	public static final HashMap<Class<?>, ArrayList<Consumer<? extends Task>>> tasks = new HashMap<>();

	public static <T extends Task> void newTask(Class<T> tClass, Consumer<T> task) {
		var list = Util.getOrPut(tasks, tClass, ArrayList::new);
		list.add(task);
	}

	@SuppressWarnings("unchecked")
	public static <T extends Task> void runTasks(T task) {
		ArrayList<Consumer<? extends Task>> list = tasks.get(task.getClass());
		if (list == null || list.isEmpty()) return;
		for (Consumer<? extends Task> consumer : list) ((Consumer<T>) consumer).accept(task);
	}
}