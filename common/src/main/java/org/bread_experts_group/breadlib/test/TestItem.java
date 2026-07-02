package org.bread_experts_group.breadlib.test;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.bread_experts_group.breadlib.extensions.IMouseItem;

public class TestItem extends Item implements IMouseItem {
	public TestItem() {
		super(new Properties());
	}

	@Override
	public boolean onMouseScroll(ItemStack heldStack, ClientLevel level, LocalPlayer player) {
		if (player.isCrouching()) {
			level.playLocalSound(player, SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.AMBIENT, 1f, 1f);
			player.displayClientMessage(Component.literal("pling!"), false);
			return true;
		}
		return false;
	}
}
