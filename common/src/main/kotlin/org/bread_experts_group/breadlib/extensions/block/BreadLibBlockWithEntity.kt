package org.bread_experts_group.breadlib.extensions.block

import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.EntityBlock
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.ticks.TickPriority
import org.bread_experts_group.breadlib.platform.ApplicationSide
import org.bread_experts_group.breadlib.platform.PlatformServices
import org.bread_experts_group.breadlib.registry.RegistryProvider.Companion.getBlockEntityTypes
import org.jetbrains.annotations.ApiStatus
import java.lang.reflect.Constructor

abstract class BreadLibBlockWithEntity<BE : BlockEntity>(
	@ApiStatus.Internal
	val blockEntity: Class<BE>,
	blockProperties: Properties,
	scheduleTick: Int? = null,
	scheduleTickPriority: TickPriority = TickPriority.NORMAL,
	modID: String
) : BreadLibBlock(blockProperties, scheduleTick, scheduleTickPriority), EntityBlock {
	private val beConstructor = blockEntity.getConstructor(BlockPos::class.java, BlockState::class.java)
	private val commonTick = Tickable.Common::class.java.isAssignableFrom(blockEntity)

	init {
		val bet = getBlockEntityTypes(modID)
		if (bet.getType(blockEntity) == null) bet.register<BlockEntityType<*>>("test_${System.currentTimeMillis()}") {
			@Suppress("UNCHECKED_CAST")
			create(blockEntity as Class<BlockEntity>, (beConstructor as Constructor<BlockEntity>)::newInstance).also { builder ->
				if (PlatformServices.PLATFORM.side == ApplicationSide.CLIENT) {
					blockEntityRenderer()?.let { renderer -> builder.withRenderer { renderer(it) as BlockEntityRenderer<BlockEntity> } }
				}
			}
		}
	}

	final override fun newBlockEntity(p0: BlockPos, p1: BlockState): BlockEntity = beConstructor.newInstance(p0, p1)
	open fun blockEntityRenderer(): ((BlockEntityRendererProvider.Context) -> BlockEntityRenderer<BE>)? = null

	private val tickerServer = if (Tickable.Server::class.java.isAssignableFrom(blockEntity))
		if (commonTick) BlockEntityTicker<BE> { level, pos, state, entity ->
			(entity as Tickable.Server).serverTick(level as ServerLevel, pos, state)
			(entity as Tickable.Common).tick(level, pos, state)
		} else BlockEntityTicker<BE> { level, pos, state, entity ->
			(entity as Tickable.Server).serverTick(level as ServerLevel, pos, state)
		}
	else null

	private val tickerClient = if (Tickable.Client::class.java.isAssignableFrom(blockEntity))
		if (commonTick) BlockEntityTicker<BE> { level, pos, state, entity ->
			(entity as Tickable.Client).clientTick(level as ClientLevel, pos, state)
			(entity as Tickable.Common).tick(level, pos, state)
		} else BlockEntityTicker<BE> { level, pos, state, entity ->
			(entity as Tickable.Client).clientTick(level as ClientLevel, pos, state)
		}
	else null

	private val tickerCommon = if (commonTick)
		BlockEntityTicker<BE> { level, pos, state, entity ->
			(entity as Tickable.Common).tick(level, pos, state)
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