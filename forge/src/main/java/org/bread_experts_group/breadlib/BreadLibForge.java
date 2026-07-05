package org.bread_experts_group.breadlib;

import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.bread_experts_group.breadlib.platform.ForgeGenerateDataTask;
import org.bread_experts_group.breadlib.registry.RegistryProvider;
import org.bread_experts_group.breadlib.task.TaskManager;

@Mod(BreadLib.MOD_ID)
public class BreadLibForge {
	public static <T> void registerContent(RegistryProvider<T> provider, RegisterEvent event) {
		event.register(provider.getKey(), helper ->
				provider.entries().forEach((key, value) -> {
					helper.register(key.getName(), value.get());
					key.bind();
				})
		);
	}

	public static void registerContent(IEventBus eventBus) {
		eventBus.addListener(EventPriority.NORMAL, false, RegisterEvent.class, event -> {
			for (RegistryProvider<?> provider : RegistryProvider.providers) registerContent(provider, event);
		});
	}

	public BreadLibForge(FMLJavaModLoadingContext context) {
		context.getModEventBus().addListener((GatherDataEvent event) ->
				TaskManager.runTasks(new ForgeGenerateDataTask(event))
		);

		IEventBus eventBus = context.getModEventBus();
		BreadLib.LOGGER.info("Hello Forge world!");
		BreadLib.init();
		registerContent(eventBus);

		ForgeEvents.registerEvents(eventBus);
		ForgeNetworking.setup();
	}
}
