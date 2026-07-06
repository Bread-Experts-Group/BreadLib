package org.bread_experts_group.breadlib.extensions.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public interface ILightningStrikeAction {
	void onLightningStruck(Level level, BlockPos pos, BlockState state);
}
