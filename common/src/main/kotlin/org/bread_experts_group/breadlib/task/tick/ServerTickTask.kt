package org.bread_experts_group.breadlib.task.tick

import net.minecraft.server.level.ServerLevel
import org.bread_experts_group.breadlib.task.FireSide
import org.bread_experts_group.breadlib.task.SidedTask

class ServerTickTask(val level: ServerLevel, side: FireSide) : SidedTask(side)