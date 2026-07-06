package org.bread_experts_group.breadlib.test;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.bread_experts_group.breadlib.BreadLib;
import org.bread_experts_group.breadlib.network.NetworkContext;
import org.bread_experts_group.breadlib.platform.PlatformServices;
import org.jetbrains.annotations.NotNull;

public record ServerboundPacketTest(int testInt, String testString) implements CustomPacketPayload {
	public static Type<ServerboundPacketTest> TYPE = new Type<>(BreadLib.modLoc("serverbound_packets", "test"));
	public static StreamCodec<ByteBuf, ServerboundPacketTest> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, ServerboundPacketTest::testInt,
			ByteBufCodecs.STRING_UTF8, ServerboundPacketTest::testString,
			ServerboundPacketTest::new
	);

	@Override
	@NotNull
	public Type<ServerboundPacketTest> type() {
		return TYPE;
	}

	public static void handleServerbound(ServerboundPacketTest data, NetworkContext context) {
		BreadLib.LOGGER.info("Serverbound packet test: {}, {}", data.testInt, data.testString);
		PlatformServices.PLATFORM.sendToAllPlayers(
				new ClientboundPacketTest(20, "client_test"),
				context.serverLevel()
		);
	}
}