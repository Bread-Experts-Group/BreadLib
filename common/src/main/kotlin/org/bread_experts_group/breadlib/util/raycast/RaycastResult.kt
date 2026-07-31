package org.bread_experts_group.breadlib.util.raycast

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.VoxelShape

data class RaycastResult<T>(
	val positionFrom: Vec3,
	val positionTo: Vec3,
	val hitPosition: Vec3,
	val hitShape: VoxelShape,
	val blockPosition: BlockPos,
	val length: Double,
	val hitDistance: Double,
	val hit: T
) {
	fun hitSide(): Direction {
		val result =
			this.hitShape.clip(
				this.positionFrom, this.hitPosition, this.blockPosition
			) ?: return Direction.getNearest(this.positionFrom.subtract(this.hitPosition))
		return result.direction
	}
}