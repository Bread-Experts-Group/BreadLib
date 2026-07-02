package org.bread_experts_group.breadlib.task.tick;

import net.minecraft.server.level.ServerLevel;
import org.bread_experts_group.breadlib.task.Task;

public abstract class ServerTickTask extends Task {
	public ServerLevel level;

	public ServerTickTask(ServerLevel level) {
		this.level = level;
	}

	public static class Pre extends ServerTickTask {
		public Pre(ServerLevel level) {
			super(level);
		}
	}

	public static class Post extends ServerTickTask {
		public Post(ServerLevel level) {
			super(level);
		}
	}
}