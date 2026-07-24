package org.bread_experts_group.breadlib.registry

import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.ItemLike

interface ItemLikeExtended : ItemLike {
	fun toStack(): ItemStack

	fun asStack(count: Int): ItemStack
}
