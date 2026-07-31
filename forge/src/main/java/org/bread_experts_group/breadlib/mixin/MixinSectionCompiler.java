package org.bread_experts_group.breadlib.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.chunk.SectionCompiler;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.model.data.ModelData;
import org.bread_experts_group.breadlib.BreadLib;
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlock;
import org.bread_experts_group.breadlib.rendering.model.ModelShim;
import org.spongepowered.asm.mixin.Debug;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Debug(export = true)
@Mixin(SectionCompiler.class)
abstract class MixinSectionCompiler {
	@Redirect(
			method = "compile",
			at = @At(
					value = "INVOKE",
					target = "Lnet/minecraft/client/renderer/block/BlockRenderDispatcher;renderBatched(Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/BlockAndTintGetter;Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZLnet/minecraft/util/RandomSource;Lnet/minecraftforge/client/model/data/ModelData;Lnet/minecraft/client/renderer/RenderType;)V"
			),
			remap = false
	)
	private void breadlib$redirectRender(
			BlockRenderDispatcher instance,
			BlockState state,
			BlockPos pos,
			BlockAndTintGetter blockAndTintGetter,
			PoseStack poseStack,
			VertexConsumer vertexConsumer,
			boolean checkSides,
			RandomSource randomSource,
			ModelData modelData,
			RenderType renderType
	) {
		var level = Minecraft.getInstance().level;
		if (state.getBlock() instanceof BreadLibBlock && level != null) {
			BreadLib.LOGGER.info("Render redirect passed");
			if (ModelShim.doom(state, pos, level, poseStack, vertexConsumer, randomSource)) return;
		}
		instance.renderBatched(state, pos, blockAndTintGetter, poseStack, vertexConsumer, checkSides, randomSource);
	}
}
