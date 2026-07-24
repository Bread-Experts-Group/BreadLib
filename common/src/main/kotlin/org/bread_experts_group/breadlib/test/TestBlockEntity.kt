package org.bread_experts_group.breadlib.test

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class TestBlockEntity(
	pos: BlockPos,
	blockState: BlockState
) : BlockEntity(BlockEntityTypeTest.TEST_TYPE.get(), pos, blockState)
