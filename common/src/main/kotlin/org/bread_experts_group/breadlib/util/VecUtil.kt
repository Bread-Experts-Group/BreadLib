package org.bread_experts_group.breadlib.util

import net.minecraft.core.BlockPos
import net.minecraft.core.Vec3i
import net.minecraft.util.Mth
import net.minecraft.world.phys.Vec3
import org.joml.Vector3f

object VecUtil {
	operator fun Vec3.unaryMinus(): Vec3 = Vec3(-this.x, -this.y, -this.z)
	operator fun Vec3i.unaryMinus(): Vec3i = Vec3i(-this.x, -this.y, -this.z)
	operator fun Vec3.component1(): Double = this.x
	operator fun Vec3.component2(): Double = this.y
	operator fun Vec3.component3(): Double = this.z
	operator fun Vec3.div(amount: Double): Vec3 = Vec3(this.x / amount, this.y / amount, this.z / amount)
	operator fun Vec3.times(amount: Float): Vec3 = Vec3(this.x * amount, this.y * amount, this.z * amount)
	operator fun Vec3.plus(amount: Float): Vec3 = Vec3(this.x + amount, this.y + amount, this.z + amount)
	operator fun Vector3f.component1(): Float = this.x
	operator fun Vector3f.component2(): Float = this.y
	operator fun Vector3f.component3(): Float = this.z
	operator fun BlockPos.component1(): Int = this.x
	operator fun BlockPos.component2(): Int = this.y
	operator fun BlockPos.component3(): Int = this.z
	operator fun BlockPos.minus(other: BlockPos): BlockPos =
		BlockPos(this.x - other.x, this.y - other.y, this.z - other.z)

	operator fun BlockPos.times(other: BlockPos): BlockPos =
		BlockPos(this.x * other.x, this.y * other.y, this.z * other.z)

	fun Vec3.toVec3i(): Vec3i = Vec3i(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z))
	fun Vector3f.toVec3(): Vec3 = Vec3(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
	fun Vec3.toBlockPos(): BlockPos = BlockPos(Mth.floor(this.x), Mth.floor(this.y), Mth.floor(this.z))
	fun Vec3i.toVec3(): Vec3 = Vec3(this.x.toDouble(), this.y.toDouble(), this.z.toDouble())
	fun Vec3.negate(): Vec3 = Vec3(-this.x, -this.y, -this.z)
}
