package org.bread_experts_group.breadlib.platform

import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.BlockEntity
import org.bread_experts_group.breadlib.capability.base.Capability
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlockEntity

class FabricCapabilityHelper : ICapabilityHelper {
	override fun capabilitiesChanged(blockEntity: BreadLibBlockEntity) {
	}

	override fun <C : Capability<*>> capability(
		blockEntity: BlockEntity,
		side: Direction?,
		clazz: Class<C>
	): C? {
		TODO("Not yet implemented")
	}

	override fun <T : Capability<*>> installCapabilityConverter(
		forC: Class<T>,
		to: (BlockEntity, Direction?) -> T?
	) {
		TODO("Not yet implemented")
	}
}