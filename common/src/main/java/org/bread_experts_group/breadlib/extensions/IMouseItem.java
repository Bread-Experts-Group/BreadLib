package org.bread_experts_group.breadlib.extensions;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IMouseItem {
	default boolean onMouseScroll(ItemStack heldStack, ClientLevel level, Player player) {
		return false;
	}

	default boolean onMouseInputPre(ItemStack heldStack, ClientLevel level, Player player) {
		return false;
	}

	default void onMouseInputPost(ItemStack heldStack, ClientLevel level, Player player) {
	}
}
