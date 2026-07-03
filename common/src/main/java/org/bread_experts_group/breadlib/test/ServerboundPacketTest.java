package org.bread_experts_group.breadlib.test;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.bread_experts_group.breadlib.BreadLib;
import org.bread_experts_group.breadlib.network.context.NetworkContext;
import org.jetbrains.annotations.NotNull;

public class ServerboundPacketTest implements CustomPacketPayload {
	public static StreamCodec<ByteBuf, ServerboundPacketTest> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.INT, ServerboundPacketTest::getTestInt,
			ByteBufCodecs.STRING_UTF8, ServerboundPacketTest::getTestString,
			ServerboundPacketTest::new
	);
	public static Type<ServerboundPacketTest> TYPE = new Type<>(BreadLib.modLoc("test_packet"));

	public int testInt;
	public String testString;

	public ServerboundPacketTest(int testInt, String testString) {
		this.testInt = testInt;
		this.testString = testString;
	}

	public int getTestInt() {
		return testInt;
	}

	public String getTestString() {
		return testString;
	}

	@Override
	public @NotNull Type<ServerboundPacketTest> type() {
		return TYPE;
	}

	public static void handleServerbound(ServerboundPacketTest data, NetworkContext context) {
		BreadLib.LOGGER.info("Serverbound packet test: {}, {}", data.testInt, data.testString);
	}
}
