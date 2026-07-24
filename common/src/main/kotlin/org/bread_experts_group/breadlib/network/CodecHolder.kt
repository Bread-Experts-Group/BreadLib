package org.bread_experts_group.breadlib.network

import com.mojang.serialization.Codec
import net.minecraft.network.codec.StreamCodec

@JvmRecord
data class CodecHolder<B, V>(val codec: Codec<V>, val streamCodec: StreamCodec<B, V>)