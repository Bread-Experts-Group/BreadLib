package org.bread_experts_group.breadlib.extensions.block

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import org.bread_experts_group.breadlib.capability.base.Capability
import org.bread_experts_group.breadlib.platform.PlatformServices
import org.bread_experts_group.breadlib.registry.RegistryProvider.Companion.getBlockEntityTypes
import org.jetbrains.annotations.ApiStatus

private val WALKER = StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)

abstract class BreadLibBlockEntity(pos: BlockPos, state: BlockState, modID: String) : BlockEntity(
	getBlockEntityTypes(modID).getType(WALKER.callerClass) ?: throw IllegalStateException("Unable to find BlockEntityType<${WALKER.callerClass.name}> for $modID"),
	pos, state
) {
	private fun platformInformCapsChanged() {
		synchronized(capabilitySides) {
			PlatformServices.PLATFORM.capabilitiesChanged(this)
		}
	}

	@Suppress("UNCHECKED_CAST")
	@ApiStatus.Internal
	val capabilitySides: Map<Class<out Capability<*>>, MutableSet<Direction>> = this::class.java.interfaces
		.filter { Capability::class.java.isAssignableFrom(it) }
		.associateWith {
			object : MutableSet<Direction> {
				private val set = Direction.entries.toMutableSet()
				override fun iterator(): MutableIterator<Direction> = object : MutableIterator<Direction> {
					val iterator = set.iterator()
					override fun remove() {
						iterator.remove()
						platformInformCapsChanged()
					}

					override fun next(): Direction = iterator.next()
					override fun hasNext(): Boolean = iterator.hasNext()
				}

				override fun add(element: Direction): Boolean = set.add(element).also { if (it) platformInformCapsChanged() }
				override fun remove(element: Direction): Boolean = set.remove(element).also { if (it) platformInformCapsChanged() }
				override fun addAll(elements: Collection<Direction>): Boolean = set.addAll(elements).also { if (it) platformInformCapsChanged() }
				override fun removeAll(elements: Collection<Direction>): Boolean = set.removeAll(elements.toSet()).also { if (it) platformInformCapsChanged() }
				override fun retainAll(elements: Collection<Direction>): Boolean = set.retainAll(elements.toSet()).also { if (it) platformInformCapsChanged() }
				override fun clear() {
					val before = set.isNotEmpty()
					set.clear()
					if (before) platformInformCapsChanged()
				}

				override val size: Int
					get() = set.size

				override fun isEmpty(): Boolean = set.isEmpty()
				override fun contains(element: Direction): Boolean = set.contains(element)
				override fun containsAll(elements: Collection<Direction>): Boolean = set.containsAll(elements)
			}
		} as Map<Class<out Capability<*>>, MutableSet<Direction>>
}