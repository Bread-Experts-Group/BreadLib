package org.bread_experts_group.breadlib.platform

import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.minecraftforge.network.PacketDistributor
import org.bread_experts_group.breadlib.ForgeNetworking

class ForgeNetworkHelper : INetworkHelper {
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