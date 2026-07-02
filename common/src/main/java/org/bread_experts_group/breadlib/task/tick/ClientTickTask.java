package org.bread_experts_group.breadlib.task.tick;

import net.minecraft.client.multiplayer.ClientLevel;
import org.bread_experts_group.breadlib.task.Task;

public abstract class ClientTickTask extends Task {
	public ClientLevel level;

	public ClientTickTask(ClientLevel level) {
		this.level = level;
	}

	public static class Pre extends ClientTickTask {
		public Pre(ClientLevel level) {
			super(level);
		}
	}

	public static class Post extends ClientTickTask {
		public Post(ClientLevel level) {
			super(level);
		}
	}
}
