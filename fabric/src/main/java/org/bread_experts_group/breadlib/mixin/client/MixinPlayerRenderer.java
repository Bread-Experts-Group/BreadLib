package org.bread_experts_group.breadlib.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import org.bread_experts_group.breadlib.MixinUtil;
import org.bread_experts_group.breadlib.registry.client.IClientItemExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(PlayerRenderer.class)
abstract class MixinPlayerRenderer {
	@ModifyReturnValue(method = "getArmPose", at = @At("RETURN"))
	private static HumanoidModel.ArmPose breadlib$getExtensionArmPose(
			HumanoidModel.ArmPose original,
			@Local(argsOnly = true) AbstractClientPlayer player,
			@Local(argsOnly = true) InteractionHand hand
	) {
		ItemStack stack = player.getItemInHand(hand);
		IClientItemExtension extension = MixinUtil.CLIENT_ITEM_EXTENSIONS.get(stack.getItem());
		return extension != null ? extension.getArmPose(player, hand, stack) : original;
	}
}
