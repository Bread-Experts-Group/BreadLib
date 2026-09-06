package org.bread_experts_group.breadlib.platform

import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.BlockEntity
import org.bread_experts_group.breadlib.capability.base.Capability
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlockEntity

interface ICapabilityHelper {
	companion object {
		inline fun <reified C : Capability<*>> BlockEntity.capability(side: Direction? = null): C? {
			return PlatformServices.CAPABILITY.capability(this, side, C::class.java)
		}
	}

	fun capabilitiesChanged(blockEntity: BreadLibBlockEntity)

	fun <C : Capability<*>> capability(blockEntity: BlockEntity, side: Direction? = null, clazz: Class<C>): C?

	fun <T : Capability<*>> installCapabilityConverter(forC: Class<T>, to: (BlockEntity, Direction?) -> T?)
}