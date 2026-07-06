package org.bread_experts_group.breadlib.platform;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.PacketDistributor;
import org.bread_experts_group.breadlib.ForgeNetworking;

import java.nio.file.Path;

public class ForgePlatformHelper implements IPlatformHelper {
	@Override
	public String getPlatformName() {
		return "Forge";
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
		ForgeNetworking.checkChannelNotNull();
		ForgeNetworking.NETWORK_CHANNEL.send(payload, PacketDistributor.SERVER.noArg());
	}

	@Override
	public void sendToAllPlayers(CustomPacketPayload payload, ServerLevel level) {
		ForgeNetworking.checkChannelNotNull();
		ForgeNetworking.NETWORK_CHANNEL.send(payload, PacketDistributor.ALL.noArg());
	}

	@Override
	public void sendToPlayersTrackingChunk(CustomPacketPayload payload, ServerLevel level, ChunkPos pos) {
		ForgeNetworking.checkChannelNotNull();
		for (ServerPlayer player : level.getChunkSource().chunkMap.getPlayers(pos,false)) {
			ForgeNetworking.NETWORK_CHANNEL.send(payload, PacketDistributor.PLAYER.with(player));
		}
	}

	@Override
	public void sendToPlayersInDimension(CustomPacketPayload payload, ServerLevel level) {
		ForgeNetworking.checkChannelNotNull();
		ForgeNetworking.NETWORK_CHANNEL.send(payload, PacketDistributor.DIMENSION.with(level.dimension()));
	}
}