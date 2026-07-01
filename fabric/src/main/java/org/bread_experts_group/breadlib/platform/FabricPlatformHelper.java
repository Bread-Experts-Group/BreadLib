package org.bread_experts_group.breadlib.platform;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import org.bread_experts_group.breadlib.platform.services.IPlatformHelper;
import org.bread_experts_group.breadlib.registry.RegistryProvider;

public class FabricPlatformHelper implements IPlatformHelper {

	@Override
	public String getPlatformName() {
		return "Fabric";
	}

	@Override
	public boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	@Override
	public boolean isDevelopmentEnvironment() {
		return FabricLoader.getInstance().isDevelopmentEnvironment();
	}

	public static <T> void registerContent(RegistryProvider<T> provider) {
		provider.entries().forEach((key, value) -> {
			Registry.registerForHolder(provider.getRegistry(), key.getName(), value.get());
			key.bind();
		});
	}

	public static void registerContent() {
		for (RegistryProvider<?> provider : RegistryProvider.providers) registerContent(provider);
	}
}
