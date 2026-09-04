package org.bread_experts_group.breadlib.platform

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.energy.IEnergyStorage
import org.bread_experts_group.breadlib.capability.BlockEnergyCapability
import org.bread_experts_group.breadlib.capability.EnergyPacket
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlock
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlockEntity
import org.bread_experts_group.breadlib.registry.RegistryProvider.Companion.get
import org.bread_experts_group.breadlib.registry.RegistryProvider.Companion.getBlockEntityTypes
import java.lang.reflect.Constructor

object PlatformInitialization {
	fun registerCapabilities(event: RegisterCapabilitiesEvent, modID: String) {
		val bet = getBlockEntityTypes(modID)

		bet.applicableBlocks.forEach { blBlock ->
			if (BlockEnergyCapability::class.java.isAssignableFrom(blBlock.blockEntity)) event.registerBlockEntity(
				Capabilities.EnergyStorage.BLOCK,
				bet.getType(blBlock.blockEntity) ?: throw IllegalStateException("Unable to find BlockEntityType for ${blBlock.blockEntity} for energy capability registration"),
			) { be, side ->
				be as BreadLibBlockEntity
				if (side == null || be.capabilitySides[BlockEnergyCapability::class.java]!!.contains(side)) {
					be as BlockEnergyCapability
					object : IEnergyStorage {
						override fun receiveEnergy(toReceive: Int, simulate: Boolean): Int = be.push(side, EnergyPacket(toReceive), simulate).energy
						override fun extractEnergy(toExtract: Int, simulate: Boolean): Int = be.pull(side, EnergyPacket(toExtract), simulate).energy
						override fun getEnergyStored(): Int = be.pull(side, null, false).energy
						override fun getMaxEnergyStored(): Int = be.pull(side, null, true).energy
						override fun canExtract(): Boolean = true
						override fun canReceive(): Boolean = true
					}
				} else null
			}
		}

		BuiltInRegistries.BLOCK.get(modID).entries.map { it.key.get() }.forEach { blBlock ->
			val provider = (blBlock as? BreadLibBlock)?.capabilityProvider ?: return@forEach
			if (BlockEnergyCapability::class.java.isAssignableFrom(provider)) event.registerBlock(
				Capabilities.EnergyStorage.BLOCK,
				{ level, pos, state, _, side ->
					val preCheck = blBlock.capabilityProviderPreCheck?.invoke(level, pos, state, side)
					if (preCheck == false) return@registerBlock null

					val constructorArguments = mutableSetOf<Any?>()
					var selectedConstructor: Constructor<*>? = null
					ctrLoop@ for (constructor in provider.constructors) {
						constructorArguments.clear()
						constructor.parameterTypes.forEach {
							val status = constructorArguments.add(
								when (it) {
									Level::class.java -> level
									BlockPos::class.java -> pos
									BlockState::class.java -> state
									Direction::class.java -> side
									else -> continue@ctrLoop
								}
							)
							if (!status) continue@ctrLoop
						}
						selectedConstructor = constructor
					}

					if (selectedConstructor == null) throw NullPointerException(
						"No suitable constructor found in $provider. Should have only 1 or 0 of ${Level::class.java}, ${BlockPos::class.java}, ${BlockState::class.java}, ${Direction::class.java} (side)."
					)
					val capProvider = selectedConstructor.newInstance(
						*constructorArguments.toTypedArray()
					) as BlockEnergyCapability
					object : IEnergyStorage {
						override fun receiveEnergy(toReceive: Int, simulate: Boolean): Int =
							capProvider.push(side, EnergyPacket(toReceive), simulate).energy

						override fun extractEnergy(toExtract: Int, simulate: Boolean): Int =
							capProvider.pull(side, EnergyPacket(toExtract), simulate).energy

						override fun getEnergyStored(): Int = capProvider.pull(side, null, false).energy
						override fun getMaxEnergyStored(): Int = capProvider.pull(side, null, true).energy
						override fun canExtract(): Boolean = true
						override fun canReceive(): Boolean = true
					}
				}, blBlock
			)
		}
	}
}