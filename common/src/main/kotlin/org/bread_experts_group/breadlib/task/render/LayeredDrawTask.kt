package org.bread_experts_group.breadlib.task.render

import net.minecraft.client.gui.LayeredDraw
import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadlib.task.Task

// todo layer based logic currently does not exist on fabric, refer to MixinGui
class LayeredDrawTask : Task() {
	@JvmField
	val layers: MutableList<LayerInfo> = mutableListOf()

	enum class Ordering { BEFORE, AFTER }
	data class LayerInfo(
		val id: ResourceLocation,
		val other: ResourceLocation?,
		val order: Ordering,
		val layer: LayeredDraw.Layer
	)

	fun getLayers(): Collection<LayerInfo> = this.layers

	fun addAbove(id: ResourceLocation, other: ResourceLocation, layer: LayeredDraw.Layer) {
		this.layers.add(LayerInfo(id, other, Ordering.AFTER, layer))
	}

	fun addBelow(id: ResourceLocation, other: ResourceLocation, layer: LayeredDraw.Layer) {
		this.layers.add(LayerInfo(id, other, Ordering.BEFORE, layer))
	}

	fun addAboveAll(id: ResourceLocation, layer: LayeredDraw.Layer) {
		this.layers.add(LayerInfo(id, null, Ordering.AFTER, layer))
	}

	fun addBelowAll(id: ResourceLocation, layer: LayeredDraw.Layer) {
		this.layers.add(LayerInfo(id, null, Ordering.BEFORE, layer))
	}
}