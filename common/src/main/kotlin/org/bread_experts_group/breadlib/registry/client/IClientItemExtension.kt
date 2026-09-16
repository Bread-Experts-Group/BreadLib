package org.bread_experts_group.breadlib.registry.client

import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack

interface IClientItemExtension {
	fun getCustomRenderer(): BlockEntityWithoutLevelRenderer

	fun getArmPose(entity: LivingEntity, hand: InteractionHand, stack: ItemStack): HumanoidModel.ArmPose
}