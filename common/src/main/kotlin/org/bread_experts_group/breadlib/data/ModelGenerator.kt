package org.bread_experts_group.breadlib.data

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.ItemDisplayContext
import org.bread_experts_group.breadlib.data.model.LoaderSpecificModelProperty
import org.bread_experts_group.breadlib.data.model.ObjectResourceLocation
import org.bread_experts_group.breadlib.data.model.block.BlockStateSingleVariant
import org.bread_experts_group.breadlib.data.model.block.BlockStateVariant
import org.bread_experts_group.breadlib.data.model.item.GUILight
import org.bread_experts_group.breadlib.data.model.item.ItemModelOverride
import org.bread_experts_group.breadlib.data.model.item.ItemModelPerspective
import org.bread_experts_group.breadlib.registry.objects.RegistryBlock
import org.bread_experts_group.breadlib.registry.objects.RegistryItem
import org.bread_experts_group.breadlib.util.location
import org.bread_experts_group.breadlib.util.resolve
import org.joml.Vector3f
import java.util.concurrent.CompletableFuture

@Suppress("MemberVisibilityCanBePrivate")
class ModelGenerator(override val modID: String) : DataGenerator() {
	companion object {
		fun Vector3f.asJsonArray(): JsonArray = JsonArray(3).also {
			it.add(x)
			it.add(y)
			it.add(z)
		}

		const val ITEM_CATEGORY: String = "item"
		const val BLOCK_CATEGORY: String = "block"
	}

	private val models = mutableSetOf<Pair<ObjectResourceLocation, JsonObject>>()
	private val blockStates = mutableMapOf<ObjectResourceLocation, JsonObject>()

	override val generateForServer: Boolean = false

