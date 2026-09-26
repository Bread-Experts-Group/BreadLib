package org.bread_experts_group.breadlib.registry.client

import com.mojang.blaze3d.vertex.PoseStack
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.item.ItemStack

interface IClientItemExtension {
	companion object {
		val EMPTY_RENDERER: BlockEntityWithoutLevelRenderer = object : BlockEntityWithoutLevelRenderer(null, null) {
			override fun renderByItem(
				stack: ItemStack,
				displayContext: ItemDisplayContext,
				poseStack: PoseStack,
				buffer: MultiBufferSource,
				packedLight: Int,
				packedOverlay: Int
			) {
			}
		}
	}

	fun getCustomRenderer(): BlockEntityWithoutLevelRenderer = Companion.EMPTY_RENDERER

	fun getArmPose(entity: LivingEntity, hand: InteractionHand, stack: ItemStack): HumanoidModel.ArmPose =
		HumanoidModel.ArmPose.ITEM
}