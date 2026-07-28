package org.bread_experts_group.breadlib.platform

import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.loading.FMLLoader
import net.minecraftforge.fml.loading.FMLPaths
import net.minecraftforge.network.PacketDistributor
import org.bread_experts_group.breadlib.ForgeNetworking
import java.nio.file.Path

class ForgePlatformHelper : IPlatformHelper {
	override val platformName: String = "Forge"
	override val configDir: Path
		get() = FMLPaths.CONFIGDIR.get()
	override val gameDir: Path
		get() = FMLPaths.GAMEDIR.get()
	override val environmentKind: EnvironmentKind
		get() = if (FMLLoader.isProduction()) EnvironmentKind.RELEASE else EnvironmentKind.DEVELOPMENT
	override val side: ApplicationSide
		get() = if (FMLLoader.getDist() == Dist.CLIENT) ApplicationSide.CLIENT else ApplicationSide.SERVER

	override fun isModLoaded(modId: String): Boolean = ModList.get().isLoaded(modId)

	override fun getModInfo(modId: String): ModInfo {
		val container = ModList.get().getModContainerById(modId).get()
		val info = container.modInfo
		val dependencies = info.dependencies.map { it.modId }
		val path = info.owningFile.file.findResource("/")
		val version = info.owningFile.versionString()

		return ModInfo(modId, info.description, version, dependencies, path)
	}

	override fun sendToServer(payload: CustomPacketPayload) {
		ForgeNetworking.checkChannelNotNull()
		ForgeNetworking.NETWORK_CHANNEL.send(payload, PacketDistributor.SERVER.noArg())
	}

	override fun sendToAllPlayers(payload: CustomPacketPayload, level: ServerLevel) {
		ForgeNetworking.checkChannelNotNull()
		ForgeNetworking.NETWORK_CHANNEL.send(payload, PacketDistributor.ALL.noArg())
	}

	override fun sendToPlayersTrackingChunk(payload: CustomPacketPayload, level: ServerLevel, pos: ChunkPos) {
		ForgeNetworking.checkChannelNotNull()
		for (player in level.chunkSource.chunkMap.getPlayers(pos, false)) {
			ForgeNetworking.NETWORK_CHANNEL.send(payload, PacketDistributor.PLAYER.with(player))
		}
	}

	override fun sendToPlayersInDimension(payload: CustomPacketPayload, level: ServerLevel) {
		ForgeNetworking.checkChannelNotNull()
		ForgeNetworking.NETWORK_CHANNEL.send(payload, PacketDistributor.DIMENSION.with(level.dimension()))
	}
}