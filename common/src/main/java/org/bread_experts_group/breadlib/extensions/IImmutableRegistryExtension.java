package org.bread_experts_group.breadlib.extensions;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;

import java.util.Map;

public interface IImmutableRegistryExtension {
	Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> getRegistries();

	void setRegistries(Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> registries);
}
