package org.bread_experts_group.breadlib.test

import net.minecraft.client.multiplayer.ClientLevel
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
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.LevelChunk
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import org.bread_experts_group.breadlib.capability.BlockEnergyCapability
import org.bread_experts_group.breadlib.capability.EnergyPacket
import org.bread_experts_group.breadlib.extensions.block.BlockProperties
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlock
import org.bread_experts_group.breadlib.platform.capability
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

	private fun getNeighbors(
		level: BlockGetter,
		pos: BlockPos
	) = Direction.entries.filterTo(EnumSet.noneOf(Direction::class.java)) { neighborDirection ->
		val neighborPos = pos.relative(neighborDirection)
		val neighborState = level.getBlockState(neighborPos)
		if (neighborState.block == BlocksTest.MP_CABLE.get()) return@filterTo true

		if (neighborState.hasBlockEntity() && level is Level) {
			val neighborEntity = level
				.getChunkAt(neighborPos)
				.getBlockEntity(neighborPos, LevelChunk.EntityCreationType.CHECK) ?: return@filterTo false
			val energy = neighborEntity.capability<BlockEnergyCapability>(neighborDirection)
            return@filterTo energy != null
        }
		false
	}

	class CableCapabilityProvider : BlockEnergyCapability {
		override fun pull(side: Direction?, what: EnergyPacket?, simulate: Boolean): EnergyPacket {
			println("Not yet implemented: $side, $what, $simulate")
			return EnergyPacket(0)
		}

		override fun push(side: Direction?, what: EnergyPacket, simulate: Boolean): EnergyPacket {
			println("Not yet implemented: $side, $what, $simulate")
			return EnergyPacket(0)
		}
	}

	override val capabilityProvider: Class<out Any> = CableCapabilityProvider::class.java

	override val meshProvider: MeshProvider by lazy {
		MeshProvider { state, pos, level, poseStack, vertexConsumer, randomSource ->
			val sprite = minecraft!!.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(
				ResourceLocation.withDefaultNamespace("block/iron_block")
			)

			val presentNeighbors = getNeighbors(level, pos)
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

	private companion object {
		const val BLOCK_DIVISIONS = 1.0 / 16.0

		const val BY_6 = BLOCK_DIVISIONS * 6   // cap
		const val BY_10 = BLOCK_DIVISIONS * 10 // cap

		const val BY_16 = BLOCK_DIVISIONS * 16 // run
		// run (divisions * 0)
	}

	override fun getShape(
		state: BlockState,
		level: BlockGetter,
		pos: BlockPos,
		context: CollisionContext
	): VoxelShape {
		// TODO: This allows the cable to update immediately to local changes,
		// TODO: but is costly (especially when loading many many cables),
		// TODO: it may be prudent to find a better solution.
		if (level is ClientLevel) minecraft!!.levelRenderer.setBlocksDirty(
			pos.x, pos.y, pos.z,
			pos.x, pos.y, pos.z
		)

		val presentNeighbors = getNeighbors(level, pos)
		var shape = Shapes.create(
			BY_6, BY_6, BY_6,
			BY_10, BY_10, BY_10
		)

		for (pNeighbor in presentNeighbors) when (pNeighbor) {
			// NEGATIVE AXIS
			Direction.WEST -> shape = Shapes.join(
				shape,
				Shapes.create(
					0.0, BY_6, BY_6,   // D B B
					BY_6, BY_10, BY_10 // B A A
				),
				BooleanOp.OR
			)

			Direction.DOWN -> shape = Shapes.join(
				shape,
				Shapes.create(
					BY_6, 0.0, BY_6,   // B D B
					BY_10, BY_6, BY_10 // A B A
				),
				BooleanOp.OR
			)

			Direction.NORTH -> shape = Shapes.join(
				shape,
				Shapes.create(
					BY_6, BY_6, 0.0,   // B B D
					BY_10, BY_10, BY_6 // A A B
				),
				BooleanOp.OR
			)

			// POSITIVE AXIS
			Direction.EAST -> shape = Shapes.join(
				shape,
				Shapes.create(
					BY_10, BY_6, BY_6,   // A B B
					BY_16, BY_10, BY_10 // C A A
				),
				BooleanOp.OR
			)

			Direction.UP -> shape = Shapes.join(
				shape,
				Shapes.create(
					BY_6, BY_10, BY_6,   // B A B
					BY_10, BY_16, BY_10 // A C A
				),
				BooleanOp.OR
			)

			Direction.SOUTH -> shape = Shapes.join(
				shape,
				Shapes.create(
					BY_6, BY_6, BY_10,   // B B A
					BY_10, BY_10, BY_16 // A A C
				),
				BooleanOp.OR
			)
		}

		return shape
	}

	override fun propagatesSkylightDown(state: BlockState, level: BlockGetter, pos: BlockPos): Boolean = true
	override fun getShadeBrightness(state: BlockState, level: BlockGetter, pos: BlockPos): Float = 1f
}