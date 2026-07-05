package org.bread_experts_group.breadlib.platform;

import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import org.bread_experts_group.breadlib.platform.services.IPlatformHelper;

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
	public <T extends CustomPacketPayload> void sendServerboundPacket(T payload) {
		ClientPlayNetworking.send(payload);
	}

	@Override
	public <T extends CustomPacketPayload> void sendClientboundPacket(T payload, ServerLevel level) {
		for (ServerPlayer player : PlayerLookup.all(level.getServer())) {
			ServerPlayNetworking.send(player, payload);
		}
	}
}
