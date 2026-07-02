package org.bread_experts_group.breadlib.task.input;

import net.minecraft.client.MouseHandler;
import org.bread_experts_group.breadlib.task.Task;

public abstract class MouseTask extends Task {
	public static class Scroll extends MouseTask {
		public double scrollX;
		public double scrollY;
		public MouseHandler mouseHandler;

		public Scroll(MouseHandler mouseHandler, double scrollX, double scrollY) {
			this.scrollX = scrollX;
			this.scrollY = scrollY;
			this.mouseHandler = mouseHandler;
		}
	}
}