package org.bread_experts_group.breadlib.test;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.bread_experts_group.breadlib.extensions.item.IKeyboardItem;
import org.bread_experts_group.breadlib.extensions.item.IMouseItem;
import org.bread_experts_group.breadlib.platform.PlatformServices;

public class TestItem extends Item implements IMouseItem, IKeyboardItem {
	public TestItem() {
		super(new Properties());
	}

	@Override
	public boolean onMouseScroll(ItemStack heldStack, ClientLevel level, Player player) {
		if (player.isCrouching()) {
			level.playLocalSound(player, SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.AMBIENT, 1f, 1f);
			player.displayClientMessage(Component.literal("pling!"), false);
			PlatformServices.PLATFORM.sendToServer(new ServerboundPacketTest(10, "test"));
			return true;
		}
		return false;
	}

	@Override
	public void onKeyPress(
			int button,
			int scanCode,
			int action,
			int modifiers,
			ItemStack heldStack,
			ClientLevel level,
			Player player
	) {
		String key = InputConstants.getKey(button, scanCode).getName();
		if (player.isHolding(this)) player.displayClientMessage(Component.literal(key), true);
	}
}