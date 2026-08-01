package org.bread_experts_group.breadlib.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlock;
import org.bread_experts_group.breadlib.rendering.model.BreadLibMeshProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SectionCompiler.class)
abstract class MixinSectionCompiler {
	// Wrap fabric's indigo renderer redirect with our own code
	@WrapOperation(
			method = "compile",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;)V"
			)
	)
	private void breadlib$redirectRender(
			BlockRenderDispatcher instance,
			BlockState blockState,
			BlockPos blockPos,
			BlockAndTintGetter blockAndTintGetter,
			PoseStack poseStack,
			VertexConsumer vertexConsumer,
			boolean checkSides,
			RandomSource randomSource,
			Operation<Void> original
	) {
		var level = Minecraft.getInstance().level;
		if (blockState.getBlock() instanceof BreadLibBlock && level != null) {
			if (BreadLibMeshProvider.renderMesh(blockState, blockPos, level, poseStack, vertexConsumer, randomSource)) return;
		}
		original.call(instance, blockState, blockPos, blockAndTintGetter, poseStack, vertexConsumer, checkSides, randomSource);
	}
}
