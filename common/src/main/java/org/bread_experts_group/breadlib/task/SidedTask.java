package org.bread_experts_group.breadlib.task;

public class SidedTask extends Task {
	private final FireSide fireSide;

	public SidedTask(FireSide fireSide) {
		this.fireSide = fireSide;
	}

	public boolean isPre() {
		return this.fireSide == FireSide.PRE;
	}

	public boolean isPost() {
		return this.fireSide == FireSide.POST;
	}

	public FireSide getSide() {
		return this.fireSide;
	}
}
