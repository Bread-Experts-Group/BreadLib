package org.bread_experts_group.breadlib.test;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;

public class TestBlockEntityRenderer implements BlockEntityRenderer<TestBlockEntity> {
	@Override
	public void render(
			TestBlockEntity blockEntity,
			float pPartialTick,
			PoseStack poseStack,
			MultiBufferSource bufferSource,
			int pPackedLight,
			int pPackedOverlay
	) {
//		BreadLib.LOGGER.info(blockEntity.getBlockPos());
	}
}
