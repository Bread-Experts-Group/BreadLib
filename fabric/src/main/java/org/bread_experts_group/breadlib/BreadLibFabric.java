package org.bread_experts_group.breadlib;

import net.fabricmc.api.ModInitializer;
import net.minecraft.core.Registry;
import org.bread_experts_group.breadlib.registry.RegistryProvider;

public class BreadLibFabric implements ModInitializer {
	public static <T> void registerContent(RegistryProvider<T> provider) {
		provider.entries().forEach((key, value) -> {
			Registry.registerForHolder(provider.getRegistry(), key.getName(), value.get());
			key.bind();
		});
	}

	public static void registerContent() {
		for (RegistryProvider<?> provider : RegistryProvider.providers) registerContent(provider);
	}

	@Override
	public void onInitialize() {
		BreadLib.LOGGER.info("Hello Fabric world!");
		BreadLib.init();
		registerContent();

		FabricEvents.registerEvents();
	}
}
