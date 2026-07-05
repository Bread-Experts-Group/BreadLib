package org.bread_experts_group.breadlib;

import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bread_experts_group.breadlib.platform.PlatformServices;
import org.bread_experts_group.breadlib.test.*;

import static org.bread_experts_group.breadlib.BreadLibExampleKt.kExample;

public class BreadLib {
	public static final String MOD_ID = "breadlib";
	public static final Logger LOGGER = LogManager.getLogger("BreadLib");

	public static ResourceLocation modLoc(String...path) {
		return ResourceLocation.fromNamespaceAndPath(MOD_ID, String.join("/", path));
	}

	public static void init() {
		LOGGER.info(
				"Hello from Common init on {}! we are currently in a {} environment on the {}!",
				PlatformServices.PLATFORM.getPlatformName(),
				PlatformServices.PLATFORM.getEnvironmentKind(),
				PlatformServices.PLATFORM.getSide()
		);
		kExample();
	}
}
