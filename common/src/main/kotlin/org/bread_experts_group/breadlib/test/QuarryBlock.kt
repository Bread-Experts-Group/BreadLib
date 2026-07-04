package org.bread_experts_group.breadlib.test

import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.block.AbstractFurnaceBlock
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition

class QuarryBlock : Block(Properties.ofFullCopy(Blocks.IRON_BLOCK)) {
	init {
		this.registerDefaultState(
			this.stateDefinition.any().setValue(
				AbstractFurnaceBlock.FACING,
				Direction.NORTH
			) as BlockState
		)
	}

	override fun getStateForPlacement(
		context: BlockPlaceContext
	): BlockState = this.defaultBlockState().setValue(
		AbstractFurnaceBlock.FACING,
		context.horizontalDirection.opposite
	) as BlockState

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(AbstractFurnaceBlock.FACING)
	}
}