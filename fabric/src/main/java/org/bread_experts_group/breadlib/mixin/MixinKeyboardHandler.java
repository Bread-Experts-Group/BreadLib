package org.bread_experts_group.breadlib.mixin;

import net.minecraft.client.KeyboardHandler;
import org.bread_experts_group.breadlib.task.TaskManager;
import org.bread_experts_group.breadlib.task.input.KeyboardTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(KeyboardHandler.class)
public class MixinKeyboardHandler {
	@Inject(method = "keyPress", at = @At(value = "TAIL"))
	private void breadlib$handleKeyTask(long windowPointer, int key, int scanCode, int action, int modifiers, CallbackInfo ci) {
		TaskManager.runTasks(new KeyboardTask(key, scanCode, action, modifiers));
	}
}
