package org.bread_experts_group.breadlib

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import org.bread_experts_group.breadlib.FabricRegistrationHelper.registerContent
import org.bread_experts_group.breadlib.platform.PlatformInitialization


class BreadLibFabric : ClientModInitializer, ModInitializer {
	override fun onInitialize() {
		BreadLib.LOGGER.info("Hello Fabric world!")
		BreadLib.init()
		registerContent(BreadLib.MOD_ID)

		FabricEvents.registerEvents()
		FabricNetworking.registerPackets()

		PlatformInitialization.registerCapabilities(BreadLib.MOD_ID)
	}

	override fun onInitializeClient() {
	}
}