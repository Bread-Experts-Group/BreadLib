package org.bread_experts_group.breadlib.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.world.item.Item;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.bread_experts_group.breadlib.ForgeEvents;
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
		if (!ForgeEvents.getExtensionsTaskRan()) ForgeEvents.setupItemExtensions();
		IClientItemExtension extension = MixinUtil.CLIENT_ITEM_EXTENSIONS.get(item);
		return extension != null ? (IClientItemExtensions) extension : original;
	}
}