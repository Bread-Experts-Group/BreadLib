package org.bread_experts_group.breadlib.test.client;

import net.minecraft.client.Minecraft;
import org.bread_experts_group.breadlib.BreadLib;
import org.bread_experts_group.breadlib.task.TaskManager;
import org.bread_experts_group.breadlib.task.network.NetworkTask;
import org.bread_experts_group.breadlib.task.render.LayeredDrawTask;
import org.bread_experts_group.breadlib.test.ClientboundPacketTest;
import org.bread_experts_group.breadlib.util.Color;

public class TasksClientTest {
	public static void renderTest() {
//		TaskManager.newTask(LevelRenderTask.class, task -> BreadLib.LOGGER.info(task.stage));
	}

	public static void layeredDrawTest() {
		TaskManager.newTask(LayeredDrawTask.class, task -> task.add(BreadLib.modLoc("layered_draw", "test_layer"), ((guiGraphics, deltaTracker) -> {
//				Window window = Minecraft.getInstance().getWindow();
			guiGraphics.fill(0, 0, 25, 12, Color.BLACK);
			guiGraphics.drawString(Minecraft.getInstance().font, "Breadlib", 1, 1, Color.WHITE, false);
		})));
	}

	public static void networkTest() {
		TaskManager.newTask(NetworkTask.class, task -> task.addClientbound(
				ClientboundPacketTest.class,
				ClientboundPacketTest.TYPE,
				ClientboundPacketTest.STREAM_CODEC,
				ClientboundPacketTest::handleClientbound
		));
	}
}
