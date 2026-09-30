package org.bread_experts_group.breadlib.task.tick

import net.minecraft.server.level.ServerLevel
import org.bread_experts_group.breadlib.task.FireSide
import org.bread_experts_group.breadlib.task.Task

class ServerTickTask(val level: ServerLevel, side: FireSide) : Task(side)