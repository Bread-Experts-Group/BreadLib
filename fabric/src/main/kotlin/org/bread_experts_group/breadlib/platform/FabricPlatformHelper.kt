package org.bread_experts_group.breadlib.platform

import net.fabricmc.api.EnvType
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import java.nio.file.Path

class FabricPlatformHelper : IPlatformHelper {
	override fun getPlatformName(): String = "Fabric"

	override fun isModLoaded(modId: String): Boolean = FabricLoader.getInstance().isModLoaded(modId)

	override fun getConfigDir(): Path = FabricLoader.getInstance().configDir

	override fun getGameDir(): Path = FabricLoader.getInstance().gameDir

	override fun getEnvironmentKind(): EnvironmentKind =
		if (FabricLoader.getInstance().isDevelopmentEnvironment) EnvironmentKind.DEVELOPMENT
		else EnvironmentKind.RELEASE

	override fun getSide(): ApplicationSide =
		if (FabricLoader.getInstance().environmentType == EnvType.CLIENT) ApplicationSide.CLIENT
		else ApplicationSide.SERVER

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
}