package org.bread_experts_group.breadlib.task.input;

import net.minecraft.client.MouseHandler;
import org.bread_experts_group.breadlib.task.FireSide;
import org.bread_experts_group.breadlib.task.SidedTask;
import org.bread_experts_group.breadlib.task.Task;

public class MouseTasks {
	public static class Scroll extends Task {
		public double scrollX;
		public double scrollY;
		public MouseHandler mouseHandler;

		public Scroll(MouseHandler mouseHandler, double scrollX, double scrollY) {
			this.scrollX = scrollX;
			this.scrollY = scrollY;
			this.mouseHandler = mouseHandler;
		}
	}

	public static class Button extends SidedTask {
		public int button;
		public int action;
		public int modifiers;
		public MouseHandler mouseHandler;

		public Button(MouseHandler mouseHandler, int button, int action, int modifiers, FireSide side) {
			super(side);
			this.button = button;
			this.action = action;
			this.modifiers = modifiers;
			this.mouseHandler = mouseHandler;
		}
	}
}