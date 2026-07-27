package org.bread_experts_group.breadlib.test

import net.minecraft.core.Direction
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.extensions.block.BlockProperties
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlockWithEntity

private val blockProperties = BlockProperties
	.prop(HorizontalDirectionalBlock.FACING, Direction.NORTH) { it.horizontalDirection.opposite }

class TestBlock : BreadLibBlockWithEntity<TestBlockEntity>(TestBlockEntity::class.java, Properties.of(), modID = BreadLib.MOD_ID) {
	override fun breadLibProperties(): BlockProperties = blockProperties
}