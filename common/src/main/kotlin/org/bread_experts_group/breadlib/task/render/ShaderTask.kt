package org.bread_experts_group.breadlib.task.render

import com.mojang.blaze3d.vertex.VertexFormat
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceProvider
import org.bread_experts_group.breadlib.task.Task
import java.util.function.Consumer

class ShaderTask(val resourceProvider: ResourceProvider) : Task() {
	private val shaderList: MutableList<Pair<ShaderInstance, Consumer<ShaderInstance>>> = mutableListOf()

	fun getShaders(): List<Pair<ShaderInstance, Consumer<ShaderInstance>>> = this.shaderList

	fun registerShader(shaderInstance: ShaderInstance, onLoaded: Consumer<ShaderInstance>) {
		shaderList.add(shaderInstance to onLoaded)
	}

	fun registerShader(name: ResourceLocation, format: VertexFormat, onLoaded: Consumer<ShaderInstance>) {
		this.registerShader(ShaderInstance(this.resourceProvider, name.toString(), format), onLoaded)
	}
}