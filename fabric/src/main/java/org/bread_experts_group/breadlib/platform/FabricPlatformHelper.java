package org.bread_experts_group.breadlib.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;

import java.nio.file.Path;

public class FabricPlatformHelper implements IPlatformHelper {
	@Override
	public String getPlatformName() {
		return "Fabric";
	}

	@Override
	public boolean isModLoaded(String modId) {
		return FabricLoader.getInstance().isModLoaded(modId);
	}

	@Override
	public Path getConfigDir() {
		return FabricLoader.getInstance().getConfigDir();
	}

	@Override
	public EnvironmentKind getEnvironmentKind() {
		if (FabricLoader.getInstance().isDevelopmentEnvironment()) {
			return EnvironmentKind.DEVELOPMENT;
		} else {
			return EnvironmentKind.RELEASE;
		}
	}

	@Override
	public ApplicationSide getSide() {
		if (FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT) {
			return ApplicationSide.CLIENT;
		} else {
			return ApplicationSide.SERVER;
		}
	}

	@Override
	public void sendToServer(CustomPacketPayload payload) {
		ClientPlayNetworking.send(payload);
	}

	@Override
	public void sendToAllPlayers(CustomPacketPayload payload, ServerLevel level) {
		for (ServerPlayer player : PlayerLookup.all(level.getServer())) ServerPlayNetworking.send(player, payload);
	}

	@Override
	public void sendToPlayersTrackingChunk(CustomPacketPayload payload, ServerLevel level, ChunkPos pos) {
		for (ServerPlayer player : PlayerLookup.tracking(level, pos)) ServerPlayNetworking.send(player, payload);
	}

	@Override
	public void sendToPlayersInDimension(CustomPacketPayload payload, ServerLevel level) {
		for (ServerPlayer player : PlayerLookup.world(level)) ServerPlayNetworking.send(player, payload);
	}
}
