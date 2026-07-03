package org.bread_experts_group.breadlib.platform;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
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
	public boolean isDevelopmentEnvironment() {
		return !FMLLoader.isProduction();
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
