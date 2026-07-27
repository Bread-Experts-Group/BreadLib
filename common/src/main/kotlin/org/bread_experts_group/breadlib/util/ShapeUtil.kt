package org.bread_experts_group.breadlib.util

import net.minecraft.world.level.block.Rotation
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.Shapes.or
import net.minecraft.world.phys.shapes.VoxelShape
import org.bread_experts_group.breadlib.util.VecUtil.unaryMinus
import java.util.stream.Stream

object ShapeUtil {
	private val shapesOrigin = Vec3(0.5, 0.5, 0.5)

	fun AABB.rotate(rotation: Rotation): AABB =
		when (rotation) {
			Rotation.NONE -> this
			Rotation.CLOCKWISE_90 -> AABB(-this.minZ, this.minY, this.minX, -this.maxZ, this.maxY, this.maxX)
			Rotation.CLOCKWISE_180 -> AABB(-this.minX, this.minY, -this.minZ, -this.maxX, this.maxY, -this.maxZ)
			Rotation.COUNTERCLOCKWISE_90 -> AABB(
				this.minZ,
				this.minY,
				-this.minX,
				this.maxZ,
				this.maxY,
				-this.maxX
			)
		}

	fun Stream<VoxelShape>.combine(): VoxelShape = this.reduce(::or).get()
	fun combineShapes(list: List<VoxelShape>): VoxelShape = list.stream().reduce(::or).get()

	fun VoxelShape.rotate(rotation: Rotation): VoxelShape = combineShapes(
		this.toAabbs().map { Shapes.create(it.move(shapesOrigin).rotate(rotation).move(-shapesOrigin)) }
	)

	fun VoxelShape.south(): VoxelShape = this.rotate(Rotation.CLOCKWISE_180)
	fun VoxelShape.east(): VoxelShape = this.rotate(Rotation.CLOCKWISE_90)
	fun VoxelShape.west(): VoxelShape = this.rotate(Rotation.COUNTERCLOCKWISE_90)
}