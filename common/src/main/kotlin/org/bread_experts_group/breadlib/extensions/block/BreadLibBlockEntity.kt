package org.bread_experts_group.breadlib.extensions.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadlib.registry.RegistryProvider.Companion.getBlockEntityTypes

private val WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)

abstract class BreadLibBlockEntity(pos: BlockPos, state: BlockState, modID: String) : BlockEntity(
	getBlockEntityTypes(modID).getType(WALKER.callerClass) ?: throw IllegalStateException("Unable to find BlockEntityType<${WALKER.callerClass.name}> for $modID"),
	pos, state
)