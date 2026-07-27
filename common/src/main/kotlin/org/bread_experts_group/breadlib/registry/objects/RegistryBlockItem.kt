package org.bread_experts_group.breadlib.registry.objects

import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

class RegistryBlockItem<B : Block, I : BlockItem>(source: RegistryBlock<B>, val registryItem: RegistryItem<I>) : AbstractRegistryBlock<B>(
	source.name
) {
	override fun asItem(): Item = registryItem.get()
}