package org.bread_experts_group.breadlib.util.raycast

import net.minecraft.client.Camera
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.EntityGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadlib.util.VecUtil.toVec3i

object RaycastUtil {
	fun <T> raycast(
		level: Level,
		positionFrom: Vec3,
		directionTo: Vec3,
		length: Double,
		stepSize: Double,
		selector: (Vec3, Vec3) -> T
	): RaycastResult<T>? {
		var result: RaycastResult<T>? = null
		var distance = 0.0
		do {
			val hitPosition = positionFrom.add(directionTo.scale(distance))
			val hit = selector.invoke(positionFrom, hitPosition)
			if (hit != null) {
				val blockPos = BlockPos.containing(hitPosition)
				val shape = level.getBlockState(blockPos).getShape(level, blockPos)
				result =
					RaycastResult(positionFrom, directionTo, hitPosition, shape, blockPos, length, distance, hit)
				break
			}
			distance += stepSize
		} while (distance < length)
		return result
	}

	fun <T> entityRaycast(
		entity: Entity,
		length: Double,
		stepSize: Double,
		deviation: Float = 0f,
		offsetX: Float = 0f,
		offsetY: Float = 0f,
		selector: (BlockGetter, Vec3, Vec3) -> T
	): RaycastResult<T>? {
		return this.raycast(
			entity.level(),
			entity.eyePosition,
			entity.calculateViewVector(entity.xRot + offsetX, entity.yRot + offsetY)
				.offsetRandom(entity.getRandom(), deviation),
			length,
			stepSize
		) { from, to -> selector(entity.level(), from, to) }
	}

	fun <T> cameraRaycast(
		camera: Camera,
		length: Double,
		stepSize: Double,
		selector: (BlockGetter, Vec3, Vec3) -> T
	): RaycastResult<T>? {
		val player = Minecraft.getInstance().player ?: return null
		return this.raycast(
			player.level(),
			camera.position,
			player.calculateViewVector(player.xRot, player.yRot),
			length,
			stepSize,
		) { from, to -> selector(player.level(), from, to) }
	}

	fun blocks(vararg filter: Block): (BlockGetter, Vec3, Vec3) -> BlockState? = { level, from, to ->
		val blockPos = BlockPos(to.toVec3i())
		val state = level.getBlockState(blockPos)
		val shape = state.getShape(level, blockPos)
		if (filter.contains(state.block)) null else
			if (shape.clip(from, to, blockPos) != null) state else null
	}

	fun entities(vararg filter: EntityType<*>): (EntityGetter, Vec3, Vec3) -> Entity? = { level, from, to ->
		val entities = level.getEntities(null, AABB.ofSize(to, 1.0, 1.0, 1.0))
			.firstOrNull()
		if (entities == null || filter.contains(entities.type) || entities.boundingBox.clip(from, to).isEmpty) null
		else entities
	}
}