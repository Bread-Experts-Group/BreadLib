package org.bread_experts_group.breadlib;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bread_experts_group.breadlib.extensions.IMouseItem;
import org.bread_experts_group.breadlib.platform.PlatformServices;
import org.bread_experts_group.breadlib.registry.RegistryProvider;
import org.bread_experts_group.breadlib.task.TaskManager;
import org.bread_experts_group.breadlib.task.input.MouseTasks;
import org.bread_experts_group.breadlib.test.*;

public class BreadLib {
	public static final String MOD_ID = "breadlib";
	public static final Logger LOGGER = LogManager.getLogger("BreadLib");

	public static void init() {
		Test.Companion.test();
		LOGGER.info(
				"Hello from Common init on {}! we are currently in a {} environment!",
				PlatformServices.PLATFORM.getPlatformName(), PlatformServices.PLATFORM.getEnvironmentName()
		);

		RegistryProvider.registerAll(
				BlocksTest.BLOCK_REGISTRY,
				ItemsTest.ITEM_REGISTRY,
				CreativeTabTest.CREATIVE_TABS_REGISTRY,
				BlockEntityTypeTest.BLOCK_ENTITY_TYPE_REGISTRY
		);

//		TasksTest.renderTest();
//		TasksTest.mouseTest();

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
				}
				else if (task.isPost()) item.onMouseInputPost(heldStack, (ClientLevel) player.level(), player);
			}
		});

		if (PlatformServices.PLATFORM.isModLoaded("breadlib")) {
			LOGGER.info("Hello to breadlib");
		}
	}
}
