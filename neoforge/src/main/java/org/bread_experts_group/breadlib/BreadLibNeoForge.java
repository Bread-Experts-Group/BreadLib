package org.bread_experts_group.breadlib;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.registries.RegisterEvent;
import org.bread_experts_group.breadlib.platform.NeoForgeGenerateDataTask;
import org.bread_experts_group.breadlib.registry.RegistryProvider;
import org.bread_experts_group.breadlib.task.TaskManager;

@Mod(BreadLib.MOD_ID)
public class BreadLibNeoForge {
	public static <T> void registerContent(RegistryProvider<T> provider, RegisterEvent event) {
		event.register(provider.getKey(), helper ->
				provider.entries().forEach((key, value) -> {
							helper.register(key.getName(), value.get());
							key.bind();
						}
				)
		);
	}

	public static void registerContent(IEventBus eventBus) {
		eventBus.addListener(RegisterEvent.class, event -> {
			for (RegistryProvider<?> provider : RegistryProvider.providers) registerContent(provider, event);
		});
	}

	public BreadLibNeoForge(IEventBus eventBus) {
		eventBus.addListener(GatherDataEvent.class, (event) -> TaskManager.runTasks(
				new NeoForgeGenerateDataTask(event))
		);

		BreadLib.LOGGER.info("Hello NeoForge world!");
		BreadLib.init();
		registerContent(eventBus);
		NeoEvents.registerEvents(eventBus);
	}
}
