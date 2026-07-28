package org.bread_experts_group.breadlib.capability.base

interface Capability<T : Any> {
	fun pull(what: T?, simulate: Boolean): T
	fun push(what: T, simulate: Boolean): T
}