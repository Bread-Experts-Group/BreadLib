package org.bread_experts_group.breadlib.task.input;

import org.bread_experts_group.breadlib.task.Task;

public class KeyboardTask extends Task {
	public final int button;
	public final int scanCode;
	public final int action;
	public final int modifiers;

	public KeyboardTask(int button, int scanCode, int action, int modifiers) {
		this.button = button;
		this.scanCode = scanCode;
		this.action = action;
		this.modifiers = modifiers;
	}
}
