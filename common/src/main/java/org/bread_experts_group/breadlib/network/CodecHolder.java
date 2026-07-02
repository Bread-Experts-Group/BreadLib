package org.bread_experts_group.breadlib.network;

import com.mojang.serialization.Codec;
import net.minecraft.network.codec.StreamCodec;

public record CodecHolder<B, V>(Codec<V> codec, StreamCodec<B, V> streamCodec) {
}