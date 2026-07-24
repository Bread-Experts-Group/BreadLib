package org.bread_experts_group.breadlib.util

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties

object PoseUtil {
	const val TRANSLATE_OFFSET = 0.0001

	@JvmStatic
	fun PoseStack.translateOnBlockSide(state: BlockState, posX: Double, posY: Double, posZ: Double) {
		val horizontal = state.getOptionalValue(BlockStateProperties.HORIZONTAL_FACING)
		val facing = state.getOptionalValue(BlockStateProperties.FACING)
		val result = horizontal.orElseGet { facing.orElse(null) } ?: return
		this.translateToSide(result, posX, posY, posZ)
	}

	fun PoseStack.translateToSide(side: Direction, posX: Double, posY: Double, posZ: Double) {
		this.mulPose(Axis.YN.rotationDegrees(side.toYRot()))
		this.translate(posX, posY, posZ)
		when (side) {
			Direction.NORTH -> this.translate(-1.0, 1.0, this@PoseUtil.TRANSLATE_OFFSET)
			Direction.EAST -> this.translate(-1.0, 1.0, 1 + this@PoseUtil.TRANSLATE_OFFSET)
			Direction.WEST -> this.translate(0.0, 1.0, this@PoseUtil.TRANSLATE_OFFSET)
			Direction.SOUTH -> this.translate(0.0, 1.0, 1 + this@PoseUtil.TRANSLATE_OFFSET)
			Direction.UP -> {
				this.translate(-1.0, 1 + this@PoseUtil.TRANSLATE_OFFSET, 0.0)
				this.mulPose(Axis.XN.rotationDegrees(90f))
			}

			Direction.DOWN -> {
				this.translate(-1.0, 0.0 - this@PoseUtil.TRANSLATE_OFFSET, 0.0)
				this.mulPose(Axis.XP.rotationDegrees(90f))
			}
		}
	}
}
