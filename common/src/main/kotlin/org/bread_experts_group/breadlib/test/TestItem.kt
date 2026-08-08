package org.bread_experts_group.breadlib.test

import com.mojang.blaze3d.platform.InputConstants
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import org.bread_experts_group.breadlib.dimension.DimensionUtil
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

	override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
		if (level.isClientSide) return super.use(level, player, usedHand)
		DimensionUtil.createDimension(level as ServerLevel)
		return super.use(level, player, usedHand)
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
		if (button == InputConstants.KEY_G && action == InputConstants.PRESS) {
			level.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE).forEach { println(it) }
		}
	}
}