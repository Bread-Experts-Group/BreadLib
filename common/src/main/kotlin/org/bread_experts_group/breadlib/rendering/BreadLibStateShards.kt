package org.bread_experts_group.breadlib.rendering

import net.minecraft.client.renderer.RenderStateShard.ShaderStateShard

object BreadLibStateShards {
	val TRANSLUCENT_TEX_SHARD: ShaderStateShard = ShaderStateShard(BreadLibRenderTypes::TRANSLUCENT_TEX_INSTANCE)
//	val POSITION_TEX_COLOR_NO_CUTOUT: ShaderStateShard =
//		ShaderStateShard(BreadLibRenderTypes::POSITION_TEX_COLOR_NO_CUTOUT_INSTANCE)
}