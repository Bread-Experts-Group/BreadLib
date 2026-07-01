package org.bread_experts_group.breadlib.registry.objects;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class RegistryObject<T, I extends T> implements Supplier<T> {
	private final ResourceLocation name;
	private final ResourceKey<? extends Registry<T>> key;
	@Nullable
	private I value = null;
	private boolean bound = false;

	public static <T, I extends T> RegistryObject<T, I> create(String modID, String name, Registry<T> registry) {
		return new RegistryObject<>(ResourceLocation.fromNamespaceAndPath(modID, name), registry);
	}

	public RegistryObject(ResourceLocation name, Registry<T> registry) {
		this.name = name;
		this.key = registry.key();
	}

	@SuppressWarnings("unchecked")
	public void bind() {
		Registry<?> registry = BuiltInRegistries.REGISTRY.getOptional(this.key.location()).orElse(null);
		if (registry == null) throw new NullPointerException("Registry" + this.key.location() + " does not exist.");
		this.value = (I) registry.get(this.name);
		this.bound = true;
	}

	public boolean isBound() {
		return bound;
	}

	@Override
	public I get() {
		if (!this.bound) throw new NullPointerException("Value was not bound.");
		return this.value;
	}

	public ResourceLocation getName() {
		return name;
	}

	public ResourceKey<? extends Registry<T>> getKey() {
		return key;
	}
}
