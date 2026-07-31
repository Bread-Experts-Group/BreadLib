package org.bread_experts_group.breadlib.test

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.BlockElementFace
import net.minecraft.client.renderer.block.model.BlockElementRotation
import net.minecraft.client.renderer.block.model.BlockFaceUV
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.resources.model.BlockModelRotation
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.InventoryMenu
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.extensions.block.BlockProperties
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlock
import org.bread_experts_group.breadlib.rendering.model.MeshProvider
import org.bread_experts_group.breadlib.rendering.model.ModelUtil.makeVertices
import org.bread_experts_group.breadlib.rendering.model.ModelUtil.model
import org.bread_experts_group.breadlib.rendering.model.ModelUtil.setupShape
import org.bread_experts_group.breadlib.util.minecraft
import org.joml.Vector3f

private val blockProperties = BlockProperties()

class MultipartCableBlock : BreadLibBlock(Properties.of()) {
	override fun breadLibProperties(): BlockProperties = blockProperties
	override val meshProvider: MeshProvider by lazy {
		MeshProvider { state, pos, level, poseStack, vertexConsumer, randomSource ->
			BreadLib.LOGGER.info("CableBlock Custom Render: $pos, ${state.block}")

			val sprite = minecraft!!.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(
				ResourceLocation.withDefaultNamespace("block/dirt")
			)

			val a = listOf(
				BakedQuad(
					makeVertices(
						BlockFaceUV(floatArrayOf(0f, 0f, 16f, 16f), 0),
						sprite,
						Direction.UP,
						setupShape(Vector3f(0f, 0f, 0f), Vector3f(16f, 16f, 16f)),
						BlockModelRotation.X0_Y0.rotation,
						BlockElementRotation(Vector3f(), Direction.Axis.X, 0f, false)
					),
					BlockElementFace.NO_TINT, Direction.UP,
					sprite,
					true
				)
			).model()
			minecraft!!.blockRenderer.modelRenderer.tesselateWithAO(
				level, a, state, pos, poseStack, vertexConsumer, true, randomSource, 0,
				OverlayTexture.NO_OVERLAY
			)
		}
	}
}