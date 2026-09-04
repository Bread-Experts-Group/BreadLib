package org.bread_experts_group.breadlib.test.network

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.BreadLib.modLoc
import org.bread_experts_group.breadlib.network.NetworkContext

data class ClientboundPacketTest(val testInt: Int, val testString: String) : CustomPacketPayload {
	companion object {
		val TYPE: CustomPacketPayload.Type<ClientboundPacketTest> =
			CustomPacketPayload.Type(modLoc("clientbound_packets", "test"))

		val STREAM_CODEC: StreamCodec<ByteBuf, ClientboundPacketTest> =
			StreamCodec.composite(
				ByteBufCodecs.INT, ClientboundPacketTest::testInt,
				ByteBufCodecs.STRING_UTF8, ClientboundPacketTest::testString,
				::ClientboundPacketTest
			)

		fun handleClientbound(data: ClientboundPacketTest, context: NetworkContext) {
			BreadLib.LOGGER.info("Clientbound packet test: {}, {}", data.testInt, data.testString)
		}
	}

	override fun type(): CustomPacketPayload.Type<ClientboundPacketTest> = TYPE
}
