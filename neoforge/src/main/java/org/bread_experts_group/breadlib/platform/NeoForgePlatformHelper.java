package org.bread_experts_group.breadlib.platform;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.ChunkPos;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.network.PacketDistributor;

import java.nio.file.Path;

public class NeoForgePlatformHelper implements IPlatformHelper {
	@Override
	public String getPlatformName() {
		return "NeoForge";
	}

	@Override
	public Path getConfigDir() {
		return FMLPaths.CONFIGDIR.get();
	}

	@Override
	public boolean isModLoaded(String modId) {
		return ModList.get().isLoaded(modId);
	}

	@Override
	public EnvironmentKind getEnvironmentKind() {
		if (FMLLoader.isProduction()) {
			return EnvironmentKind.RELEASE;
		} else {
			return EnvironmentKind.DEVELOPMENT;
		}
	}

	@Override
	public ApplicationSide getSide() {
		if (FMLLoader.getDist() == Dist.CLIENT) {
			return ApplicationSide.CLIENT;
		} else {
			return ApplicationSide.SERVER;
		}
	}

	@Override
	public void sendToServer(CustomPacketPayload payload) {
		PacketDistributor.sendToServer(payload);
	}

	@Override
	public void sendToAllPlayers(CustomPacketPayload packet, ServerLevel level) {
		PacketDistributor.sendToAllPlayers(packet);
	}

	@Override
	public void sendToPlayersTrackingChunk(CustomPacketPayload payload, ServerLevel level, ChunkPos pos) {
		PacketDistributor.sendToPlayersTrackingChunk(level, pos, payload);
	}

	@Override
	public void sendToPlayersInDimension(CustomPacketPayload payload, ServerLevel level) {
		PacketDistributor.sendToPlayersInDimension(level, payload);
	}
}