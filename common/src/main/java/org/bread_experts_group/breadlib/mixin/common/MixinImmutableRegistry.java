package org.bread_experts_group.breadlib.mixin.common;

import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import org.bread_experts_group.breadlib.extensions.IImmutableRegistryExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(RegistryAccess.ImmutableRegistryAccess.class)
abstract class MixinImmutableRegistry implements IImmutableRegistryExtension {
	@Shadow public Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> registries;

	@Override
	public Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> getRegistries() {
		return registries;
	}

	@Override
	public void setRegistries(Map<? extends ResourceKey<? extends Registry<?>>, ? extends Registry<?>> registries) {
		this.registries = registries;
	}
}