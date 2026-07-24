package org.bread_experts_group.breadlib.registry.objects

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadlib.registry.ItemLikeExtended

class RegistryBlock<B : Block>(name: ResourceLocation) : RegistryObject<Block, B>(
	name, BuiltInRegistries.BLOCK
), ItemLikeExtended {
	companion object {
		fun <B : Block> create(modID: String, name: String): RegistryBlock<B> =
			RegistryBlock(ResourceLocation.fromNamespaceAndPath(modID, name))
	}

	fun defaultState(): BlockState = get().defaultBlockState()

	override fun asItem(): Item = toStack().item

	override fun toStack(): ItemStack = ItemStack(this, 1)

	override fun asStack(count: Int): ItemStack = ItemStack(this, count)
}
