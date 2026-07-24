package org.bread_experts_group.breadlib.task.input

import org.bread_experts_group.breadlib.task.Task

class KeyboardTask(val button: Int, val scanCode: Int, val action: Int, val modifiers: Int) : Task()
