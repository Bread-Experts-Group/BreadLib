package org.bread_experts_group.breadlib.platform

import net.fabricmc.fabric.api.transfer.v1.transaction.TransactionContext
import org.bread_experts_group.breadlib.capability.BlockEnergyCapability
import org.bread_experts_group.breadlib.capability.EnergyPacket
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlockEntity
import org.bread_experts_group.breadlib.registry.RegistryProvider.Companion.getBlockEntityTypes
import team.reborn.energy.api.EnergyStorage

object PlatformInitialization {
	fun registerCapabilities(modID: String) {
		val bet = getBlockEntityTypes(modID)
		// todo band-aid so the example mod doesn't fail to load
		val techRebornLoaded = PlatformServices.PLATFORM.isModLoaded("tech_reborn")
		bet.applicableBlocks.forEach { blBlock ->
			if (BlockEnergyCapability::class.java.isAssignableFrom(blBlock.blockEntity) && techRebornLoaded) EnergyStorage.SIDED.registerForBlockEntity(
				{ be, side ->
					be as BreadLibBlockEntity
					if (side == null || (be.capabilitySides[BlockEnergyCapability::class.java] ?: error("")).contains(side)) {
						be as BlockEnergyCapability
						object : EnergyStorage {
							override fun insert(maxAmount: Long, transaction: TransactionContext): Long {
								val initial = amount
								val pkt = EnergyPacket(maxAmount.toInt())
								val insert = be.push(side, pkt, true).energy
								transaction.addCloseCallback { _, result ->
									if (result.wasCommitted()) be.push(side, pkt, false)
								}
								return initial + insert
							}

							override fun extract(maxAmount: Long, transaction: TransactionContext): Long {
								val initial = amount
								val pkt = EnergyPacket(maxAmount.toInt())
								val extract = be.pull(side, pkt, true).energy
								transaction.addCloseCallback { _, result ->
									if (result.wasCommitted()) be.pull(side, pkt, false)
								}
								return initial + extract
							}

							override fun getAmount(): Long = be.pull(side, null, false).energy.toLong()
							override fun getCapacity(): Long = be.pull(side, null, true).energy.toLong()
						}
					} else null
				},
				bet.getType(blBlock.blockEntity) ?: throw IllegalStateException("Unable to find BlockEntityType for ${blBlock.blockEntity} for energy capability registration")
			)
		}
	}
}