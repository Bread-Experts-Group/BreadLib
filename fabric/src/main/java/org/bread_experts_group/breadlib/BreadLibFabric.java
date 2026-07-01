package org.bread_experts_group.breadlib;

import net.fabricmc.api.ModInitializer;

import static org.bread_experts_group.breadlib.platform.FabricPlatformHelper.registerContent;

public class BreadLibFabric implements ModInitializer {
	@Override
	public void onInitialize() {
		BreadLib.LOGGER.info("Hello Fabric world!");
		BreadLib.init();
		registerContent();

		FabricEvents.addWorldRenderTasks();
	}
}
