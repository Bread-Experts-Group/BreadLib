package org.bread_experts_group.breadlib.test

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition

class QuarryBlock : Block(Properties.ofFullCopy(Blocks.IRON_BLOCK)), EntityBlock {
	init {
		this.registerDefaultState(
			this.stateDefinition.any().setValue(
				HorizontalDirectionalBlock.FACING,
				Direction.NORTH
			) as BlockState
		)
	}

	override fun getStateForPlacement(
		context: BlockPlaceContext
	): BlockState = this.defaultBlockState().setValue(
		HorizontalDirectionalBlock.FACING,
		context.horizontalDirection.opposite
	) as BlockState

	override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		builder.add(HorizontalDirectionalBlock.FACING)
	}

	override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = QuarryBlockEntity(pos, state)

	override fun <T : BlockEntity> getTicker(
		level: Level,
		state: BlockState,
		blockEntityType: BlockEntityType<T>
	): BlockEntityTicker<T> = object : BlockEntityTicker<T> {
		override fun tick(p0: Level, p1: BlockPos, p2: BlockState, p3: T) {
			(p3 as QuarryBlockEntity).tick(level, state)
		}
	}
}