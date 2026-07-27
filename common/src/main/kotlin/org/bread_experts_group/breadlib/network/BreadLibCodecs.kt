package org.bread_experts_group.breadlib.network

import com.mojang.serialization.Codec
import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.Vec3
import org.joml.Quaternionf

object BreadLibCodecs {
	var LONG: CodecHolder<ByteBuf, Long> = CodecHolder(Codec.LONG, ByteBufCodecs.VAR_LONG)
	var FLOAT: CodecHolder<ByteBuf, Float> = CodecHolder(Codec.FLOAT, ByteBufCodecs.FLOAT)
	var INT: CodecHolder<ByteBuf, Int> = CodecHolder(Codec.INT, ByteBufCodecs.INT)
	var DOUBLE: CodecHolder<ByteBuf, Double> = CodecHolder(Codec.DOUBLE, ByteBufCodecs.DOUBLE)
	var STRING: CodecHolder<ByteBuf, String> = CodecHolder(Codec.STRING, ByteBufCodecs.STRING_UTF8)
	var BOOLEAN: CodecHolder<ByteBuf, Boolean> = CodecHolder(Codec.BOOL, ByteBufCodecs.BOOL)
	var BLOCK_POS: CodecHolder<ByteBuf, BlockPos> = CodecHolder(BlockPos.CODEC, BlockPos.STREAM_CODEC)

	var VEC3: CodecHolder<ByteBuf, Vec3> = CodecHolder(
		Vec3.CODEC,
		StreamCodec.composite(
			ByteBufCodecs.DOUBLE, { it.x() },
			ByteBufCodecs.DOUBLE, { it.y() },
			ByteBufCodecs.DOUBLE, { it.z() },
			::Vec3
		)
	)

	var QUATERNIONF: CodecHolder<ByteBuf, Quaternionf> = CodecHolder(
		ExtraCodecs.QUATERNIONF,
		ByteBufCodecs.QUATERNIONF
	)

	var BLOCK_STATE: CodecHolder<ByteBuf, BlockState> = CodecHolder(
		BlockState.CODEC,
		ByteBufCodecs.idMapper(Block.BLOCK_STATE_REGISTRY)
	)

	var BOUNDING_BOX: CodecHolder<ByteBuf, BoundingBox> = CodecHolder(
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
}
