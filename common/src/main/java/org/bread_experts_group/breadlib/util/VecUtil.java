package org.bread_experts_group.breadlib.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public class VecUtil {
	public static Vec3i toVec3i(Vec3 vec) {
		return new Vec3i(Mth.floor(vec.x), Mth.floor(vec.y), Mth.floor(vec.z));
	}

	public static BlockPos toBlockPos(Vec3 vec) {
		return new BlockPos(toVec3i(vec));
	}

	public static Vec3 toVec3(Vec3i vec) {
		return new Vec3(vec.getX(), vec.getY(), vec.getZ());
	}
}
