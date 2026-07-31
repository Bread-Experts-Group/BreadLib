package org.bread_experts_group.breadlib.rendering.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlock
import org.bread_experts_group.breadlib.test.BlocksTest

typealias MeshProvider = (
	state: BlockState,
	pos: BlockPos,
	level: Level,
	poseStack: PoseStack,
	vertexConsumer: VertexConsumer,
	randomSource: RandomSource
) -> Unit

object CustomMeshProvider {
	private val meshes: MutableMap<BreadLibBlock, MeshProvider> = mutableMapOf()

	init {
		meshes[BlocksTest.TEST_BLOCK.get()] = { state, pos, level, poseStack, consumer, random ->
			BreadLib.LOGGER.info("TestBlock Custom Render: $pos, ${state.block}")
		}
		meshes[BlocksTest.MP_CABLE.get()] = { state, pos, level, poseStack, consumer, random ->
			BreadLib.LOGGER.info("CableBlock Custom Render: $pos, ${state.block}")
		}
	}

	fun get(block: BreadLibBlock): MeshProvider? = meshes[block]

	// todo we need to provide the game's buffer source to allow rendering with custom render types
	//  (which may or may not cause issues with the section compiler rendering one type at a time)
	//  maybe work out some method to inject *which* stage of rendering we render our stuff at (solid, translucent, cutout)
	//  and possibly introduce more rendering stages for custom types in the level renderer
	@JvmStatic
	fun renderMesh(
		state: BlockState,
		pos: BlockPos,
		level: Level,
		poseStack: PoseStack,
		vertexConsumer: VertexConsumer,
		randomSource: RandomSource
	): Boolean {
		this.get((state.block as BreadLibBlock))?.let {
			it(state, pos, level, poseStack, vertexConsumer, randomSource)
			return true
		}
		return false
	}
}