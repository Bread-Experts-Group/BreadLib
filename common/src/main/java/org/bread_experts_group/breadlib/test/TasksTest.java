package org.bread_experts_group.breadlib.test;

import org.bread_experts_group.breadlib.BreadLib;
import org.bread_experts_group.breadlib.task.TaskManager;
import org.bread_experts_group.breadlib.task.input.MouseTask;
import org.bread_experts_group.breadlib.task.render.LevelRenderTask;

public class TasksTest {
	public static void renderTest() {
		TaskManager.newTask(LevelRenderTask.class, task -> BreadLib.LOGGER.info(task.stage));
	}

	public static void mouseTest() {
		TaskManager.newTask(MouseTask.Scroll.class, task -> {
			BreadLib.LOGGER.info("{}, {}, {}", task.mouseHandler, task.scrollX, task.scrollY);
		});
	}
}
