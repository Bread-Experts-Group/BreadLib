package org.bread_experts_group.breadlib.test.client

import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.LayeredDraw
import org.bread_experts_group.breadlib.BreadLib.modLoc
import org.bread_experts_group.breadlib.task.TaskManager.newTask
import org.bread_experts_group.breadlib.task.network.NetworkTask
import org.bread_experts_group.breadlib.task.render.LayeredDrawTask
import org.bread_experts_group.breadlib.test.ClientboundPacketTest
import org.bread_experts_group.breadlib.util.Color

object TasksClientTest {
	fun renderTest() {
//		TaskManager.newTask(LevelRenderTask.class, task -> BreadLib.LOGGER.info(task.stage));
	}

	fun layeredDrawTest() {
		newTask(LayeredDrawTask::class.java) { task: LayeredDrawTask ->
			task.add(
				modLoc("layered_draw", "test_layer"),
				(LayeredDraw.Layer { guiGraphics: GuiGraphics, deltaTracker: DeltaTracker ->
//				Window window = Minecraft.getInstance().getWindow();
					guiGraphics.fill(0, 0, 35, 12, Color.BLACK)
					guiGraphics.drawString(Minecraft.getInstance().font, "Breadlib", 1, 1, Color.WHITE, false)
				})
			)
		}
	}

	fun networkTest() {
		newTask(NetworkTask::class.java) { task: NetworkTask ->
			task.addClientbound(
				ClientboundPacketTest::class.java,
				ClientboundPacketTest.TYPE,
				ClientboundPacketTest.STREAM_CODEC,
				ClientboundPacketTest::handleClientbound
			)
		}
	}
}
