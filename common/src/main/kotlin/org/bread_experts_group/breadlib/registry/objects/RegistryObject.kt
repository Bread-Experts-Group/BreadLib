package org.bread_experts_group.breadlib.registry.objects

import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import java.util.function.Supplier

open class RegistryObject<T, I : T>(val name: ResourceLocation, registry: Registry<T>) : Supplier<T> {
	val key: ResourceKey<out Registry<T>> = registry.key()
	private var value: I? = null

	@Suppress("UNCHECKED_CAST")
	fun bind() {
		val registry: Registry<*> = BuiltInRegistries.REGISTRY.getOptional(this.key.location()).orElse(null)
			?: throw NullPointerException("Registry" + this.key.location() + " does not exist.")
		this.value = registry.get(this.name) as I
	}

	override fun get(): I {
		return this.value ?: throw NullPointerException("Value for $name was null.")
	}

	companion object {
		fun <T, I : T> create(modID: String, name: String, registry: Registry<T>): RegistryObject<T, I> =
			RegistryObject(ResourceLocation.fromNamespaceAndPath(modID, name), registry)
	}
}
