package org.bread_experts_group.breadlib

import net.minecraft.core.Direction
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod
import net.neoforged.neoforge.capabilities.BlockCapability
import net.neoforged.neoforge.capabilities.Capabilities
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent
import net.neoforged.neoforge.data.event.GatherDataEvent
import net.neoforged.neoforge.registries.RegisterEvent
import org.bread_experts_group.breadlib.BreadLib.init
import org.bread_experts_group.breadlib.capability.BlockEnergyCapability
import org.bread_experts_group.breadlib.capability.EnergyPacket
import org.bread_experts_group.breadlib.capability.base.Capability
import org.bread_experts_group.breadlib.platform.NeoForgeGenerateDataTask
import org.bread_experts_group.breadlib.platform.PlatformInitialization
import org.bread_experts_group.breadlib.platform.PlatformServices
import org.bread_experts_group.breadlib.registry.RegistryProvider
import org.bread_experts_group.breadlib.task.TaskManager

@Mod(BreadLib.MOD_ID)
class BreadLibNeoForge(eventBus: IEventBus) {
	companion object {
		@Suppress("TYPE_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
		fun <T> registerContent(provider: RegistryProvider<T>, event: RegisterEvent) {
			event.register(provider.key) { helper ->
				provider.entries.forEach { (key, value) ->
					helper.register(key.name, value.get())
					key.bind()
				}
				provider.freeze()
			}
		}

		fun registerContent(eventBus: IEventBus) {
			eventBus.addListener { event: RegisterEvent ->
				for ((_, registries) in RegistryProvider.providers) {
					for ((_, registry) in registries) this.registerContent(registry, event)
				}
			}
		}
	}

	init {
		PlatformServices.PLATFORM.installCapabilityConverter(
			BlockEnergyCapability::class.java,
		) { blockEntity, direction ->
			val level = blockEntity.level ?: return@installCapabilityConverter null
			@Suppress("TYPE_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
			val storage = level.getCapability(
				Capabilities.EnergyStorage.BLOCK, blockEntity.blockPos,
				direction?.opposite
			) ?: return@installCapabilityConverter null
			object : BlockEnergyCapability {
				override fun pull(side: Direction?, what: EnergyPacket?, simulate: Boolean): EnergyPacket {
					TODO("Not yet implemented")
				}

				override fun push(side: Direction?, what: EnergyPacket, simulate: Boolean): EnergyPacket {
					TODO("Not yet implemented")
				}
			}
		}

		eventBus.addListener { event: GatherDataEvent ->
			TaskManager.runTasks(NeoForgeGenerateDataTask(event))

//			val generator = event.generator
//			val packOutput = generator.packOutput
//			TaskManager.runTasks(BootstrapDatapackEntriesTask()).getSuppliers().forEach { (modID, supplier) ->
//				val builder = RegistrySetBuilder().also { supplier(it) }
//				val provider = DatapackBuiltinEntriesProvider(
//					packOutput,
//					event.lookupProvider,
//					builder,
//					setOf(modID)
//				)
//				generator.addProvider(true, provider)
//			}
		}
		eventBus.addListener { event: RegisterCapabilitiesEvent -> PlatformInitialization.registerCapabilities(event, BreadLib.MOD_ID) }

		BreadLib.LOGGER.info("Hello NeoForge world!")
		init()
		registerContent(eventBus)
		NeoEvents.registerEvents(eventBus)
		NeoForgeNetworking.registerPackets(eventBus)
	}
}
