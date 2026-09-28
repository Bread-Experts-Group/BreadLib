package org.bread_experts_group.breadlib.data

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import net.minecraft.data.CachedOutput
import net.minecraft.data.DataProvider
import net.minecraft.data.PackOutput
import net.minecraft.resources.ResourceLocation
import net.minecraft.sounds.SoundEvent
import org.bread_experts_group.breadlib.registry.objects.RegistryObject
import org.bread_experts_group.breadlib.util.resolve
import java.util.concurrent.CompletableFuture

class SoundProvider(modID: String, packOutput: PackOutput) : DataGenerationProvider(modID, packOutput) {
	data class SoundDefinition(
		val soundObject: RegistryObject<SoundEvent, SoundEvent>,
		val volume: Double = 1.0,
		val pitch: Double = 1.0,
		val weight: Int = 1,
		val stream: Boolean = false,
		val attenuationDistance: Int = 16
	) {
		fun hasDifference(): Boolean =
			this.volume != 1.0 || this.pitch != 1.0 || this.weight != 1 || this.stream || this.attenuationDistance != 16

		fun serialize(sounds: JsonArray) {
			if (!this.hasDifference()) {
				sounds.add(this.soundObject.name.toString())
				return
			}
			sounds.add(JsonObject().also {
				it.addProperty("name", this.soundObject.name.toString())
				if (volume != 1.0) it.addProperty("volume", this.volume)
				if (pitch != 1.0) it.addProperty("pitch", this.pitch)
				if (weight != 1) it.addProperty("weight", this.weight)
				if (stream) it.addProperty("stream", true)
				if (attenuationDistance != 16) it.addProperty("attenuation_distance", this.attenuationDistance)
			})
		}
	}

	private val sounds: MutableMap<ResourceLocation, MutableList<SoundDefinition>> = mutableMapOf()

	fun sound(
		name: String,
		vararg definitions: SoundDefinition,
	): SoundProvider = this.also {
		check(definitions.isNotEmpty()) { "sound definitions cannot be empty." }
		sounds.getOrPut(
			ResourceLocation.fromNamespaceAndPath(modID, name)
		) { mutableListOf() }.addAll(definitions)
	}

	fun sound(name: String, vararg soundObject: RegistryObject<SoundEvent, SoundEvent>): SoundProvider =
		this.sound(name, *soundObject.map { SoundDefinition(it) }.toTypedArray())

	override fun run(output: CachedOutput): CompletableFuture<*> =
		DataProvider.saveStable(
			output,
			JsonObject().also {
				for ((location, definitions) in sounds) {
					it.add(location.path, JsonObject().also { child ->
						child.add("sounds", JsonArray().also { sounds ->
							definitions.forEach { def -> def.serialize(sounds) }
						})
						child.addProperty("subtitle", "sound.$modID.${location.path}")
					})
				}
			},
			packOutput.getOutputFolder(PackOutput.Target.RESOURCE_PACK).resolve(modID, "sounds.json")
		)

	override fun getName(): String = "BreadLib Sound Provider ($modID)"
}