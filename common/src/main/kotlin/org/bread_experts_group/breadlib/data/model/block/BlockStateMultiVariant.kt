package org.bread_experts_group.breadlib.data.model.block

import org.bread_experts_group.breadlib.data.model.ObjectResourceLocation

class BlockStateMultiVariant(val variants: List<SingleVariant>) : BlockStateVariant {
	class SingleVariant(
		val model: ObjectResourceLocation,
		val x: Number? = null,
		val y: Number? = null,
		val z: Number? = null,
		val uvLock: Boolean? = null,
		val weight: Number? = null
	)
}