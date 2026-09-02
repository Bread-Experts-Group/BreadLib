package org.bread_experts_group.breadlib.test

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.block.model.BlockElementFace
import net.minecraft.client.renderer.block.model.BlockFaceUV
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.model.BlockModelRotation
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.InventoryMenu
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.BooleanOp
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
import java.util.EnumSet

private val blockProperties = BlockProperties()

class MultipartCableBlock : BreadLibBlock(Properties.of()) {
	override fun breadLibProperties(): BlockProperties = blockProperties

	/**
	 * Relative to block side, not world coordinates
	 */
	fun createRelativeRun(
		facing: Direction,
		depth: Float,
		startX: Float, startZ: Float,
		endX: Float, endZ: Float,
		sprite: TextureAtlasSprite
	): BakedQuad {
		val depth = if (facing.axisDirection == Direction.AxisDirection.NEGATIVE) depth else (16f - depth)
		val uv = BlockFaceUV(floatArrayOf(0f, 0f, endX - startX, endZ - startZ), 0)
		val shape = when (facing) {
			Direction.UP -> setupShape(Vector3f(startX, depth, startZ), Vector3f(endX, depth, endZ))
			Direction.DOWN -> setupShape(Vector3f(startX, depth, startZ), Vector3f(endX, depth, endZ))
			Direction.NORTH -> setupShape(Vector3f(startX, startZ, depth), Vector3f(endX, endZ, depth))
			Direction.SOUTH -> setupShape(Vector3f(startX, startZ, depth), Vector3f(endX, endZ, depth))
			Direction.EAST -> setupShape(Vector3f(depth, startZ, startX), Vector3f(depth, endZ, endX))
			Direction.WEST -> setupShape(Vector3f(depth, startZ, startX), Vector3f(depth, endZ, endX))
		}

		return BakedQuad(
			makeVertices(
				uv, sprite, facing, shape,
				BlockModelRotation.X0_Y0.rotation
			),
			BlockElementFace.NO_TINT, facing,
			sprite,
			true
		)
	}

	override val meshProvider: MeshProvider by lazy {
		MeshProvider { state, pos, level, poseStack, vertexConsumer, randomSource ->
			val sprite = minecraft!!.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(
				ResourceLocation.withDefaultNamespace("block/iron_block")
			)

			val presentNeighbors = Direction.entries.filterTo(EnumSet.noneOf(Direction::class.java)) {
				level.getBlockState(pos.relative(it)).block == BlocksTest.MP_CABLE.get()
			}
			val nonPresentNeighbors = EnumSet.complementOf(presentNeighbors)


			val quads = mutableListOf<BakedQuad>()

			for (npNeighbor in nonPresentNeighbors) quads.add(
				createRelativeRun(
					npNeighbor,
					6f, 6f, 6f, 10f, 10f,
					sprite
				)
			)
			for (pNeighbor in presentNeighbors) when (pNeighbor) {
				Direction.SOUTH, Direction.NORTH -> {
					val endZ: Float
					val startZ: Float
					if (pNeighbor.axisDirection == Direction.AxisDirection.POSITIVE) {
						endZ = 16f
						startZ = 10f
					} else {
						endZ = 6f
						startZ = 0f
					}
					quads.add(
						createRelativeRun(
							Direction.UP,
							6f, 6f, startZ, 10f, endZ,
							sprite
						)
					)
					quads.add(
						createRelativeRun(
							Direction.DOWN,
							6f, 6f, startZ, 10f, endZ,
							sprite
						)
					)
					quads.add(
						createRelativeRun(
							Direction.EAST,
							6f, startZ, 6f, endZ, 10f,
							sprite
						)
					)
					quads.add(
						createRelativeRun(
							Direction.WEST,
							6f, startZ, 6f, endZ, 10f,
							sprite
						)
					)
				}

				Direction.EAST, Direction.WEST -> {
					val endX: Float
					val startX: Float
					if (pNeighbor.axisDirection == Direction.AxisDirection.POSITIVE) {
						endX = 16f
						startX = 10f
					} else {
						endX = 6f
						startX = 0f
					}
					quads.add(
						createRelativeRun(
							Direction.UP,
							6f, startX, 6f, endX, 10f,
							sprite
						)
					)
					quads.add(
						createRelativeRun(
							Direction.DOWN,
							6f, startX, 6f, endX, 10f,
							sprite
						)
					)
					quads.add(
						createRelativeRun(
							Direction.NORTH,
							6f, startX, 6f, endX, 10f,
							sprite
						)
					)
					quads.add(
						createRelativeRun(
							Direction.SOUTH,
							6f, startX, 6f, endX, 10f,
							sprite
						)
					)
				}

				Direction.UP, Direction.DOWN -> {
					val endX: Float
					val startX: Float
					if (pNeighbor.axisDirection == Direction.AxisDirection.POSITIVE) {
						endX = 16f
						startX = 10f
					} else {
						endX = 6f
						startX = 0f
					}
					quads.add(
						createRelativeRun(
							Direction.WEST,
							6f, 6f, startX, 10f, endX,
							sprite
						)
					)
					quads.add(
						createRelativeRun(
							Direction.EAST,
							6f, 6f, startX, 10f, endX,
							sprite
						)
					)
					quads.add(
						createRelativeRun(
							Direction.NORTH,
							6f, 6f, startX, 10f, endX,
							sprite
						)
					)
					quads.add(
						createRelativeRun(
							Direction.SOUTH,
							6f, 6f, startX, 10f, endX,
							sprite
						)
					)
				}
			}

			minecraft!!.blockRenderer.modelRenderer.tesselateWithAO(
				level, quads.model(), state, pos, poseStack, vertexConsumer, true, randomSource, 0,
				OverlayTexture.NO_OVERLAY
			)
		}
	}

