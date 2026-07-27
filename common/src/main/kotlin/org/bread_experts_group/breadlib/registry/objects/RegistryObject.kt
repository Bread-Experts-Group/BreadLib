package org.bread_experts_group.breadlib.registry.objects

import net.minecraft.core.Registry
import net.minecraft.resources.ResourceLocation
import java.util.function.Supplier

open class RegistryObject<T, I : T>(
	val name: ResourceLocation,
	val registry: Registry<T>
) : Supplier<T> {
	companion object {
		fun <T, I : T> create(modID: String, name: String, registry: Registry<T>): RegistryObject<T, I> =
			RegistryObject(ResourceLocation.fromNamespaceAndPath(modID, name), registry)
	}

	private var value: I? = null

	@Suppress("UNCHECKED_CAST")
	fun bind() {
		this.value = registry.get(this.name) as I
	}

	override fun get(): I {
		return this.value ?: throw NullPointerException("Value for $name was null.")
	}
}
