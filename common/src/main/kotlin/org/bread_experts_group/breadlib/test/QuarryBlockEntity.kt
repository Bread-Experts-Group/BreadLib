package org.bread_experts_group.breadlib.test

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.monster.Skeleton
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.material.FluidState
import net.minecraft.world.level.material.Fluids
import kotlin.math.ceil
import kotlin.math.roundToInt

class QuarryBlockEntity(pPos: BlockPos, pBlockState: BlockState) : BlockEntity(
	BlockEntityTypeTest.QUARRY_TYPE.get(), pPos, pBlockState
) {
	var x = -10
	var z = -10

	var y = blockPos.y - 1

	val xMine = 10
	val zMine = 10

	var noUpdate = false

	var drillPos: BlockPos? = null
	var drillItem = Items.NETHERITE_PICKAXE.defaultInstance
	var correctTool = false

	var breakingProgress = 0
	var breakingTick = 0
	var tickTime = 0f

	private var id: Int? = null

	fun tick(level: ServerLevel, pos: BlockPos, state: BlockState) {
		if (id == null) id = ItemEntity(level, 0.0, 0.0, 0.0, ItemStack(this.blockState.block)).id
		if (drillPos == null) {
			val (nX, nZ) = when (state.getValue(HorizontalDirectionalBlock.FACING)) {
				Direction.UP -> 0 to 0
				Direction.DOWN -> 0 to 0
				Direction.NORTH -> 1 to -1
				Direction.SOUTH -> -1 to 1
				Direction.WEST -> -1 to 1
				Direction.EAST -> 1 to -1
			}
			var minePos: BlockPos
			do {
				if (x > xMine) {
					x = -10
					z++
				}
				if (z > zMine) {
					z = -10
					y--
				}
				minePos = BlockPos(this.blockPos.x - (x++ * nX), y, this.blockPos.z - (z * nZ))
				val currentState = level.getBlockState(minePos)
				if (currentState.block == Blocks.BEDROCK) {
					noUpdate = true
					return
				}
				if (currentState.block == Blocks.AIR || !currentState.fluidState.isEmpty) {
					continue
				}
				drillPos = minePos

				val tool = drillItem.get(DataComponents.TOOL)!!
				val speedMultiplier = tool.getMiningSpeed(currentState)
				var damage = speedMultiplier / currentState.getDestroySpeed(level, minePos)
				correctTool = (!currentState.requiresCorrectToolForDrops()) || tool.isCorrectForDrops(currentState)
				damage /= if (correctTool) 30 else 100
				tickTime = if (damage >= 1f) -1f else ceil(1 / damage)
				break
			} while (true)
		}
		val drillPos = drillPos!!
		if (tickTime > 0f) {
			breakingTick++
			val newProgress = ((breakingTick / tickTime) * 10).roundToInt()
			if (newProgress != 10) {
				if (newProgress != breakingProgress) {
					level.destroyBlockProgress(id!!, drillPos, newProgress)
					breakingProgress = newProgress
				}
				return
			}
		}
		level.destroyBlock(drillPos, correctTool)
		this.drillPos = null
		this.breakingTick = 0
		this.breakingProgress = 0
	}
}