package org.bread_experts_group.breadlib.test

import net.minecraft.core.Direction
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockBehaviour.Properties
import org.bread_experts_group.breadlib.extensions.block.BlockProperties
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlockWithEntity

private val blockProperties = BlockProperties
	.prop(HorizontalDirectionalBlock.FACING, Direction.NORTH) { it.horizontalDirection.opposite }

class QuarryBlock : BreadLibBlockWithEntity<QuarryBlockEntity>(
	::QuarryBlockEntity,
	Properties.ofFullCopy(Blocks.IRON_BLOCK),
	serverTick = QuarryBlockEntity::tick
) {
	override fun breadLibProperties(): BlockProperties = blockProperties
}