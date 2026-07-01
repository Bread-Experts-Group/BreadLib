package org.bread_experts_group.breadlib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import net.minecraft.client.MouseHandler;
import org.bread_experts_group.breadlib.task.TaskManager;
import org.bread_experts_group.breadlib.task.input.MouseTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MouseHandler.class)
public class MixinMouseHandler {
    @Unique
    private MouseHandler breadlib$getThis() {
        return (MouseHandler) (Object) this;
    }

    @Inject(method = "onScroll", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/player/LocalPlayer;isSpectator()Z", shift = At.Shift.BEFORE))
    private void breadlib$handleScrollTask(
            long windowPointer, double xOffset, double yOffset, CallbackInfo ci,
            @Local(name = "e") double e, @Local(name = "f") double f
    ) {
        TaskManager.runTasks(new MouseTask.Scroll(breadlib$getThis(), e, f));
    }
}
