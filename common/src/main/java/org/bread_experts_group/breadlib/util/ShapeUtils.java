package org.bread_experts_group.breadlib.util;

import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import java.util.List;
import java.util.stream.Stream;

public class ShapeUtils {
	private static final Vec3 shapesOrigin = new Vec3(0.5, 0.5, 0.5);

	public static AABB rotate(AABB input, Rotation rotation) {
		return switch (rotation) {
			case NONE -> input;
			case CLOCKWISE_90 -> new AABB(-input.minZ, input.minY, input.minX, -input.maxZ, input.maxY, input.maxX);
			case CLOCKWISE_180 -> new AABB(-input.minX, input.minY, -input.minZ, -input.maxX, input.maxY, -input.maxZ);
			case COUNTERCLOCKWISE_90 ->
					new AABB(input.minZ, input.minY, -input.minX, input.maxZ, input.maxY, -input.maxX);
		};
	}

	public static VoxelShape combine(Stream<VoxelShape> shapes) {
		return shapes.reduce(Shapes::or).orElse(Shapes.block());
	}

	public static VoxelShape combine(List<VoxelShape> shapes) {
		return shapes.stream().reduce(Shapes::or).orElse(Shapes.block());
	}

	public static VoxelShape rotate(VoxelShape input, Rotation rotation) {
		return combine(input.toAabbs().stream().map((aabb) ->
						rotate(
								Shapes.create(aabb.move(shapesOrigin)), rotation)
								.move(-shapesOrigin.x, -shapesOrigin.y, -shapesOrigin.z)
				)
		);
	}

	public static VoxelShape south(VoxelShape input) {
		return rotate(input, Rotation.CLOCKWISE_180);
	}

	public static VoxelShape east(VoxelShape input) {
		return rotate(input, Rotation.CLOCKWISE_90);
	}

	public static VoxelShape west(VoxelShape input) {
		return rotate(input, Rotation.COUNTERCLOCKWISE_90);
	}
}