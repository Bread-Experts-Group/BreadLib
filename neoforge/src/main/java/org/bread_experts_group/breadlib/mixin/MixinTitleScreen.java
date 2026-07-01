package org.bread_experts_group.breadlib.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.TitleScreen;
import org.bread_experts_group.breadlib.BreadLib;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TitleScreen.class)
public class MixinTitleScreen {

	@Inject(at = @At("HEAD"), method = "init()V")
	private void init(CallbackInfo info) {
		BreadLib.LOGGER.info("This line is printed by the BreadLib mixin from NeoForge!");
		BreadLib.LOGGER.info("MC Version: {}", Minecraft.getInstance().getVersionType());
	}
}
