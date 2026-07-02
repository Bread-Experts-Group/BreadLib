package org.bread_experts_group.breadlib.registry;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public interface ItemLikeExtended extends ItemLike {
	ItemStack toStack();

	ItemStack asStack(int count);
}
