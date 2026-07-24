package org.bread_experts_group.breadlib.extensions.item

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack

interface IKeyboardItem {
	fun onKeyPress(
		button: Int,
		scanCode: Int,
		action: Int,
		modifiers: Int,
		heldStack: ItemStack,
		level: ClientLevel,
		player: Player
	)
}