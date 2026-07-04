package org.bread_experts_group.breadlib.data

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import org.bread_experts_group.breadlib.data.model.LoaderSpecificModelProperty
import org.bread_experts_group.breadlib.data.model.item.GUILight
import org.bread_experts_group.breadlib.data.model.item.ItemModelOverride
import org.bread_experts_group.breadlib.data.model.item.ItemModelPerspective
import org.bread_experts_group.breadlib.registry.objects.RegistryItem
import org.bread_experts_group.breadlib.util.location
import org.bread_experts_group.breadlib.util.resolve
import org.joml.Vector3f
import java.util.concurrent.CompletableFuture

class ModelGenerator(override val modID: String) : DataGenerator() {
	companion object {
		fun itemTexture(
			resourceLocation: ResourceLocation
		): String = "${resourceLocation.namespace}:item/${resourceLocation.path}"

		fun Vector3f.asJsonArray(): JsonArray = JsonArray(3).also {
			it.add(x)
			it.add(y)
			it.add(z)
		}
	}

	private val items = mutableMapOf<ResourceLocation, Pair<JsonObject, String>>()

	override val generateForServer: Boolean = false

	fun flat2D(
		item: ResourceLocation,
		layer0: ResourceLocation = item,
		layer1: ResourceLocation? = null,
		layer2: ResourceLocation? = null,
		layer3: ResourceLocation? = null,
		layer4: ResourceLocation? = null,
		guiLight: GUILight = GUILight.FRONT,
		overrides: List<ItemModelOverride> = emptyList(),
		perspectives: Map<ItemDisplayContext, ItemModelPerspective> = emptyMap(),
		vararg loaderSpecifics: LoaderSpecificModelProperty
	) {
		items[item] = JsonObject().also { model ->
			model.addProperty("parent", "minecraft:item/generated")
			model.add("textures", JsonObject().also { textures ->
				textures.addProperty("layer0", itemTexture(layer0))
				if (layer1 != null) textures.addProperty("layer1", itemTexture(layer1))
				if (layer2 != null) textures.addProperty("layer2", itemTexture(layer2))
				if (layer3 != null) textures.addProperty("layer3", itemTexture(layer3))
				if (layer4 != null) textures.addProperty("layer4", itemTexture(layer4))
			})
			model.addProperty("gui_light", guiLight.name.lowercase())
			if (overrides.isNotEmpty()) model.add("overrides", JsonArray().also { modelOverrides ->
				for ((predicates, overrideModel) in overrides) modelOverrides.add(
					JsonObject().also { modelOverride ->
						modelOverride.add("predicate", JsonObject().also { modelPredicates ->
							for ((variable, value) in predicates) modelPredicates.addProperty(variable, value)
						})
						modelOverride.addProperty("model", overrideModel)
					}
				)
			})
			if (perspectives.isNotEmpty()) model.add("display", JsonObject().also { display ->
				for ((context, perspective) in perspectives) display.add(
					context.serializedName,
					JsonObject().also { modelPerspective ->
						if (perspective.translation != null)
							modelPerspective.add("translation", perspective.translation.asJsonArray())
						if (perspective.rotation != null)
							modelPerspective.add("rotation", perspective.rotation.asJsonArray())
						if (perspective.scale != null)
							modelPerspective.add("scale", perspective.scale.asJsonArray())
					}
				)
			})
		} to "item"
	}

	fun flat2D(
		item: RegistryItem<*>,
		layer0: ResourceLocation = item.get().location,
		layer1: ResourceLocation? = null,
		layer2: ResourceLocation? = null,
		layer3: ResourceLocation? = null,
		layer4: ResourceLocation? = null,
		guiLight: GUILight = GUILight.FRONT,
		overrides: List<ItemModelOverride> = emptyList(),
		perspectives: Map<ItemDisplayContext, ItemModelPerspective> = emptyMap(),
		vararg loaderSpecifics: LoaderSpecificModelProperty
	) = this.flat2D(
		item.get().location,
		layer0, layer1, layer2, layer3, layer4, guiLight, overrides, perspectives, *loaderSpecifics
	)

	override fun getName(): String = "BreadLib Model Generator ($modID)"

	override fun run(p0: CachedOutput): CompletableFuture<*> {
		val completableFutures = arrayOfNulls<CompletableFuture<*>>(items.size)
		var i = 0
		for ((item, pair) in items) completableFutures[i++] = DataProvider.saveStable(
			p0, pair.first,
			packOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(
				item.namespace, "models", pair.second, "${item.path}.json"
			)
		)
		return CompletableFuture.allOf(*completableFutures)
	}
}