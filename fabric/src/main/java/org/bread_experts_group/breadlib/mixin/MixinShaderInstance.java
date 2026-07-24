package org.bread_experts_group.breadlib.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.blaze3d.shaders.Program;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ShaderInstance.class)
abstract class MixinShaderInstance {
	@Mutable
	@Shadow
	@Final
	private String name;

	@Redirect(
			method = "<init>",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/resources/ResourceLocation;withDefaultNamespace(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"
			)
	)
	private ResourceLocation breadlib$redirect(String location) {
		String string = "shaders/core/";
		if (location.contains(":")) {
			ResourceLocation loc = ResourceLocation.parse(location.replace(string, ""));
			return ResourceLocation.fromNamespaceAndPath(loc.getNamespace(), string + loc.getPath());
		}
		return ResourceLocation.withDefaultNamespace(location);
	}

	@ModifyExpressionValue(
			method = "getOrCreate",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/resources/ResourceLocation;withDefaultNamespace(Ljava/lang/String;)Lnet/minecraft/resources/ResourceLocation;"
			)
	)
	private static ResourceLocation breadlib$redirectLocationAgain(
			ResourceLocation original,
			@Local(argsOnly = true) Program.Type type,
			@Local(argsOnly = true) String name
	) {
		if (!original.getNamespace().equals("minecraft")) {
			ResourceLocation location = ResourceLocation.parse(name);
			return ResourceLocation.fromNamespaceAndPath(
					location.getNamespace(),
					"shaders/core/" + location.getPath() + type.getExtension()
			);
		}
		return original;
	}
}