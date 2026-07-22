package org.bread_experts_group.breadlib.test

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderLookup
import net.minecraft.core.component.DataComponents
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.saveddata.SavedData
import org.bread_experts_group.breadlib.extensions.block.Tickable
import kotlin.math.ceil
import kotlin.math.roundToInt

class QuarryBlockEntity(pPos: BlockPos, pBlockState: BlockState) : BlockEntity(
	BlockEntityTypeTest.QUARRY_TYPE.get(), pPos, pBlockState
), Tickable.Server {
	private val xMine = 10
	private val zMine = 10

	private var x = -xMine
	private var z = -zMine

	private var y = blockPos.y - 1

	private var noUpdate = false

	var drillPos: BlockPos? = null
	private var drillItem = Items.NETHERITE_PICKAXE.defaultInstance
	private var correctTool = false

	private var breakingProgress = 0
	private var breakingTick = 0
	private var tickTime = 0f

	var id: Int? = null

	override fun serverTick(level: ServerLevel, pos: BlockPos, state: BlockState) {
		if (id == null) id = ItemEntity(level, 0.0, 0.0, 0.0, ItemStack(this.blockState.block)).let {
			it.discard()
			it.id
		}
		if (drillPos == null) {
			val facing = state.getValue(HorizontalDirectionalBlock.FACING).opposite
			val (nX, nZ) = when (facing) {
				Direction.UP -> 0 to 0
				Direction.DOWN -> 0 to 0
				Direction.NORTH -> -1 to 1
				Direction.SOUTH -> 1 to -1
				Direction.WEST -> 1 to -1
				Direction.EAST -> -1 to 1
			}
			val offset = facing.normal.let { BlockPos((xMine + 1) * it.x, 0, (zMine + 1) * it.z) }
			var minePos: BlockPos
			do {
				if (x > xMine) {
					x = -xMine
					z++
				}
				if (z > zMine) {
					z = -zMine
					y--
				}
				minePos = BlockPos(
					(this.blockPos.x - (x++ * nX)) + offset.x,
					y,
					(this.blockPos.z - (z * nZ)) + offset.z
				)
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
			// TODO: when home: add chunk specific data if possible?
			// TODO: if not feasible: look at level datastorage
			level.dataStorage.set("a", object : SavedData() {
				override fun save(p0: CompoundTag, p1: HolderLookup.Provider): CompoundTag {
					TODO("Not yet implemented")
				}
			})
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