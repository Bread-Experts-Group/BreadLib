package org.bread_experts_group.breadlib.data.model.block

import org.bread_experts_group.breadlib.data.model.ObjectResourceLocation

class BlockStateSingleVariant(
	val model: ObjectResourceLocation,
	val x: Number? = null,
	val y: Number? = null,
	val z: Number? = null,
	val uvLock: Boolean? = null
) : BlockStateVariant