	fun model2D(
		parent: ObjectResourceLocation,
		item: ObjectResourceLocation,
		textures: Map<String, ObjectResourceLocation> = emptyMap(),
		guiLight: GUILight = GUILight.FRONT,
		overrides: List<ItemModelOverride> = emptyList(),
		perspectives: Map<ItemDisplayContext, ItemModelPerspective> = emptyMap(),
		vararg loaderSpecifics: LoaderSpecificModelProperty
	) {
		models.add(item to JsonObject().also { model ->
			model.addProperty("parent", parent.toString())
			if (textures.isNotEmpty()) model.add("textures", JsonObject().also { modelTextures ->
				for ((variable, texture) in textures) modelTextures.addProperty(variable, texture.toString())
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
		})
	}

	fun flat2D(
		item: ObjectResourceLocation,
		layer0: ObjectResourceLocation = item,
		layer1: ObjectResourceLocation? = null,
		layer2: ObjectResourceLocation? = null,
		layer3: ObjectResourceLocation? = null,
		layer4: ObjectResourceLocation? = null,
		guiLight: GUILight = GUILight.FRONT,
		overrides: List<ItemModelOverride> = emptyList(),
		perspectives: Map<ItemDisplayContext, ItemModelPerspective> = emptyMap(),
		vararg loaderSpecifics: LoaderSpecificModelProperty
	) = model2D(
		ObjectResourceLocation(
			ResourceLocation.withDefaultNamespace("generated"),
			ITEM_CATEGORY
		),
		item, buildMap {
			put("layer0", layer0)
			if (layer1 != null) put("layer1", layer1)
			if (layer2 != null) put("layer2", layer2)
			if (layer3 != null) put("layer3", layer3)
			if (layer4 != null) put("layer4", layer4)
		},
		guiLight, overrides, perspectives, *loaderSpecifics
	)

	fun flat2D(
		item: RegistryItem<*>,
		layer0: ObjectResourceLocation = ObjectResourceLocation(item.get()),
		layer1: ObjectResourceLocation? = null,
		layer2: ObjectResourceLocation? = null,
		layer3: ObjectResourceLocation? = null,
		layer4: ObjectResourceLocation? = null,
		guiLight: GUILight = GUILight.FRONT,
		overrides: List<ItemModelOverride> = emptyList(),
		perspectives: Map<ItemDisplayContext, ItemModelPerspective> = emptyMap(),
		vararg loaderSpecifics: LoaderSpecificModelProperty
	): Unit = this.flat2D(
		ObjectResourceLocation(item.get()),
		layer0, layer1, layer2, layer3, layer4, guiLight, overrides, perspectives, *loaderSpecifics
	)

	fun flat3D(
		block: ObjectResourceLocation,
		all: ObjectResourceLocation = block,
		particles: ObjectResourceLocation? = null,
		vararg loaderSpecifics: LoaderSpecificModelProperty
	) {
		models.add(block to JsonObject().also { model ->
			model.addProperty("parent", "minecraft:$BLOCK_CATEGORY/cube_all")
			model.add("textures", JsonObject().also { textures ->
				textures.addProperty("all", all.toString())
				if (particles != null) textures.addProperty("particles", particles.toString())
			})
		})
	}

	fun flat3D(
		block: RegistryBlock<*>,
		all: ObjectResourceLocation = ObjectResourceLocation(block.get()),
		particles: ObjectResourceLocation? = null,
		vararg loaderSpecifics: LoaderSpecificModelProperty
	): Unit = flat3D(
		ObjectResourceLocation(block.get()),
		all, particles, *loaderSpecifics
	)

	fun verticalHorizontalFront3D(
		block: ObjectResourceLocation,
		vertical: ObjectResourceLocation,
		horizontal: ObjectResourceLocation,
		front: ObjectResourceLocation,
		particles: ObjectResourceLocation? = null,
		vararg loaderSpecifics: LoaderSpecificModelProperty
	) {
		models.add(block to JsonObject().also { model ->
			model.addProperty("parent", "minecraft:$BLOCK_CATEGORY/orientable")
			model.add("textures", JsonObject().also { textures ->
				textures.addProperty("front", front.toString())
				textures.addProperty("side", horizontal.toString())
				textures.addProperty("top", vertical.toString())
				if (particles != null) textures.addProperty("particles", particles.toString())
			})
		})
	}

	fun verticalHorizontalFront3D(
		block: RegistryBlock<*>,
		vertical: ObjectResourceLocation,
		horizontal: ObjectResourceLocation,
		front: ObjectResourceLocation,
		particles: ObjectResourceLocation? = null,
		vararg loaderSpecifics: LoaderSpecificModelProperty
	): Unit = verticalHorizontalFront3D(
		ObjectResourceLocation(block.get()),
		vertical, horizontal, front, particles, *loaderSpecifics
	)

	fun model2D(
		block: RegistryBlock<*>
	): Unit = model2D(
		ObjectResourceLocation(block),
		ObjectResourceLocation(block.get().location, "item")
	)

	fun blockState(
		block: ObjectResourceLocation,
		variants: Map<String, BlockStateVariant> = mapOf(
			"" to BlockStateSingleVariant(block)
		)
	) {
		blockStates[block] = JsonObject().also { model ->
			model.add("variants", JsonObject().also { modelVariants ->
				for ((name, variant) in variants) modelVariants.add(
					name,
					when (variant) {
						is BlockStateSingleVariant -> JsonObject().also { modelVariant ->
							modelVariant.addProperty("model", variant.model.toString())
							if (variant.x != null) modelVariant.addProperty("x", variant.x)
							if (variant.y != null) modelVariant.addProperty("y", variant.y)
							if (variant.z != null) modelVariant.addProperty("z", variant.z)
							if (variant.uvLock != null) modelVariant.addProperty("uvlock", variant.uvLock)
						}

						else -> throw IllegalArgumentException("Unknown variant kind ${variant::class}: $variant")
					}
				)
			})
		}
	}

	fun blockState(
		block: RegistryBlock<*>,
		variants: Map<String, BlockStateVariant> = mapOf(
			"" to BlockStateSingleVariant(ObjectResourceLocation(block))
		)
	): Unit = this.blockState(ObjectResourceLocation(block), variants)

	override fun getName(): String = "BreadLib Model Generator ($modID)"

	override fun run(p0: CachedOutput): CompletableFuture<*> {
		val completableFutures = arrayOfNulls<CompletableFuture<*>>(models.size + blockStates.size)
		var i = 0
		for ((entry, model) in models) completableFutures[i++] = DataProvider.saveStable(
			p0, model,
			packOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(
				entry.location.namespace, "models", entry.type, "${entry.location.path}.json"
			)
		)
		for ((entry, state) in blockStates) completableFutures[i++] = DataProvider.saveStable(
			p0, state,
			packOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(
				entry.location.namespace, "blockstates", "${entry.location.path}.json"
			)
		)
		return CompletableFuture.allOf(*completableFutures)
	}
}