package org.bread_experts_group.breadlib.test

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlockEntity

class TestBlockEntity(
	pos: BlockPos,
	blockState: BlockState
) : BreadLibBlockEntity(pos, blockState, BreadLib.MOD_ID)