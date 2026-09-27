package org.bread_experts_group.breadlib

import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.event.server.ServerStartingEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
import org.bread_experts_group.breadlib.BreadLib.init
import org.bread_experts_group.breadlib.ForgeEvents.registerEvents
import org.bread_experts_group.breadlib.ForgeHelper.registerContent
import org.bread_experts_group.breadlib.task.TaskManager
import org.bread_experts_group.breadlib.task.server.ServerStartingTask

@Mod(BreadLib.MOD_ID)
class BreadLibForge(context: FMLJavaModLoadingContext) {
	init {
		val eventBus = context.modEventBus
		MinecraftForge.EVENT_BUS.addListener { event: ServerStartingEvent ->
			TaskManager.runTasks(ServerStartingTask(event.server))
		}

		BreadLib.LOGGER.info("Hello Forge world!")
		init()
		registerContent(eventBus, BreadLib.MOD_ID)
		registerEvents(eventBus)
		ForgeNetworking.setup()

		ForgeHelper.runDataGenerator(eventBus, BreadLib.MOD_ID)
	}
}
