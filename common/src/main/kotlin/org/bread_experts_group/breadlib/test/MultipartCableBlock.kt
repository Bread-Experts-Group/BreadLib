package org.bread_experts_group.breadlib.test

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadlib.extensions.block.BlockProperties
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlock

private val blockProperties = BlockProperties()

class MultipartCableBlock : BreadLibBlock(Properties.of()) {
	override fun breadLibProperties(): BlockProperties = blockProperties
	override val generateQuads: (
		state: BlockState, pos: BlockPos, level: Level,
		poseStack: PoseStack, vertexConsumer: VertexConsumer, randomSource: RandomSource
	) -> Unit = { state, pos, level, poseStack, vertexConsumer, randomSource ->
		poseStack.pushPose()

		vertexConsumer.addVertex(poseStack.last(), 0f, 0f, 0f)
		vertexConsumer.setColor(1f, 0f, 0f, 1f)
		vertexConsumer.setUv(0f, 0f)
		vertexConsumer.setNormal(0f, 0f, 0f)
		vertexConsumer.setOverlay(OverlayTexture.NO_OVERLAY)
		vertexConsumer.setLight(0xF000F0)

		vertexConsumer.addVertex(poseStack.last(), 0f, 1f, 0f)
		vertexConsumer.setColor(0f, 1f, 0f, 1f)
		vertexConsumer.setUv(0f, 0f)
		vertexConsumer.setNormal(0f, 0f, 0f)
		vertexConsumer.setOverlay(OverlayTexture.NO_OVERLAY)
		vertexConsumer.setLight(0xF000F0)

		vertexConsumer.addVertex(poseStack.last(), 1f, 0f, 0f)
		vertexConsumer.setColor(0f, 0f, 1f, 1f)
		vertexConsumer.setUv(0f, 0f)
		vertexConsumer.setNormal(0f, 0f, 0f)
		vertexConsumer.setOverlay(OverlayTexture.NO_OVERLAY)
		vertexConsumer.setLight(0xF000F0)

		vertexConsumer.addVertex(poseStack.last(), 1f, 1f, 0f)
		vertexConsumer.setColor(0f, 0f, 1f, 1f)
		vertexConsumer.setUv(0f, 0f)
		vertexConsumer.setNormal(0f, 0f, 0f)
		vertexConsumer.setOverlay(OverlayTexture.NO_OVERLAY)
		vertexConsumer.setLight(0xF000F0)

		poseStack.popPose()
	}
}