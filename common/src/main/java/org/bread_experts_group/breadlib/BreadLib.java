package org.bread_experts_group.breadlib;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bread_experts_group.breadlib.data.LocaleGenerator;
import org.bread_experts_group.breadlib.platform.PlatformServices;
import org.bread_experts_group.breadlib.registry.RegistryProvider;
import org.bread_experts_group.breadlib.task.TaskManager;
import org.bread_experts_group.breadlib.task.data.GenerateDataTask;
import org.bread_experts_group.breadlib.test.*;

public class BreadLib {
	public static final String MOD_ID = "breadlib";
	public static final Logger LOGGER = LogManager.getLogger("BreadLib");

	public static ResourceLocation modLoc(String...path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, String.join("/", path));
	}

	public static void init() {
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
		TasksTest.mouseTests();
		TasksTest.layeredDrawTest();

		TaskManager.newTask(GenerateDataTask.class, task -> task.addGenerator(new LocaleGenerator(BreadLib.MOD_ID)));

		if (PlatformServices.PLATFORM.isModLoaded("breadlib")) {
			LOGGER.info("Hello to breadlib");
		}
	}
}
