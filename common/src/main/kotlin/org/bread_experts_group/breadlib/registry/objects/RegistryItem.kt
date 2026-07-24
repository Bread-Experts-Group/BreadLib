package org.bread_experts_group.breadlib.registry.objects

import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadlib.registry.ItemLikeExtended

class RegistryItem<I : Item>(name: ResourceLocation) : RegistryObject<Item, I>(
	name, BuiltInRegistries.ITEM
), ItemLikeExtended {
	companion object {
		fun <I : Item> create(modID: String, name: String): RegistryItem<I> =
			RegistryItem(ResourceLocation.fromNamespaceAndPath(modID, name))
	}

	override fun asItem(): Item = get()

	override fun toStack(): ItemStack = ItemStack(this, 1)

	override fun asStack(count: Int): ItemStack = ItemStack(this, count)
}
