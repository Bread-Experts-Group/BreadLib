package org.bread_experts_group.breadlib.platform;

import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.RegisterEvent;
import org.bread_experts_group.breadlib.platform.services.IPlatformHelper;
import org.bread_experts_group.breadlib.registry.RegistryProvider;

public class ForgePlatformHelper implements IPlatformHelper {

	@Override
	public String getPlatformName() {
		return "Forge";
	}

	@Override
	public boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	@Override
	public boolean isDevelopmentEnvironment() {
		return !FMLLoader.isProduction();
	}

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
}
