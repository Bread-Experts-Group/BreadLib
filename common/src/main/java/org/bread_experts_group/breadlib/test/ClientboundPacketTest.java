package org.bread_experts_group.breadlib.test;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.bread_experts_group.breadlib.BreadLib;
import org.bread_experts_group.breadlib.network.context.NetworkContext;
import org.jetbrains.annotations.NotNull;

public class ClientboundPacketTest implements CustomPacketPayload {
	public static final Type<ClientboundPacketTest> TYPE = new Type<>(BreadLib.modLoc("clientbound_packets", "test"));
	public static final StreamCodec<ByteBuf, ClientboundPacketTest> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, ClientboundPacketTest::getTestInt,
			ByteBufCodecs.STRING_UTF8, ClientboundPacketTest::getTestString,
			ClientboundPacketTest::new
	);

	private final int testInt;
	private final String testString;

	public ClientboundPacketTest(int testInt, String testString) {
		this.testInt = testInt;
		this.testString = testString;
	}

	public String getTestString() {
		return testString;
	}

	public int getTestInt() {
		return testInt;
	}

	@Override
	@NotNull
	public Type<ClientboundPacketTest> type() {
		return TYPE;
	}

	public static void handleClientbound(ClientboundPacketTest data, NetworkContext context) {
		BreadLib.LOGGER.info("Clientbound packet test: {}, {}", data.getTestInt(), data.getTestString());
	}
}
