package org.bread_experts_group.breadlib.extensions.block

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.ticks.TickPriority

private typealias BlockEntityTickL<BE, T> = BE.(level: T, pos: BlockPos, state: BlockState) -> Unit

abstract class BreadLibBlockWithEntity<BE : BlockEntity>(
	private val blockEntity: (pos: BlockPos, state: BlockState) -> BE,
	blockProperties: Properties,
	protected val commonTick: BlockEntityTickL<BE, Level>? = null,
	protected val serverTick: BlockEntityTickL<BE, ServerLevel>? = null,
	protected val clientTick: BlockEntityTickL<BE, ClientLevel>? = null,
	scheduleTick: Int? = null,
	scheduleTickPriority: TickPriority = TickPriority.NORMAL
) : BreadLibBlock(blockProperties, scheduleTick, scheduleTickPriority), EntityBlock {
	override fun newBlockEntity(p0: BlockPos, p1: BlockState): BlockEntity = blockEntity(p0, p1)

	private val tickerServer = if (serverTick != null)
		if (commonTick != null) BlockEntityTicker<BE> { level, pos, state, entity ->
			serverTick(entity, level as ServerLevel, pos, state)
			commonTick(entity, level, pos, state)
		} else BlockEntityTicker<BE> { level, pos, state, entity ->
			serverTick(entity, level as ServerLevel, pos, state)
		}
	else null

	private val tickerClient = if (clientTick != null)
		if (commonTick != null) BlockEntityTicker<BE> { level, pos, state, entity ->
			clientTick(entity, level as ClientLevel, pos, state)
			commonTick(entity, level, pos, state)
		} else BlockEntityTicker<BE> { level, pos, state, entity ->
			clientTick(entity, level as ClientLevel, pos, state)
		}
	else null

	private val tickerCommon = if (commonTick != null)
		BlockEntityTicker<BE> { level, pos, state, entity ->
			commonTick(entity, level, pos, state)
		}
	else null

	@Suppress("UNCHECKED_CAST")
	override fun <T : BlockEntity> getTicker(
		level: Level,
		state: BlockState,
		blockEntityType: BlockEntityType<T>
	): BlockEntityTicker<T>? = ((if (level is ServerLevel) tickerServer else tickerClient)
		?: tickerCommon) as? BlockEntityTicker<T>
}