package org.bread_experts_group.breadlib.platform;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.network.PacketDistributor;
import org.bread_experts_group.breadlib.platform.services.IPlatformHelper;

public class NeoForgePlatformHelper implements IPlatformHelper {
	@Override
	public String getPlatformName() {
		return "NeoForge";
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
	public <T extends CustomPacketPayload> void sendServerboundPacket(T payload) {
		PacketDistributor.sendToServer(payload);
	}

	@Override
	public <T extends CustomPacketPayload> void sendClientboundPacket(T packet, ServerLevel level) {
		PacketDistributor.sendToAllPlayers(packet);
	}
}