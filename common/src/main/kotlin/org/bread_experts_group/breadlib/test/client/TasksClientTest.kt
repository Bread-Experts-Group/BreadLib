package org.bread_experts_group.breadlib.test.client

import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.BreadLib.modLoc
import org.bread_experts_group.breadlib.platform.PlatformServices
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
		newTask { task: LayeredDrawTask ->
			val platform = PlatformServices.PLATFORM.getPlatformName()
			val debugInfo =
				"${BreadLib.MOD_ID} v${BreadLib.MOD_VERSION} | $platform"
			val width = Minecraft.getInstance().font.width(platform)
			task.add(modLoc("layered_draw", "test_layer")) { guiGraphics: GuiGraphics, _: DeltaTracker ->
				guiGraphics.fill(0, 0, 40 + width, 12, Color.BLACK)
				guiGraphics.drawString(Minecraft.getInstance().font, debugInfo, 2, 2, Color.ORANGE, false)
			}
		}
	}

	fun networkTest() {
		newTask { task: NetworkTask ->
			task.addClientbound(
				ClientboundPacketTest::class.java,
				ClientboundPacketTest.TYPE,
				ClientboundPacketTest.STREAM_CODEC,
				ClientboundPacketTest::handleClientbound
			)
		}
	}
}
