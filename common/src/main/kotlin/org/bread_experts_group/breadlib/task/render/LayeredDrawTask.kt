package org.bread_experts_group.breadlib.task.render

import net.minecraft.client.gui.LayeredDraw
import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadlib.task.Task

// todo order based layer adding for all three platforms (render our layers behind or in front of other layers)
class LayeredDrawTask : Task() {
	@JvmField
	val layers: MutableMap<ResourceLocation, LayeredDraw.Layer> = hashMapOf()

	fun add(location: ResourceLocation, layer: LayeredDraw.Layer) {
		this.layers[location] = layer
	}
}