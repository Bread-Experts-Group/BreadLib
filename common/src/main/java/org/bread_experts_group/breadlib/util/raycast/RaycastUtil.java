package org.bread_experts_group.breadlib.util.raycast;

import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bread_experts_group.breadlib.util.Invokers;
import org.bread_experts_group.breadlib.util.VecUtil;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;

public class RaycastUtil {
	@Nullable
	public static <T> RaycastResult<T> raycast(
			Level level,
			Vec3 positionFrom,
			Vec3 directionTo,
			double length,
			double stepSize,
			Invokers.Two<Vec3, Vec3, T> selector
	) {
		RaycastResult<T> result = null;
		double distance = 0.0;
		do {
			Vec3 hitPosition = positionFrom.add(directionTo.scale(distance));
			T hit = selector.invoke(positionFrom, hitPosition);
			if (hit != null) {
				BlockPos blockPos = BlockPos.containing(hitPosition);
				VoxelShape shape = level.getBlockState(blockPos).getShape(level, blockPos);
				result = new RaycastResult<>(positionFrom, directionTo, hitPosition, shape, blockPos, length, distance, hit);
				break;
			}
			distance += stepSize;
		} while (distance < length);
		return result;
	}

	@Nullable
	public static <T> RaycastResult<T> entityRaycast(
			Entity entity,
			double length,
			double stepSize,
			float deviation,
			float offsetX,
			float offsetY,
			Invokers.Three<Level, Vec3, Vec3, T> selector
	) {
		return raycast(
				entity.level(),
				entity.getEyePosition(),
				entity.calculateViewVector(entity.getXRot() + offsetX, entity.getYRot() + offsetY)
						.offsetRandom(entity.getRandom(), deviation),
				length,
				stepSize,
				(from, to) -> selector.invoke(entity.level(), from, to)
		);
	}

	@Nullable
	public static <T> RaycastResult<T> entityRaycast(
			Entity entity,
			double length,
			double stepSize,
			Invokers.Three<Level, Vec3, Vec3, T> selector
	) {
		return entityRaycast(entity, length, stepSize, 0f, 0f, 0f, selector);
	}

	@Nullable
	public static <T> RaycastResult<T> cameraRaycast(
			Camera camera,
			double length,
			double stepSize,
			Invokers.Three<Level, Vec3, Vec3, T> selector
	) {
		LocalPlayer player = Minecraft.getInstance().player;
		if (player == null) return null;
		return raycast(
				player.level(),
				camera.getPosition(),
				player.calculateViewVector(player.getXRot(), player.getYRot()),
				length,
				stepSize,
				(from, to) -> selector.invoke(player.level(), from, to)
		);
	}

	public static Invokers.Three<Level, Vec3, Vec3, BlockState> blocks(Block... filter) {
		return (level, from, to) -> {
			BlockPos blockPos = new BlockPos(VecUtil.toVec3i(to));
			BlockState state = level.getBlockState(blockPos);
			VoxelShape shape = state.getShape(level, blockPos);
			if (Arrays.stream(filter).anyMatch(block -> block == state.getBlock())) return null;
			return (shape.clip(from, to, blockPos) != null) ? state : null;
		};
	}

	public static Invokers.Three<Level, Vec3, Vec3, Entity> entities(EntityType<?>... filter) {
		return (level, from, to) -> {
			List<Entity> entities = level.getEntities(null, AABB.ofSize(to, 1.0, 1.0, 1.0));
			if (entities.isEmpty()) return null;
			Entity entity = entities.getFirst();
			if (Arrays.stream(filter).anyMatch(type -> type == entity.getType())) return null;
			return entity;
		};
	}
}