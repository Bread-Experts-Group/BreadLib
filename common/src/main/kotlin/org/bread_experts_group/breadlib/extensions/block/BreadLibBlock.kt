package org.bread_experts_group.breadlib.extensions.block

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import net.minecraft.core.BlockPos
import net.minecraft.util.RandomSource
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.Property
import net.minecraft.world.ticks.TickPriority

abstract class BreadLibBlock(
	blockProperties: Properties,
	private val scheduleTick: Int? = null,
	private val scheduleTickPriority: TickPriority = TickPriority.NORMAL
) : Block(blockProperties) {
	protected abstract fun breadLibProperties(): BlockProperties

	init {
		var stateDefinition = this.stateDefinition.any()
		breadLibProperties().getPropertiesAndDefaultValues().forEach { (property, defaultValue) ->
			@Suppress("UNCHECKED_CAST")
			stateDefinition = stateDefinition.setValue(
				property as Property<Comparable<Any>>,
				defaultValue as Comparable<Any>
			)
		}
		this.registerDefaultState(stateDefinition)
	}

	override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, movedByPiston: Boolean) {
		if (scheduleTick != null) level.scheduleTick(pos, this, scheduleTick, scheduleTickPriority)
	}

	final override fun getStateForPlacement(
		context: BlockPlaceContext
	): BlockState {
		var state = this.defaultBlockState()
		breadLibProperties().getPropertiesAndOnPlacement().forEach { (property, onPlacement) ->
			@Suppress("UNCHECKED_CAST")
			state = state.setValue(
				property as Property<Comparable<Any>>,
				onPlacement(context) as Comparable<Any>
			)
		}
		return state
	}

	final override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block, BlockState>) {
		breadLibProperties().getProperties().forEach(builder::add)
	}

	open val generateQuads: ((
		state: BlockState,
		pos: BlockPos,
		level: Level,
		poseStack: PoseStack,
		vertexConsumer: VertexConsumer,
		randomSource: RandomSource
	) -> Unit)? = null
}