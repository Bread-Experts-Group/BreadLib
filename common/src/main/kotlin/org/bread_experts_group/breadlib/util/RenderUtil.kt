package org.bread_experts_group.breadlib.util

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.GraphicsStatus
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.ItemBlockRenderTypes
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.resources.model.BakedModel
import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack
import org.bread_experts_group.breadlib.platform.PlatformServices

object RenderUtil {
	fun getModel(location: ResourceLocation): BakedModel =
		Minecraft.getInstance().modelManager.getModel(ModelResourceLocation(
			location,
			if (PlatformServices.PLATFORM.platformName == "Fabric") "fabric_resource" else "standalone"
		))

	fun ItemRenderer.renderItemModel(
		model: BakedModel,
		stack: ItemStack,
		displayContext: ItemDisplayContext,
		poseStack: PoseStack,
		bufferSource: MultiBufferSource,
		packedOverlay: Int,
		packedLight: Int
	) {
		val fabulous = Minecraft.getInstance().options.graphicsMode().get() == GraphicsStatus.FABULOUS
		val renderType = ItemBlockRenderTypes.getRenderType(stack, false)
		val buffer = if (fabulous) ItemRenderer.getFoilBufferDirect(bufferSource, renderType, true, stack.hasFoil())
		else ItemRenderer.getFoilBuffer(bufferSource, renderType, true, stack.hasFoil())
		val leftHand = displayContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND
		model.transforms.getTransform(displayContext).apply(leftHand, poseStack)
		this.renderModelLists(model, stack, packedLight, packedOverlay, poseStack, buffer)
	}
}