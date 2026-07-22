package org.bread_experts_group.breadlib.extensions.block

import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

sealed interface Tickable {
	interface Common : Tickable {
		fun tick(level: Level, pos: BlockPos, state: BlockState)
	}

	interface Server : Tickable {
		fun serverTick(level: ServerLevel, pos: BlockPos, state: BlockState)
	}

	interface Client : Tickable {
		fun clientTick(level: Level, pos: BlockPos, state: BlockState)
	}
}