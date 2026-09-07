package org.bread_experts_group.breadlib.registry.network

import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.biome.Biome
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.network.NetworkContext
import org.bread_experts_group.breadlib.util.DimUtil.registerBiome

class BiomeRegisterPacket(
	private val key: ResourceLocation,
	private val biome: Biome
) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<BiomeRegisterPacket> = CustomPacketPayload.Type(
			BreadLib.modLoc("clientbound_packets", "biome_register")
		)

		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, BiomeRegisterPacket> = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, BiomeRegisterPacket::key,
			ByteBufCodecs.fromCodec(Biome.NETWORK_CODEC), BiomeRegisterPacket::biome,
			::BiomeRegisterPacket
		)

		fun handleClientbound(data: BiomeRegisterPacket, context: NetworkContext) {
			val player = context.player as LocalPlayer
			player.registryAccess().registerBiome(data.biome, data.key)
		}
	}

	override fun type(): CustomPacketPayload.Type<BiomeRegisterPacket> = TYPE
}