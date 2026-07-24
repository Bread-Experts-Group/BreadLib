package org.bread_experts_group.breadlib.test

import net.minecraft.core.BlockPos
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties

class TestBlock : Block(Properties.of()), EntityBlock {
	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = TestBlockEntity(pos, state)

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(BlockStateProperties.HORIZONTAL_FACING)
	}

	override fun getStateForPlacement(context: BlockPlaceContext): BlockState =
		this.defaultBlockState()
			.setValue(BlockStateProperties.HORIZONTAL_FACING, context.horizontalDirection.opposite)
}