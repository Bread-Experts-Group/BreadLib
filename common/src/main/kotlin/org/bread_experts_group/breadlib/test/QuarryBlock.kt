package org.bread_experts_group.breadlib.test

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadlib.extensions.block.BlockProperties
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlockWithEntity

private val blockProperties = BlockProperties
	.prop(HorizontalDirectionalBlock.FACING, Direction.NORTH) { it.horizontalDirection.opposite }

class QuarryBlock : BreadLibBlockWithEntity<QuarryBlockEntity>(
	QuarryBlockEntity::class.java,
	Properties.ofFullCopy(Blocks.IRON_BLOCK)
) {
	override fun breadLibProperties(): BlockProperties = blockProperties

	override fun onRemove(
		state: BlockState,
		level: Level,
		pos: BlockPos,
		newState: BlockState,
		movedByPiston: Boolean
	) {
		(level.getBlockEntity(pos) as? QuarryBlockEntity)?.let {
			val id = it.id ?: return@let
			val drillPos = it.drillPos ?: return@let
			level.destroyBlockProgress(id, drillPos, -1)
		}
		super.onRemove(state, level, pos, newState, movedByPiston)
	}
}