package org.bread_experts_group.breadlib.test;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.bread_experts_group.breadlib.BreadLib;
import org.bread_experts_group.breadlib.extensions.IMouseItem;
import org.bread_experts_group.breadlib.task.TaskManager;
import org.bread_experts_group.breadlib.task.input.MouseTasks;
import org.bread_experts_group.breadlib.task.network.NetworkTask;
import org.bread_experts_group.breadlib.task.render.LayeredDrawTask;
import org.bread_experts_group.breadlib.task.render.LevelRenderTask;
import org.bread_experts_group.breadlib.util.Color;

public class TasksTest {
	public static void renderTest() {
		TaskManager.newTask(LevelRenderTask.class, task -> BreadLib.LOGGER.info(task.stage));
	}

	public static void mouseTests() {
		TaskManager.newTask(MouseTasks.Scroll.class, task -> {
			Minecraft minecraft = Minecraft.getInstance();
			LocalPlayer player = minecraft.player;
			if (player == null) return;
			ItemStack heldStack = player.getMainHandItem();
			if (heldStack.getItem() instanceof IMouseItem item) {
				if (item.onMouseScroll(heldStack, (ClientLevel) player.level(), player)) task.cancel();
			}
		});

		TaskManager.newTask(MouseTasks.Button.class, task -> {
			Minecraft minecraft = Minecraft.getInstance();
			LocalPlayer player = minecraft.player;
			if (player == null) return;
			ItemStack heldStack = player.getMainHandItem();
			if (heldStack.getItem() instanceof IMouseItem item) {
				if (task.isPre()) {
					if (item.onMouseInputPre(heldStack, (ClientLevel) player.level(), player)) task.cancel();
				} else if (task.isPost()) item.onMouseInputPost(heldStack, (ClientLevel) player.level(), player);
			}
		});
	}

	public static void layeredDrawTest() {
		TaskManager.newTask(LayeredDrawTask.class, task -> {
			task.add(BreadLib.modLoc("layered_draw", "test_layer"), ((guiGraphics, deltaTracker) -> {
//				Window window = Minecraft.getInstance().getWindow();
				guiGraphics.fill(0, 0, 25, 12, Color.BLACK);
				guiGraphics.drawString(Minecraft.getInstance().font, "Breadlib", 1, 1, Color.WHITE, false);
			}));
		});
	}

	public static void networkTest() {
		TaskManager.newTask(NetworkTask.class, task -> {
			task.addServerbound(
					ServerboundPacketTest.class,
					ServerboundPacketTest.TYPE,
					ServerboundPacketTest.STREAM_CODEC,
					ServerboundPacketTest::handleServerbound
			);
			task.addClientbound(
					ClientboundPacketTest.class,
					ClientboundPacketTest.TYPE,
					ClientboundPacketTest.STREAM_CODEC,
					ClientboundPacketTest::handleClientbound
			);
		});
	}
}
