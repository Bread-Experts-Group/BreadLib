package org.bread_experts_group.breadlib.data.model

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import org.bread_experts_group.breadlib.registry.objects.AbstractRegistryBlock
import org.bread_experts_group.breadlib.registry.objects.RegistryItem
import org.bread_experts_group.breadlib.util.location

data class ObjectResourceLocation(val location: ResourceLocation, val type: String) {
	constructor(item: Item) : this(item.location, "item")
	constructor(block: Block) : this(block.location, "block")

	constructor(item: RegistryItem<*>) : this(item.get())
	constructor(block: AbstractRegistryBlock<*>) : this(block.get())

	override fun toString(): String = "${location.namespace}:$type/${location.path}"
}