package org.bread_experts_group.breadlib.task.client

import net.minecraft.client.resources.model.ModelResourceLocation
import net.minecraft.resources.ResourceLocation
import org.bread_experts_group.breadlib.task.Task

class AdditionalModelsTask : Task() {
	private val models: MutableList<ResourceLocation> = mutableListOf()

	fun register(location: ResourceLocation) {
		this.models.add(location)
	}

	fun getLocations(): Collection<ResourceLocation> = this.models

	fun getModelLocations(): Collection<ModelResourceLocation> =
		this.models.map { ModelResourceLocation(it, "standalone") }
}