package org.bread_experts_group.breadlib.test.server;

import org.bread_experts_group.breadlib.task.TaskManager;
import org.bread_experts_group.breadlib.task.network.NetworkTask;
import org.bread_experts_group.breadlib.test.ServerboundPacketTest;

public class TasksServerTest {
	public static void networkTest() {
		TaskManager.newTask(NetworkTask.class, task -> task.addServerbound(
				ServerboundPacketTest.class,
				ServerboundPacketTest.TYPE,
				ServerboundPacketTest.STREAM_CODEC,
				ServerboundPacketTest::handleServerbound
		));
	}
}
