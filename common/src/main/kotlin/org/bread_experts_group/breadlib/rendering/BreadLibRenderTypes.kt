package org.bread_experts_group.breadlib.rendering

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.Util
import net.minecraft.client.renderer.RenderStateShard.LIGHTMAP
import net.minecraft.client.renderer.RenderStateShard.TRANSLUCENT_TARGET
import net.minecraft.client.renderer.RenderStateShard.TRANSLUCENT_TRANSPARENCY
import net.minecraft.client.renderer.RenderStateShard.TextureStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.RenderType.SMALL_BUFFER_SIZE
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.resources.ResourceLocation
import java.util.function.Function

@Suppress("INFERRED_INVISIBLE_RETURN_TYPE_WARNING")
object BreadLibRenderTypes {
	lateinit var TRANSLUCENT_TEX_INSTANCE: ShaderInstance
//	lateinit var POSITION_TEX_COLOR_NO_CUTOUT_INSTANCE: ShaderInstance

	private val TRANSLUCENT_TEX: Function<ResourceLocation, RenderType> = Util.memoize { texture ->
		val textureState = TextureStateShard(texture, false, false)
		RenderType.create(
			"translucent_tex",
			DefaultVertexFormat.BLOCK,
			VertexFormat.Mode.QUADS,
			SMALL_BUFFER_SIZE,
			true,
			true,
			RenderType.CompositeState.builder()
				.setLightmapState(LIGHTMAP)
				.setShaderState(BreadLibStateShards.TRANSLUCENT_TEX_SHARD)
				.setTextureState(textureState)
				.setTransparencyState(TRANSLUCENT_TRANSPARENCY)
				.setOutputState(TRANSLUCENT_TARGET)
				.createCompositeState(false)
		)
	}

	/**
	 * Translucent tex Shader.
	 */
	fun translucentTex(texture: ResourceLocation): RenderType = this.TRANSLUCENT_TEX.apply(texture)
}