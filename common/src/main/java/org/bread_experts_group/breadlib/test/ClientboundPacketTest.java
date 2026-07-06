package org.bread_experts_group.breadlib.test;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.bread_experts_group.breadlib.BreadLib;
import org.bread_experts_group.breadlib.network.NetworkContext;
import org.jetbrains.annotations.NotNull;

public record ClientboundPacketTest(int testInt, String testString) implements CustomPacketPayload {
	public static final Type<ClientboundPacketTest> TYPE = new Type<>(BreadLib.modLoc("clientbound_packets", "test"));
	public static final StreamCodec<ByteBuf, ClientboundPacketTest> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, ClientboundPacketTest::testInt,
			ByteBufCodecs.STRING_UTF8, ClientboundPacketTest::testString,
			ClientboundPacketTest::new
	);

	@Override
	@NotNull
	public Type<ClientboundPacketTest> type() {
		return TYPE;
	}

	public static void handleClientbound(ClientboundPacketTest data, NetworkContext context) {
		BreadLib.LOGGER.info("Clientbound packet test: {}, {}", data.testInt(), data.testString());
	}
}
