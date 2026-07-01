package org.bread_experts_group.breadlib.util.raycast;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public record RaycastResult<T>(
		Vec3 positionFrom,
		Vec3 positionTo,
		Vec3 hitPosition,
		VoxelShape hitShape,
		BlockPos blockPosition,
		double length,
		double hitDistance,
		T hit
) {
	public Direction hitSide() {
		BlockHitResult result = this.hitShape.clip(this.positionFrom, this.hitPosition, this.blockPosition);
		if (result == null) return Direction.getNearest(this.positionFrom.subtract(this.hitPosition));
		return result.getDirection();
	}
}