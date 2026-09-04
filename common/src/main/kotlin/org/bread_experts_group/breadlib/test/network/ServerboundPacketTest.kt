package org.bread_experts_group.breadlib.test.network

import io.netty.buffer.ByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.BreadLib.modLoc
import org.bread_experts_group.breadlib.network.NetworkContext
import org.bread_experts_group.breadlib.platform.PlatformServices

data class ServerboundPacketTest(val testInt: Int, val testString: String) : CustomPacketPayload {
	companion object {
		var TYPE: CustomPacketPayload.Type<ServerboundPacketTest> =
			CustomPacketPayload.Type(modLoc("serverbound_packets", "test"))

		var STREAM_CODEC: StreamCodec<ByteBuf, ServerboundPacketTest> =
			StreamCodec.composite(
				ByteBufCodecs.INT, ServerboundPacketTest::testInt,
				ByteBufCodecs.STRING_UTF8, ServerboundPacketTest::testString,
				::ServerboundPacketTest
			)

		fun handleServerbound(data: ServerboundPacketTest, context: NetworkContext) {
			BreadLib.LOGGER.info("Serverbound packet test: {}, {}", data.testInt, data.testString)
			PlatformServices.PLATFORM.sendToAllPlayers(
				ClientboundPacketTest(20, "client_test"),
				context.serverLevel()
			)
		}
	}

	override fun type(): CustomPacketPayload.Type<ServerboundPacketTest> = TYPE
}