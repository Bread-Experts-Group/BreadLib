package org.bread_experts_group.breadlib.platform

import net.minecraft.core.Direction
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.ModList
import net.neoforged.fml.loading.FMLLoader
import net.neoforged.fml.loading.FMLPaths
import net.neoforged.neoforge.network.PacketDistributor
import org.bread_experts_group.breadlib.capability.base.BlockCapability
import org.bread_experts_group.breadlib.capability.base.Capability
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlockEntity
import java.nio.file.Path

class NeoForgePlatformHelper : IPlatformHelper {
	override val platformName: String = "NeoForge"
	override val configDir: Path
		get() = FMLPaths.CONFIGDIR.get()
	override val gameDir: Path
		get() = FMLPaths.GAMEDIR.get()
	override val environmentKind: EnvironmentKind
		get() = if (FMLLoader.isProduction()) EnvironmentKind.RELEASE else EnvironmentKind.DEVELOPMENT
	override val side: ApplicationSide
		get() = if (FMLLoader.getDist() == Dist.CLIENT) ApplicationSide.CLIENT else ApplicationSide.SERVER

	override fun isModLoaded(modId: String): Boolean {
		return ModList.get().isLoaded(modId)
	}

	override fun getModInfo(modId: String): ModInfo {
		val container = ModList.get().getModContainerById(modId).get()
		val info = container.modInfo
		val dependencies = info.dependencies.map { it.modId }
		val path = info.owningFile.file.findResource("/")
		val version = info.owningFile.versionString()

		return ModInfo(modId, info.description, version, dependencies, path)
	}

	override fun sendToServer(payload: CustomPacketPayload) {
		PacketDistributor.sendToServer(payload)
	}

	override fun sendToAllPlayers(payload: CustomPacketPayload, level: ServerLevel) {
		PacketDistributor.sendToAllPlayers(payload)
	}

	override fun sendToPlayersTrackingChunk(payload: CustomPacketPayload, level: ServerLevel, pos: ChunkPos) {
		PacketDistributor.sendToPlayersTrackingChunk(level, pos, payload)
	}

	override fun sendToPlayersInDimension(payload: CustomPacketPayload, level: ServerLevel) {
		PacketDistributor.sendToPlayersInDimension(level, payload)
	}

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