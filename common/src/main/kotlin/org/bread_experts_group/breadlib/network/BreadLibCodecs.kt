package org.bread_experts_group.breadlib.network

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf

object BreadLibCodecs {
	val LONG: CodecHolder<ByteBuf, Long> = CodecHolder(Codec.LONG, ByteBufCodecs.VAR_LONG)
	val FLOAT: CodecHolder<ByteBuf, Float> = CodecHolder(Codec.FLOAT, ByteBufCodecs.FLOAT)
	val INT: CodecHolder<ByteBuf, Int> = CodecHolder(Codec.INT, ByteBufCodecs.INT)
	val DOUBLE: CodecHolder<ByteBuf, Double> = CodecHolder(Codec.DOUBLE, ByteBufCodecs.DOUBLE)
	val STRING: CodecHolder<ByteBuf, String> = CodecHolder(Codec.STRING, ByteBufCodecs.STRING_UTF8)
	val BOOLEAN: CodecHolder<ByteBuf, Boolean> = CodecHolder(Codec.BOOL, ByteBufCodecs.BOOL)
	val BLOCK_POS: CodecHolder<ByteBuf, BlockPos> = CodecHolder(BlockPos.CODEC, BlockPos.STREAM_CODEC)

	val VEC3: CodecHolder<ByteBuf, Vec3> = CodecHolder(
		Vec3.CODEC,
		StreamCodec.composite(
			ByteBufCodecs.DOUBLE, { it.x() },
			ByteBufCodecs.DOUBLE, { it.y() },
			ByteBufCodecs.DOUBLE, { it.z() },
			::Vec3
		)
	)

	val QUATERNIONF: CodecHolder<ByteBuf, Quaternionf> = CodecHolder(
		ExtraCodecs.QUATERNIONF,
		ByteBufCodecs.QUATERNIONF
	)

	val BLOCK_STATE: CodecHolder<ByteBuf, BlockState> = CodecHolder(
		BlockState.CODEC,
		ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY)
	)

	val BOUNDING_BOX: CodecHolder<ByteBuf, BoundingBox> = CodecHolder(
		BoundingBox.CODEC,
		StreamCodec.composite(
			ByteBufCodecs.INT, BoundingBox::minX,
			ByteBufCodecs.INT, BoundingBox::minY,
			ByteBufCodecs.INT, BoundingBox::minZ,
			ByteBufCodecs.INT, BoundingBox::maxX,
			ByteBufCodecs.INT, BoundingBox::maxY,
			ByteBufCodecs.INT, BoundingBox::maxZ,
			::BoundingBox
		)
	)

	val AXIS_ALIGNED_BOUNDING_BOX: CodecHolder<ByteBuf, AABB> = CodecHolder(
		RecordCodecBuilder.create { inst ->
			inst.group(
				Codec.DOUBLE.fieldOf("minX").forGetter(AABB::minX),
				Codec.DOUBLE.fieldOf("minY").forGetter(AABB::minY),
				Codec.DOUBLE.fieldOf("minZ").forGetter(AABB::minZ),
				Codec.DOUBLE.fieldOf("maxX").forGetter(AABB::maxX),
				Codec.DOUBLE.fieldOf("maxY").forGetter(AABB::maxY),
				Codec.DOUBLE.fieldOf("maxZ").forGetter(AABB::maxZ)
			).apply(inst, ::AABB)
		},
		StreamCodec.composite(
			ByteBufCodecs.DOUBLE, AABB::minX,
			ByteBufCodecs.DOUBLE, AABB::minY,
			ByteBufCodecs.DOUBLE, AABB::minZ,
			ByteBufCodecs.DOUBLE, AABB::maxX,
			ByteBufCodecs.DOUBLE, AABB::maxY,
			ByteBufCodecs.DOUBLE, AABB::maxZ,
			::AABB
		)
	)
}
