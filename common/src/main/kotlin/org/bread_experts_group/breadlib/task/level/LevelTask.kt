package org.bread_experts_group.breadlib.task.level

import net.minecraft.world.level.LevelAccessor
import org.bread_experts_group.breadlib.task.Task

sealed class LevelTask : Task() {
    class Load(val level: LevelAccessor) : LevelTask()
}