package org.bread_experts_group.breadlib;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bread_experts_group.breadlib.platform.PlatformServices;
import org.bread_experts_group.breadlib.registry.RegistryProvider;
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
		TasksTest.mouseTest();

		if (PlatformServices.PLATFORM.isModLoaded("breadlib")) {
			LOGGER.info("Hello to breadlib");
		}
	}
}
