package org.bread_experts_group.breadlib.dimension

import net.minecraft.client.player.LocalPlayer
import net.minecraft.core.registries.Registries
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.resources.ResourceKey
import net.minecraft.world.level.Level
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.network.NetworkContext

class DimensionUpdatePacket(
	private val levelKey: ResourceKey<Level>,
//	private val dimType: Holder<DimensionType>
) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<DimensionUpdatePacket> =
			CustomPacketPayload.Type(BreadLib.modLoc("clientbound_packets", "dimension_update"))
		val STREAM_CODEC: StreamCodec<RegistryFriendlyByteBuf, DimensionUpdatePacket> = StreamCodec.composite(
			ResourceKey.streamCodec(Registries.DIMENSION), DimensionUpdatePacket::levelKey,
//			DimensionType.STREAM_CODEC, DimensionUpdatePacket::dimType,
			::DimensionUpdatePacket
		)

		fun handleClientbound(data: DimensionUpdatePacket, context: NetworkContext) {
			val player = context.player as LocalPlayer
			player.connection.levels().add(data.levelKey)
			val key = ResourceKey.create(Registries.DIMENSION_TYPE, data.levelKey.location())

//			val registry = context.level().registryAccess().registryOrThrow(Registries.DIMENSION_TYPE)
//			(registry as IRegistryExtension).unfreeze()
//			Registry.register(registry, key, data.dimType.value())
//			registry.freeze()
		}
	}

	override fun type(): CustomPacketPayload.Type<DimensionUpdatePacket> = TYPE
}