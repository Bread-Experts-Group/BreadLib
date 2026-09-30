package org.bread_experts_group.breadlib.task.tick

import net.minecraft.client.multiplayer.ClientLevel
import org.bread_experts_group.breadlib.task.FireSide
import org.bread_experts_group.breadlib.task.Task

class ClientTickTask(val level: ClientLevel, side: FireSide) : Task(side)
