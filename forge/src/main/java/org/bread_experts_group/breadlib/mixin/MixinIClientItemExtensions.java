package org.bread_experts_group.breadlib.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.bread_experts_group.breadlib.MixinUtil;
import org.bread_experts_group.breadlib.registry.client.IClientItemExtension;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(IClientItemExtensions.class)
public interface MixinIClientItemExtensions {
	@ModifyReturnValue(
			method = "of(Lnet/minecraft/world/item/Item;)Lnet/minecraftforge/client/extensions/common/IClientItemExtensions;",
			at = @At("RETURN"),
			remap = false
	)
	private static IClientItemExtensions breadlib$modifyReturn(
			IClientItemExtensions original, @Local(argsOnly = true) Item item
	) {
		IClientItemExtension extension = MixinUtil.itemExtensions.get(item);
		return extension != null ? new IClientItemExtensions() {
			@Override
			public HumanoidModel.ArmPose getArmPose(LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
				return extension.getArmPose(entityLiving, hand, itemStack);
			}

			@Override
			public BlockEntityWithoutLevelRenderer getCustomRenderer() {
				return extension.getCustomRenderer();
			}
		} : original;
	}
}
