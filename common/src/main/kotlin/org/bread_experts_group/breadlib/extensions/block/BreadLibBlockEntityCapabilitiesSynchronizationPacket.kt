package org.bread_experts_group.breadlib.extensions.block

import io.netty.buffer.ByteBuf
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.SectionPos
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.network.protocol.common.custom.CustomPacketPayload
import net.minecraft.util.ExtraCodecs
import net.minecraft.world.level.chunk.LevelChunk
import org.bread_experts_group.breadlib.BreadLib.modLoc
import org.bread_experts_group.breadlib.network.NetworkContext
import org.bread_experts_group.breadlib.util.minecraft

class BreadLibBlockEntityCapabilitiesSynchronizationPacket(
    val pos: BlockPos,
    val capabilities: Map<String, List<Direction>>
) : CustomPacketPayload {
    companion object {
        val TYPE: CustomPacketPayload.Type<BreadLibBlockEntityCapabilitiesSynchronizationPacket> = CustomPacketPayload.Type(
            modLoc("clientbound_packets", "block_entity_capability_synchronization")
        )

        val STREAM_CODEC: StreamCodec<ByteBuf, BreadLibBlockEntityCapabilitiesSynchronizationPacket> =
            StreamCodec.composite(
                BlockPos.STREAM_CODEC, BreadLibBlockEntityCapabilitiesSynchronizationPacket::pos,
                ByteBufCodecs.map(
                    { mutableMapOf() },
                    ByteBufCodecs.STRING_UTF8,
                    Direction.STREAM_CODEC.apply(ByteBufCodecs.list())
                ), BreadLibBlockEntityCapabilitiesSynchronizationPacket::capabilities,
                ::BreadLibBlockEntityCapabilitiesSynchronizationPacket
            )

        fun handleClientbound(data: BreadLibBlockEntityCapabilitiesSynchronizationPacket, context: NetworkContext) {
            val chunk = context.level()
                .getChunkAt(data.pos)
            val blockState = chunk.getBlockState(data.pos)
            val blockEntity = chunk.getBlockEntity(data.pos, LevelChunk.EntityCreationType.CHECK)
            if (blockEntity !is BreadLibBlockEntity) return
            data.capabilities.forEach { (klass, directions) ->
                val clazz = Class.forName(klass)
                val currentDirections = blockEntity.capabilitySides[clazz] ?: return@forEach
                currentDirections.clear()
                currentDirections.addAll(directions)
            }
            context.level().updateNeighborsAt(data.pos, blockState.block)
        }
    }

    override fun type(): CustomPacketPayload.Type<BreadLibBlockEntityCapabilitiesSynchronizationPacket> = TYPE
}