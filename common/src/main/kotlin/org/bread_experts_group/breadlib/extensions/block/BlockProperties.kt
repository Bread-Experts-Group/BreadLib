package org.bread_experts_group.breadlib.extensions.block

import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.state.properties.Property
import org.bread_experts_group.breadlib.util.toMapKV1
import org.bread_experts_group.breadlib.util.toMapKV2

class BlockProperties {
	private val properties = mutableMapOf<Property<*>, Pair<Any, (context: BlockPlaceContext) -> Any>>()
	fun <T : Comparable<T>> prop(
		property: Property<T>,
		defaultValue: T, onPlacement: (context: BlockPlaceContext) -> T
	): BlockProperties {
		properties[property] = defaultValue to onPlacement
		return this
	}

	fun getPropertiesAndOnPlacement(): Map<Property<*>, (BlockPlaceContext) -> Any> = properties.toMapKV2()
	fun getPropertiesAndDefaultValues(): Map<Property<*>, Any> = properties.toMapKV1()
	fun getProperties(): Set<Property<*>> = properties.keys.toSet()

	companion object {
		fun <T : Comparable<T>> prop(
			property: Property<T>,
			defaultValue: T, onPlacement: (context: BlockPlaceContext) -> T
		): BlockProperties = BlockProperties().prop(property, defaultValue, onPlacement)
	}
}