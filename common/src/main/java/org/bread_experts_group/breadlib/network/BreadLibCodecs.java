package org.bread_experts_group.breadlib.network;

import com.mojang.serialization.Codec;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;

public class BreadLibCodecs {
	public static CodecHolder<ByteBuf, Long> LONG = new CodecHolder<>(Codec.LONG, ByteBufCodecs.VAR_LONG);
	public static CodecHolder<ByteBuf, Float> FLOAT = new CodecHolder<>(Codec.FLOAT, ByteBufCodecs.FLOAT);
	public static CodecHolder<ByteBuf, Integer> INT = new CodecHolder<>(Codec.INT, ByteBufCodecs.INT);
	public static CodecHolder<ByteBuf, Double> DOUBLE = new CodecHolder<>(Codec.DOUBLE, ByteBufCodecs.DOUBLE);
	public static CodecHolder<ByteBuf, String> STRING = new CodecHolder<>(Codec.STRING, ByteBufCodecs.STRING_UTF8);
	public static CodecHolder<ByteBuf, Boolean> BOOLEAN = new CodecHolder<>(Codec.BOOL, ByteBufCodecs.BOOL);
	public static CodecHolder<ByteBuf, BlockPos> BLOCK_POS = new CodecHolder<>(BlockPos.CODEC, BlockPos.STREAM_CODEC);

	public static CodecHolder<ByteBuf, Vec3> VEC3 = new CodecHolder<>(
			Vec3.CODEC,
			StreamCodec.composite(
					ByteBufCodecs.DOUBLE, Vec3::x,
					ByteBufCodecs.DOUBLE, Vec3::y,
					ByteBufCodecs.DOUBLE, Vec3::z,
					Vec3::new
			)
	);

	public static CodecHolder<ByteBuf, Quaternionf> QUATERNIONF = new CodecHolder<>(
			ExtraCodecs.QUATERNIONF,
			ByteBufCodecs.QUATERNIONF
	);

	public static CodecHolder<ByteBuf, BlockState> BLOCK_STATE = new CodecHolder<>(
			BlockState.CODEC,
			ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY)
	);

	public static CodecHolder<ByteBuf, BoundingBox> BOUNDING_BOX = new CodecHolder<>(
			BoundingBox.CODEC,
			StreamCodec.composite(
					ByteBufCodecs.INT, BoundingBox::minX,
					ByteBufCodecs.INT, BoundingBox::minY,
					ByteBufCodecs.INT, BoundingBox::minZ,
					ByteBufCodecs.INT, BoundingBox::maxX,
					ByteBufCodecs.INT, BoundingBox::maxY,
					ByteBufCodecs.INT, BoundingBox::maxZ,
					BoundingBox::new
			)
	);
}
