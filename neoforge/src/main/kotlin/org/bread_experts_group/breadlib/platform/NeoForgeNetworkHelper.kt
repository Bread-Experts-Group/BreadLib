package org.bread_experts_group.breadlib.platform

import net.minecraft.client.Minecraft
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.neoforged.neoforge.network.PacketDistributor

class NeoForgeNetworkHelper : INetworkHelper {
	override val client: Minecraft
		get() = trueClient.get() ?: throw IllegalStateException("Not running on ${ApplicationSide.CLIENT}.")
	override val server: MinecraftServer
		get() = trueServer.get() ?: throw IllegalStateException("Not running on ${ApplicationSide.SERVER}.")

	internal val trueSide: ThreadLocal<ApplicationSide?> = ThreadLocal.withInitial { null }
	internal val trueClient: ThreadLocal<Minecraft?> = ThreadLocal.withInitial { null }
	internal val trueServer: ThreadLocal<MinecraftServer?> = ThreadLocal.withInitial { null }

	override val side: ApplicationSide
		get() = trueSide.get() ?: throw IllegalStateException("Side not initialized... network/level might not be setup")

	override fun sendToServer(payload: CustomPacketPayload) {
		PacketDistributor.sendToServer(payload)
	}

	override fun sendToAllPlayers(payload: CustomPacketPayload) {
		PacketDistributor.sendToAllPlayers(payload)
	}

	override fun sendToPlayersTrackingChunk(payload: CustomPacketPayload, level: ServerLevel, pos: ChunkPos) {
		PacketDistributor.sendToPlayersTrackingChunk(level, pos, payload)
	}

	override fun sendToPlayersInDimension(payload: CustomPacketPayload, level: ServerLevel) {
		PacketDistributor.sendToPlayersInDimension(level, payload)
	}
}