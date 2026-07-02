package org.bread_experts_group.breadlib;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static org.bread_experts_group.breadlib.platform.ForgePlatformHelper.registerContent;

@Mod(BreadLib.MOD_ID)
public class BreadLibForge {

	public BreadLibForge(FMLJavaModLoadingContext context) {
		IEventBus eventBus = context.getModEventBus();
		BreadLib.LOGGER.info("Hello Forge world!");
		BreadLib.init();
		registerContent(eventBus);

		ForgeEvents.registerEvents();
	}
}
