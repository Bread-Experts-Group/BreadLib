package org.bread_experts_group.breadlib;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.SimpleChannel;
import org.bread_experts_group.breadlib.network.context.ServerNetworkContext;
import org.bread_experts_group.breadlib.network.payload.PayloadInfo;
import org.bread_experts_group.breadlib.task.TaskManager;
import org.bread_experts_group.breadlib.task.network.NetworkTask;

import static org.bread_experts_group.breadlib.platform.ForgePlatformHelper.registerContent;

@Mod(BreadLib.MOD_ID)
public class BreadLibForge {
	@SuppressWarnings({"unchecked", "rawtypes"})
	public static final SimpleChannel NETWORK_CHANNEL =
			ChannelBuilder
					.named("breadlib")
					.networkProtocolVersion(1)
					.simpleChannel()
					.configuration()
					.serverbound(consumer -> {
						NetworkTask task = TaskManager.runTasks(new NetworkTask());
						for (PayloadInfo info : task.serverboundPayloads()) {
							consumer.add((CustomPacketPayload.class), info.streamCodec(), (payload, context) -> {
								info.handler().handle(payload, new ServerNetworkContext(context.getSender()));
							});
						}
					}).bidirectional().build();

	public BreadLibForge(FMLJavaModLoadingContext context) {
		IEventBus eventBus = context.getModEventBus();
		BreadLib.LOGGER.info("Hello Forge world!");
		BreadLib.init();
		registerContent(eventBus);

		ForgeEvents.registerEvents(eventBus);
	}
}
