package org.bread_experts_group.breadlib.task.tick;

import net.minecraft.server.level.ServerLevel;
import org.bread_experts_group.breadlib.task.FireSide;
import org.bread_experts_group.breadlib.task.SidedTask;

public class ServerTickTask extends SidedTask {
	public ServerLevel level;

	public ServerTickTask(ServerLevel level, FireSide side) {
		super(side);
		this.level = level;
	}
}