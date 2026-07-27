package org.bread_experts_group.breadlib.test

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexFormat
import com.mojang.blaze3d.vertex.VertexFormatElement
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.world.phys.Vec2
import net.minecraft.world.phys.Vec3
import org.bread_experts_group.breadlib.rendering.ExtraVertexFormatElements
import org.bread_experts_group.breadlib.rendering.ExtraVertexFormatElements.putFloat
import org.bread_experts_group.breadlib.rendering.ExtraVertexFormatElements.putVec2
import org.bread_experts_group.breadlib.rendering.VertexUtil.simpleQuad
import org.bread_experts_group.breadlib.rendering.shader.DynamicShaderManager.getType
import org.bread_experts_group.breadlib.rendering.shader.DynamicShaderManager.tryAddTypeAndShader
import org.bread_experts_group.breadlib.util.Color
import org.bread_experts_group.breadlib.util.PoseUtil.translateOnBlockSide

class TestBlockEntityRenderer(
	private val context: BlockEntityRendererProvider.Context
) : BlockEntityRenderer<TestBlockEntity> {
	companion object {
		private val format: VertexFormat = VertexFormat.builder()
			.add("Position", VertexFormatElement.POSITION)
			.add("UV0", VertexFormatElement.UV0)
			.add("Speed", ExtraVertexFormatElements.SPEED)
			.add("Direction", ExtraVertexFormatElements.DIRECTION)
			.build()
	}

	init {
		tryAddTypeAndShader("breadlib:squares_background", format, VertexFormat.Mode.QUADS)
	}

	override fun render(
		blockEntity: TestBlockEntity,
		partialTick: Float,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		pPackedLight: Int,
		pPackedOverlay: Int
	) {
		val rainbowType = getType("breadlib:squares_background") ?: return
		poseStack.translateOnBlockSide(blockEntity.blockState, 0.0, 0.0, 0.0)
		simpleQuad(poseStack, bufferSource, rainbowType, Color.WHITE, Vec2(2f, 2f)) { consumer ->
			consumer.putFloat(ExtraVertexFormatElements.SPEED, 1000f)
			consumer.putVec2(ExtraVertexFormatElements.DIRECTION, Vec2(1f, -1f))
		}
	}

	override fun shouldRenderOffScreen(blockEntity: TestBlockEntity): Boolean = true
	override fun shouldRender(blockEntity: TestBlockEntity, cameraPos: Vec3): Boolean = true
}