	override fun getShape(
		state: BlockState,
		level: BlockGetter,
		pos: BlockPos,
		context: CollisionContext
	): VoxelShape {
		val divisions = 1.0 / 16.0

		val presentNeighbors = EnumSet.noneOf(Direction::class.java)

		Direction.entries.forEach {
			val present = level.getBlockState(pos.relative(it)).block == BlocksTest.MP_CABLE.get()
			if (present) presentNeighbors.add(it)
		}

		var shape = Shapes.create(
			divisions * 6, divisions * 6, divisions * 6,
			divisions * 10, divisions * 10, divisions * 10
		)

		for (pNeighbor in presentNeighbors) when (pNeighbor) {
			Direction.UP -> shape = Shapes.join(
				shape,
				Shapes.create(
					divisions * 6, divisions * 10, divisions * 6,
					divisions * 10, divisions * 16, divisions * 10
				),
				BooleanOp.OR
			)

			Direction.DOWN -> shape = Shapes.join(
				shape,
				Shapes.create(
					divisions * 6, divisions * 0, divisions * 6,
					divisions * 10, divisions * 6, divisions * 10
				),
				BooleanOp.OR
			)

			Direction.EAST -> shape = Shapes.join(
				shape,
				Shapes.create(
					divisions * 10, divisions * 6, divisions * 6,
					divisions * 16, divisions * 10, divisions * 10
				),
				BooleanOp.OR
			)

			Direction.WEST -> shape = Shapes.join(
				shape,
				Shapes.create(
					divisions * 0, divisions * 6, divisions * 6,
					divisions * 6, divisions * 10, divisions * 10
				),
				BooleanOp.OR
			)

			Direction.NORTH -> shape = Shapes.join(
				shape,
				Shapes.create(
					divisions * 6, divisions * 6, divisions * 0,
					divisions * 10, divisions * 10, divisions * 6
				),
				BooleanOp.OR
			)

			Direction.SOUTH -> shape = Shapes.join(
				shape,
				Shapes.create(
					divisions * 6, divisions * 6, divisions * 10,
					divisions * 10, divisions * 10, divisions * 16
				),
				BooleanOp.OR
			)

			else -> {}
		}

		return shape
	}

	override fun propagatesSkylightDown(state: BlockState, level: BlockGetter, pos: BlockPos): Boolean = true
	override fun getShadeBrightness(state: BlockState, level: BlockGetter, pos: BlockPos): Float = 1f
}