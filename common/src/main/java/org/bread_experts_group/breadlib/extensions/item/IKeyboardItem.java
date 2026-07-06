package org.bread_experts_group.breadlib.extensions.item;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public interface IKeyboardItem {
	void onKeyPress(
			int button,
			int scanCode,
			int action,
			int modifiers,
			ItemStack heldStack,
			ClientLevel level,
			Player player
	);
}