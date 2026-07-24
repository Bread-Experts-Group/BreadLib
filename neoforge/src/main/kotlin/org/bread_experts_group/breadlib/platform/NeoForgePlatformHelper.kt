package org.bread_experts_group.breadlib.platform

import com.google.common.eventbus.EventBus
import io.netty.buffer.ByteBuf
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.ChunkPos
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModList
import net.neoforged.fml.ModLoadingContext
import net.neoforged.fml.loading.FMLLoader
import net.neoforged.fml.loading.FMLPaths
import net.neoforged.fml.loading.FMLServiceProvider
import net.neoforged.neoforge.network.PacketDistributor
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent
import org.bread_experts_group.breadlib.network.NetworkDirection
import org.bread_experts_group.breadlib.network.payload.PayloadInfo
import java.nio.file.Path

class NeoForgePlatformHelper : IPlatformHelper {
	override fun getPlatformName(): String = "NeoForge"

	override fun getConfigDir(): Path = FMLPaths.CONFIGDIR.get()

	override fun getGameDir(): Path = FMLPaths.GAMEDIR.get()

	override fun isModLoaded(modId: String): Boolean {
		return ModList.get().isLoaded(modId)
	}

	override fun getEnvironmentKind(): EnvironmentKind =
		if (FMLLoader.isProduction()) EnvironmentKind.RELEASE else EnvironmentKind.DEVELOPMENT

	override fun getSide(): ApplicationSide =
		if (FMLLoader.getDist() == Dist.CLIENT) ApplicationSide.CLIENT else ApplicationSide.SERVER

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
}