package org.bread_experts_group.breadlib.capability.base

import net.minecraft.core.Direction

interface BlockCapability<T : Any> : Capability<T> {
	override fun pull(what: T?, simulate: Boolean): T = pull(null, what, simulate)
	override fun push(what: T, simulate: Boolean): T = push(null, what, simulate)
	fun pull(side: Direction?, what: T?, simulate: Boolean): T
	fun push(side: Direction?, what: T, simulate: Boolean): T
}