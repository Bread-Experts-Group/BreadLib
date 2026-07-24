package org.bread_experts_group.breadlib.extensions.item

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

interface IMouseItem {
	fun onMouseScroll(heldStack: ItemStack, level: ClientLevel, player: Player): Boolean {
		return false
	}

	fun onMouseInputPre(heldStack: ItemStack, level: ClientLevel, player: Player): Boolean {
		return false
	}

	fun onMouseInputPost(heldStack: ItemStack, level: ClientLevel, player: Player) {
	}
}
