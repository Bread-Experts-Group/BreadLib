package org.bread_experts_group.breadlib.registry.objects

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import org.bread_experts_group.breadlib.registry.RegistryProvider.Companion.getItems

class RegistryBlock<B : Block>(name: ResourceLocation) : AbstractRegistryBlock<B>(
	name
) {
	companion object {
		fun <B : Block> create(modID: String, name: String): RegistryBlock<B> =
			RegistryBlock(ResourceLocation.fromNamespaceAndPath(modID, name))
	}

	fun withItem(properties: Item.Properties = Item.Properties()): RegistryBlockItem<B, BlockItem> {
		val blockItem = getItems(this.name.namespace).registerSimpleBlockItem(name.path, { this.get() }, properties)
		return RegistryBlockItem(this, blockItem)
	}
}
