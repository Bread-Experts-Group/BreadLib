package org.bread_experts_group.breadlib.test

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.network.chat.Component
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadlib.extensions.item.IKeyboardItem
import org.bread_experts_group.breadlib.extensions.item.IMouseItem
import org.bread_experts_group.breadlib.platform.PlatformServices

class TestItem : Item(Properties()), IMouseItem, IKeyboardItem {
	override fun onMouseScroll(heldStack: ItemStack, level: ClientLevel, player: Player): Boolean {
		if (player.isShiftKeyDown) {
			level.playLocalSound(player, SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.AMBIENT, 1f, 1f)
			player.displayClientMessage(Component.literal("pling!"), false)
			PlatformServices.PLATFORM.sendToServer(ServerboundPacketTest(10, "test"))
			return true
		}
		return false
	}

	override fun onKeyPress(
		button: Int,
		scanCode: Int,
		action: Int,
		modifiers: Int,
		heldStack: ItemStack,
		level: ClientLevel,
		player: Player
	) {
		val key = InputConstants.getKey(button, scanCode).name
		player.displayClientMessage(Component.literal(key), true)
	}
}