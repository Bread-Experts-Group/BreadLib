package org.bread_experts_group.breadlib.platform

import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.BlockEntity
import org.bread_experts_group.breadlib.capability.base.Capability
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlockEntity

class NeoForgeCapabilityHelper : ICapabilityHelper {
	override fun capabilitiesChanged(blockEntity: BreadLibBlockEntity) {
		blockEntity.level?.invalidateCapabilities(blockEntity.blockPos)
	}

	private val capConverter = mutableMapOf<Class<*>, (BlockEntity, Direction?) -> Capability<*>?>()

	@Suppress("UNCHECKED_CAST")
	override fun <C : Capability<*>> capability(
		blockEntity: BlockEntity, side: Direction?, clazz: Class<C>
	): C? {
		if (blockEntity is BreadLibBlockEntity && clazz.isAssignableFrom(blockEntity::class.java)) {
			val sides = blockEntity.capabilitySides[clazz] ?: return null
			if (side != null && side !in sides) return null
			return blockEntity as C
		}

		return (capConverter[clazz] ?: return null)(blockEntity, side) as C?
	}

	override fun <T : Capability<*>> installCapabilityConverter(forC: Class<T>, to: (BlockEntity, Direction?) -> T?) {
		capConverter[forC] = to
	}
}