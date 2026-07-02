package org.bread_experts_group.breadlib.task;

public class Task {
	private boolean canceled = false;

	public boolean isCanceled() {
		return canceled;
	}

	public void cancel() {
		this.canceled = true;
	}
}