package org.bread_experts_group.breadlib.task

/**
 * Dictates if the running Task is firing before or after the main logic fires, or both if NONE is specified.
 */
enum class FireSide {
	PRE, POST, NONE
}