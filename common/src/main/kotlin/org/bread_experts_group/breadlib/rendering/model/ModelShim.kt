package org.bread_experts_group.breadlib.rendering.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.platform.PlatformServices

object ModelShim {
	@JvmStatic
	fun doom(
		state: BlockState,
		pos: BlockPos,
		level: Level,
		poseStack: PoseStack,
		vertexConsumer: VertexConsumer,
		randomSource: RandomSource
	) {
		val platform = PlatformServices.PLATFORM.platformName
		BreadLib.LOGGER.info("$platform, (${pos.x}, ${pos.y}, ${pos.z}), ${state.block}")
	}
}