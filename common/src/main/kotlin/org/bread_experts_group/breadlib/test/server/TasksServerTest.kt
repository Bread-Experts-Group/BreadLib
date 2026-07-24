package org.bread_experts_group.breadlib.test.server

import io.netty.buffer.ByteBuf
import org.bread_experts_group.breadlib.network.NetworkContext
import org.bread_experts_group.breadlib.network.payload.PayloadHandler
import org.bread_experts_group.breadlib.task.TaskManager
import org.bread_experts_group.breadlib.task.TaskManager.newTask
import org.bread_experts_group.breadlib.task.network.NetworkTask
import org.bread_experts_group.breadlib.test.ServerboundPacketTest
import java.util.function.Consumer

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
