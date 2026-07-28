package org.bread_experts_group.breadlib.platform

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.metadata.ModDependency
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlockEntity
import java.nio.file.Path

class FabricPlatformHelper : IPlatformHelper {
	private val fabricLoader = FabricLoader.getInstance()

	override val platformName: String = "Fabric"
	override val configDir: Path
		get() = fabricLoader.configDir
	override val gameDir: Path
		get() = fabricLoader.gameDir
	override val environmentKind: EnvironmentKind
		get() = if (fabricLoader.isDevelopmentEnvironment) EnvironmentKind.DEVELOPMENT
		else EnvironmentKind.RELEASE
	override val side: ApplicationSide
		get() = if (fabricLoader.environmentType == EnvType.CLIENT) ApplicationSide.CLIENT
		else ApplicationSide.SERVER

	override fun isModLoaded(modId: String): Boolean = fabricLoader.isModLoaded(modId)

	override fun getModInfo(modId: String): ModInfo {
		check(isModLoaded(modId)) { "Mod $modId is not loaded, cannot retrieve info." }
		val container = fabricLoader.getModContainer(modId).get()
		val metadata = container.metadata
		val dependencies = metadata.dependencies.filter { it.kind == ModDependency.Kind.DEPENDS }.map { it.modId }
		val version = metadata.version.friendlyString
		val jarPath = container.origin.paths.first()

		return ModInfo(modId, metadata.description, version, dependencies, jarPath)
	}

	override fun sendToServer(payload: CustomPacketPayload) {
		ClientPlayNetworking.send(payload)
	}

	override fun sendToAllPlayers(payload: CustomPacketPayload, level: ServerLevel) {
		for (player in PlayerLookup.all(level.server)) ServerPlayNetworking.send(player, payload)
	}

	override fun sendToPlayersTrackingChunk(payload: CustomPacketPayload, level: ServerLevel, pos: ChunkPos) {
		for (player in PlayerLookup.tracking(level, pos)) ServerPlayNetworking.send(player, payload)
	}

	override fun sendToPlayersInDimension(payload: CustomPacketPayload, level: ServerLevel) {
		for (player in PlayerLookup.world(level)) ServerPlayNetworking.send(player, payload)
	}

	override fun capabilitiesChanged(blockEntity: BreadLibBlockEntity) {
	}
}