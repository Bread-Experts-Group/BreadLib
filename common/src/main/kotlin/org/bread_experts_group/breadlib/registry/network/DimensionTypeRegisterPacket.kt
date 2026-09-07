package org.bread_experts_group.breadlib.registry.network

import net.minecraft.client.player.LocalPlayer
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.dimension.DimensionType
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.network.NetworkContext
import org.bread_experts_group.breadlib.util.DimUtil.registerDimensionType

class DimensionTypeRegisterPacket(
	private val key: ResourceLocation,
	private val dimensionType: DimensionType
) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<DimensionTypeRegisterPacket> = CustomPacketPayload.Type(
			BreadLib.modLoc("clientbound_packets", "dimension_type_register")
		)

		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, DimensionTypeRegisterPacket> = StreamCodec.composite(
			ResourceLocation.STREAM_CODEC, DimensionTypeRegisterPacket::key,
			ByteBufCodecs.fromCodec(DimensionType.DIRECT_CODEC), DimensionTypeRegisterPacket::dimensionType,
			::DimensionTypeRegisterPacket
		)

		fun handleClientbound(data: DimensionTypeRegisterPacket, context: NetworkContext) {
			val player = context.player as LocalPlayer
			player.registryAccess().registerDimensionType(data.dimensionType, data.key)
		}
	}

	override fun type(): CustomPacketPayload.Type<DimensionTypeRegisterPacket> = TYPE
}