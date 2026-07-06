package org.bread_experts_group.breadlib.platform;

import org.bread_experts_group.breadlib.BreadLib;

import java.util.ServiceLoader;

public class PlatformServices {
	public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

	public static <T> T load(Class<T> clazz) {
		final T loadedService = ServiceLoader.load(clazz).findFirst().orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
		BreadLib.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
		return loadedService;
	}
}