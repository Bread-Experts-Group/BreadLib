package org.bread_experts_group.breadlib

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.client.model.loading.v1.ModelLoadingPlugin
import org.bread_experts_group.breadlib.FabricHelper.registerContent
import org.bread_experts_group.breadlib.platform.PlatformInitialization
import org.bread_experts_group.breadlib.task.TaskManager
import org.bread_experts_group.breadlib.task.client.AdditionalModelsTask


class BreadLibFabric : ClientModInitializer, ModInitializer {
	override fun onInitialize() {
		BreadLib.LOGGER.info("Hello Fabric world!")
		BreadLib.init()
		registerContent(BreadLib.MOD_ID)

		FabricEvents.registerEvents()
		FabricNetworking.registerPackets()

		PlatformInitialization.registerCapabilities(BreadLib.MOD_ID)

		ModelLoadingPlugin.register { context ->
			context.addModels(TaskManager.runTasks(AdditionalModelsTask()).getLocations())
		}
	}

	override fun onInitializeClient() {
	}
}