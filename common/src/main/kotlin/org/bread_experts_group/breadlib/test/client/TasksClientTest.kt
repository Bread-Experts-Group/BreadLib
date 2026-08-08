package org.bread_experts_group.breadlib.test.client

import net.minecraft.client.DeltaTracker
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.network.chat.Component
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.BreadLib.modLoc
import org.bread_experts_group.breadlib.platform.PlatformServices
import org.bread_experts_group.breadlib.task.TaskManager.newTask
import org.bread_experts_group.breadlib.task.render.LayeredDrawTask
import org.bread_experts_group.breadlib.util.Color
import org.bread_experts_group.breadlib.util.minecraft

object TasksClientTest {
	fun renderTest() {
//		TaskManager.newTask(LevelRenderTask.class, task -> BreadLib.LOGGER.info(task.stage));
	}

	fun layeredDrawTest() {
		newTask { task: LayeredDrawTask ->
			val platform = PlatformServices.PLATFORM.platformName
			val debugInfo = Component.literal("${BreadLib.MOD_ID} v${BreadLib.MOD_VERSION} | $platform")
			val width = Minecraft.getInstance().font.width(debugInfo)
			task.add(modLoc("layered_draw", "test_layer")) { guiGraphics: GuiGraphics, _: DeltaTracker ->
				guiGraphics.fill(0, 0, width, 12, Color.BLACK)
				guiGraphics.drawString(Minecraft.getInstance().font, debugInfo, 2, 2, Color.ORANGE, false)
			}

			task.add(BreadLib.modLoc("dim_overlay")) { guiGraphics, deltaTracker ->
				val dimension = minecraft!!.level!!.dimension()
				guiGraphics.drawString(minecraft!!.font, "current dim: ${dimension.location()}", 0, 15, Color.WHITE)
			}
		}
	}
}
