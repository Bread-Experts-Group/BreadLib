package org.bread_experts_group.breadlib.test.server

import org.bread_experts_group.breadlib.task.TaskManager
import org.bread_experts_group.breadlib.task.network.NetworkTask
import org.bread_experts_group.breadlib.test.ServerboundPacketTest

object TasksServerTest {
	fun networkTest() {
		TaskManager.newTask { task: NetworkTask ->
			task.addServerbound(
				ServerboundPacketTest::class.java,
				ServerboundPacketTest.TYPE,
				ServerboundPacketTest.STREAM_CODEC,
				ServerboundPacketTest::handleServerbound
			)
		}
	}
}
