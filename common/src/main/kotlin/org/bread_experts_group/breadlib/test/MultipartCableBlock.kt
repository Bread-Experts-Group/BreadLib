package org.bread_experts_group.breadlib.test

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.BlockElementFace
import net.minecraft.client.renderer.block.model.BlockFaceUV
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.resources.model.BlockModelRotation
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
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

class MultipartCableBlock : BreadLibBlock(Properties.of().noOcclusion()) {
	override fun breadLibProperties(): BlockProperties = blockProperties
	override val meshProvider: MeshProvider by lazy {
		MeshProvider { state, pos, level, poseStack, vertexConsumer, randomSource ->
			BreadLib.LOGGER.info("CableBlock Custom Render: $pos, ${state.block}")

			val sprite = minecraft!!.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(
				ResourceLocation.withDefaultNamespace("block/dirt")
			)

			val quads = mutableListOf<BakedQuad>()
			// Y (runs)
			quads.add(
				BakedQuad(
					makeVertices(
						BlockFaceUV(floatArrayOf(0f, 0f, 16f, 4f), 0),
						sprite,
						Direction.DOWN, // -Y
						setupShape(Vector3f(0f, 6f, 6f), Vector3f(16f, 6f, 10f)),
						BlockModelRotation.X0_Y0.rotation
					),
					BlockElementFace.NO_TINT, Direction.DOWN,
					sprite,
					true
				)
			)
			quads.add(
				BakedQuad(
					makeVertices(
						BlockFaceUV(floatArrayOf(0f, 0f, 16f, 4f), 0),
						sprite,
						Direction.UP, // +Y
						setupShape(Vector3f(0f, 10f, 6f), Vector3f(16f, 10f, 10f)),
						BlockModelRotation.X0_Y0.rotation
					),
					BlockElementFace.NO_TINT, Direction.UP,
					sprite,
					true
				)
			)
			// X (runs)
			quads.add(
				BakedQuad(
					makeVertices(
						BlockFaceUV(floatArrayOf(0f, 0f, 16f, 4f), 0),
						sprite,
						Direction.NORTH, // -Z
						setupShape(Vector3f(0f, 6f, 6f), Vector3f(16f, 10f, 6f)),
						BlockModelRotation.X0_Y0.rotation
					),
					BlockElementFace.NO_TINT, Direction.NORTH,
					sprite,
					true
				)
			)
			quads.add(
				BakedQuad(
					makeVertices(
						BlockFaceUV(floatArrayOf(0f, 0f, 16f, 4f), 0),
						sprite,
						Direction.SOUTH, // +Z
						setupShape(Vector3f(0f, 6f, 10f), Vector3f(16f, 10f, 10f)),
						BlockModelRotation.X0_Y0.rotation
					),
					BlockElementFace.NO_TINT, Direction.SOUTH,
					sprite,
					true
				)
			)
			// Z (caps)
			quads.add(
				BakedQuad(
					makeVertices(
						BlockFaceUV(floatArrayOf(0f, 0f, 4f, 4f), 0),
						sprite,
						Direction.EAST, // +X
						setupShape(Vector3f(16f, 6f, 6f), Vector3f(16f, 10f, 10f)),
						BlockModelRotation.X0_Y0.rotation
					),
					BlockElementFace.NO_TINT, Direction.EAST,
					sprite,
					true
				)
			)
			quads.add(
				BakedQuad(
					makeVertices(
						BlockFaceUV(floatArrayOf(0f, 0f, 4f, 4f), 0),
						sprite,
						Direction.WEST, // -X
						setupShape(Vector3f(0f, 6f, 6f), Vector3f(0f, 10f, 10f)),
						BlockModelRotation.X0_Y0.rotation
					),
					BlockElementFace.NO_TINT, Direction.WEST,
					sprite,
					true
				)
			)

//			Direction.entries.forEach { cableDirection ->
//
//			}
//			Direction.entries.forEach { connectedDirection ->
//				if (level.getBlockState(pos.relative(it)).block !is MultipartCableBlock) return@forEach
//			}
			minecraft!!.blockRenderer.modelRenderer.tesselateWithAO(
				level, quads.model(), state, pos, poseStack, vertexConsumer, true, randomSource, 0,
				OverlayTexture.NO_OVERLAY
			)
		}
	}

	override fun getVisualShape(
		state: BlockState,
		level: BlockGetter,
		pos: BlockPos,
		context: CollisionContext
	): VoxelShape = Shapes.empty()

	override fun propagatesSkylightDown(state: BlockState, level: BlockGetter, pos: BlockPos): Boolean = true
	override fun getShadeBrightness(state: BlockState, level: BlockGetter, pos: BlockPos): Float = 1f
}