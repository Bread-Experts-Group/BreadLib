package org.bread_experts_group.breadlib.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.datafixers.util.Pair;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.server.packs.resources.ResourceProvider;
import org.bread_experts_group.breadlib.task.TaskManager;
import org.bread_experts_group.breadlib.task.render.ShaderTask;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;
import java.util.function.Consumer;

@Mixin(GameRenderer.class)
abstract class MixinGameRenderer {
	@Inject(
			method = "reloadShaders",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/GameRenderer;loadBlurEffect(Lnet/minecraft/server/packs/resources/ResourceProvider;)V"
			)
	)
	private void breadlib$registerShaders(
			ResourceProvider resourceProvider,
			CallbackInfo ci,
			@Local(ordinal = 1) List<Pair<ShaderInstance, Consumer<ShaderInstance>>> list
	) {
		ShaderTask task = TaskManager.runTasks(new ShaderTask(resourceProvider));
		task.getShaders().forEach((pair) ->
			list.add(Pair.of(pair.getFirst(), pair.getSecond()))
		);
	}
}
