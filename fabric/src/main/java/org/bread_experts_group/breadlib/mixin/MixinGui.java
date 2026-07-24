package org.bread_experts_group.breadlib.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.LayeredDraw;
import org.bread_experts_group.breadlib.task.TaskManager;
import org.bread_experts_group.breadlib.task.render.LayeredDrawTask;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Gui.class)
abstract class MixinGui {
	@Shadow
	@Final
	private LayeredDraw layers;

	@Shadow
	private int lastHealth;

	@Inject(method = "<init>", at = @At("TAIL"))
	private void breadlib$addGuiLayers(Minecraft minecraft, CallbackInfo ci) {
		TaskManager.runTasks(new LayeredDrawTask()).layers.forEach(((resourceLocation, layer) ->
				this.layers.add(layer)
		));
	}
}
