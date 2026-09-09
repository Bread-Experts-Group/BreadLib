package org.bread_experts_group.breadlib;

import net.minecraft.client.Minecraft;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.simple.SimpleProtocol;
import org.bread_experts_group.breadlib.network.payload.PayloadInfo;
import org.bread_experts_group.breadlib.task.network.NetworkTask;

class ForgeNetworkingClient {
    @SuppressWarnings({"rawtypes", "unchecked"})
    static void setupClient(NetworkTask task, SimpleProtocol<RegistryFriendlyByteBuf, Object> ctx) {
        for (PayloadInfo info : task.clientboundPayloads()) {
            ctx.clientbound().add(info.packetClass, info.streamCodec, (payload, context) -> {
                Player local = Minecraft.getInstance().player;
                if (local == null) return;
                info.handler.handle((CustomPacketPayload) payload, local);
            });
        }
    }
}
