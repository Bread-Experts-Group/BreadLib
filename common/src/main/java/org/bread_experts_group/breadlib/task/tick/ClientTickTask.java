package org.bread_experts_group.breadlib.task.tick;

import net.minecraft.client.multiplayer.ClientLevel;
import org.bread_experts_group.breadlib.task.FireSide;
import org.bread_experts_group.breadlib.task.SidedTask;

public class ClientTickTask extends SidedTask {
	public final ClientLevel level;

	public ClientTickTask(ClientLevel level, FireSide side) {
		super(side);
		this.level = level;
	}
}
