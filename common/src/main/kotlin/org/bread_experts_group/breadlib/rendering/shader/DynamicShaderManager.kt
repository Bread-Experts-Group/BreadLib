package org.bread_experts_group.breadlib.rendering.shader

import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.RenderStateShard
import net.minecraft.client.renderer.RenderType
import net.minecraft.client.renderer.ShaderInstance
import org.bread_experts_group.breadlib.BreadLib
import java.io.IOException
import java.util.function.Consumer

object DynamicShaderManager {
	val dynamicTypes: MutableMap<String, RenderType> = hashMapOf()
	private val dynamicShaders: MutableList<String> = arrayListOf()
	private val manager: ShaderResourceManager = ShaderResourceManager()

	private fun gameRenderer(): GameRenderer = Minecraft.getInstance().gameRenderer

	/**
	 * @return The RenderType associated with the provided name, otherwise return null.
	 */
	@JvmStatic
	fun getType(name: String): RenderType? {
		val type = this.dynamicTypes[name]
		this.tryEnsuringShaderExists(name, type)
		return type
	}

	fun getShader(name: String?): ShaderInstance? = this.gameRenderer().shaders[name]

	fun removeShader(name: String) {
		this.gameRenderer().shaders.remove(name)
		this.dynamicShaders.remove(name)
	}

	private fun putShader(name: String, format: VertexFormat, onLoaded: Consumer<ShaderInstance>) {
		try {
			val instance = ShaderInstance(this.manager, name, format)
			this.gameRenderer().shaders[instance.name] = instance
			this.dynamicShaders.add(instance.name)
			onLoaded.accept(instance)
		} catch (e: IOException) {
			BreadLib.LOGGER.error(e)
		}
	}

	/**
	 * Ensures the ShaderInstance for the provided name exists, otherwise re-populate the shader entry and update the render type.
	 */
	private fun tryEnsuringShaderExists(name: String, type: RenderType?) {
		if (type == null) return
		if (this.getShader(name) == null) {
			this.tryAddTypeAndShader(name, type.format(), type.mode())
		}
	}

	fun removeTypeAndShader(name: String) {
		val instance = this.getShader(name)
		if (instance != null) {
			instance.close()
			this.dynamicTypes.remove(name)
			this.removeShader(name)
		}
	}

	@Suppress("INFERRED_INVISIBLE_RETURN_TYPE_WARNING")
	@JvmStatic
	fun tryAddTypeAndShader(name: String, format: VertexFormat, mode: VertexFormat.Mode) {
		this.putShader(name, format) { instance ->
			val type: RenderType = RenderType.create(
				instance.name, format, mode, RenderType.SMALL_BUFFER_SIZE, false, true,
				RenderType.CompositeState.builder()
					.setShaderState(RenderStateShard.ShaderStateShard { instance })
					.createCompositeState(false)
			)
			this.dynamicTypes[instance.name] = type
		}
	}
}
