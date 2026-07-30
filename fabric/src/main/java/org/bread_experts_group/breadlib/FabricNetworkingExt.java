package org.bread_experts_group.breadlib;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.bread_experts_group.breadlib.network.payload.PayloadInfo;

/**
 * This class maintains separation for Fabric's strict sidedness checks.
 * Possibly might not work if JDK uses eager loading. Why did we let mod loaders do this?
 * @author Miko Elbrecht
 */
public class FabricNetworkingExt {
    private final PayloadInfo<?, CustomPacketPayload> info;
    FabricNetworkingExt(PayloadInfo<?, CustomPacketPayload> info) {
        this.info = info;
    }

    void impl(CustomPacketPayload payload, ClientPlayNetworking.Context context) {
        info.handler.handle(payload, context.player());
    }
}
