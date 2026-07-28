package org.bread_experts_group.breadlib.platform

import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.energy.IEnergyStorage
import org.bread_experts_group.breadlib.capability.BlockEnergyCapability
import org.bread_experts_group.breadlib.capability.EnergyPacket
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlockEntity
import org.bread_experts_group.breadlib.registry.RegistryProvider.Companion.getBlockEntityTypes

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
	}
}