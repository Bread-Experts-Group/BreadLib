package org.bread_experts_group.breadlib.mixin.client;

import net.minecraft.client.Minecraft;
import org.bread_experts_group.breadlib.BreadLib;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MixinMinecraft {

	@Inject(at = @At("TAIL"), method = "<init>", remap = false)
	private void init(CallbackInfo info) {
		BreadLib.LOGGER.info("This line is printed by the BreadLib common mixin!");
		BreadLib.LOGGER.info("MC Version: {}", Minecraft.getInstance().getVersionType());
	}
}
