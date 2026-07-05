package org.bread_experts_group.breadlib.platform;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.network.PacketDistributor;
import org.bread_experts_group.breadlib.ForgeNetworking;
import org.bread_experts_group.breadlib.platform.services.IPlatformHelper;

public class ForgePlatformHelper implements IPlatformHelper {
	@Override
	public String getPlatformName() {
		return "Forge";
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
		if (ForgeNetworking.NETWORK_CHANNEL == null) throw new NullPointerException(
				"Breadlib's network channel was not initialized before accessing, this shouldn't be possible!"
		);
		ForgeNetworking.NETWORK_CHANNEL.send(payload, PacketDistributor.SERVER.noArg());
	}

	@Override
	public <T extends CustomPacketPayload> void sendClientboundPacket(T payload, ServerLevel level) {
		if (ForgeNetworking.NETWORK_CHANNEL == null) throw new NullPointerException(
				"Breadlib's network channel was not initialized before accessing, this shouldn't be possible!"
		);
		ForgeNetworking.NETWORK_CHANNEL.send(payload, PacketDistributor.ALL.noArg());
	}
}
