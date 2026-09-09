package org.bread_experts_group.breadlib.util

import net.minecraft.core.Holder
import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.*
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.*
import net.minecraft.world.level.border.BorderChangeListener
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.dimension.LevelStem
import net.minecraft.world.level.storage.DerivedLevelData
import net.minecraft.world.level.storage.LevelResource
import org.bread_experts_group.breadlib.BreadLib.MOD_ID
import org.bread_experts_group.breadlib.platform.ApplicationSide
import org.bread_experts_group.breadlib.platform.PlatformServices
import org.bread_experts_group.breadlib.registry.network.BiomeRegisterPacket
import org.bread_experts_group.breadlib.registry.network.DimensionTypeRegisterPacket
import java.awt.Color
import java.io.IOException
import kotlin.io.path.createDirectories

// This code was derived from the ideas in:
// https://github.com/McJtyMods/RFToolsDimensions/blob/1.21_neo/src/main/java/mcjty/rftoolsdim/dimension/tools/DynamicDimensionManager.java

object DimUtil {
	fun MinecraftServer.dynamicLevelDataFile() = this.getWorldPath(LevelResource.ROOT)
		.resolve("data", MOD_ID, "dynamic level data.nbtc")

	fun createAndRegisterWorldAndDimension(
		worldKey: ResourceLocation,
		dimension: LevelStem,
		persist: Boolean = true
	): ServerLevel = PlatformServices.NETWORK.server.let { server ->
		val resourceKey = ResourceKey.create(Registries.DIMENSION, worldKey)

		val existingWorld = server.levels[resourceKey]
		if (existingWorld != null) return@let existingWorld

		val worldData = server.worldData
		val newWorld = ServerLevel(
			server,
			server.executor,
			server.storageSource,
			DerivedLevelData(worldData, worldData.overworldData()),
			resourceKey,
			dimension,
			server.progressListenerFactory.create(11),
			false,
			BiomeManager.obfuscateSeed(worldData.worldGenOptions().seed()),
			listOf(),
			true,
			null
		)

		val registryAccess = server.registryAccess()
		if (persist) {
			val file = server.dynamicLevelDataFile().also { it.parent.createDirectories() }
			val mutable = try {
				NbtIo.readCompressed(file, NbtAccounter.unlimitedHeap())
			} catch (_: IOException) {
				CompoundTag()
			}

			val stemKey = registryAccess.registry(Registries.LEVEL_STEM).get().getResourceKey(dimension).get()

			if (stemKey.location() == worldKey) mutable.put(
				"levels_simple",
				mutable.getList("levels_simple", Tag.TAG_STRING.toInt()).apply {
					add(StringTag.valueOf(stemKey.location().toString()))
				}
			) else TODO("Complex")

			NbtIo.writeCompressed(mutable, file)
		}

		registryAccess.registerDimension(newWorld, worldKey)

		server.getLevel(Level.OVERWORLD)!!.worldBorder.addListener(
			BorderChangeListener.DelegateBorderChangeListener(newWorld.worldBorder)
		)

		server.levels[resourceKey] = newWorld
		PlatformServices.PLATFORM.refreshLevels()

		return newWorld
	}

	fun createBiomeSpecialFX(
		skyColor: Color, skyFogColor: Color,
		waterColor: Color, waterFogColor: Color
	): BiomeSpecialEffects = BiomeSpecialEffects.Builder()
		.skyColor(skyColor.rgb).fogColor(skyFogColor.rgb)
		.waterColor(waterColor.rgb).waterFogColor(waterFogColor.rgb)
		.build()

	fun createBiome(
		precipitation: Boolean,
		temperature: Float, downfall: Float,
		mobSpawnSettings: MobSpawnSettings,
		generationSettings: BiomeGenerationSettings,
		specialEffects: BiomeSpecialEffects
	): Biome = Biome.BiomeBuilder()
		.hasPrecipitation(precipitation)
		.temperature(temperature).downfall(downfall)
		.mobSpawnSettings(mobSpawnSettings)
		.generationSettings(generationSettings)
		.specialEffects(specialEffects)
		.build()

	fun <T : Any> RegistryAccess.register(
		registry: ResourceKey<Registry<T>>,
		entry: () -> T, location: ResourceLocation
	): Holder<T> {
		val entryRegistry = this.registry(registry).get() as MappedRegistry<T>

		val holderCheck = entryRegistry.getHolder(location)
		if (holderCheck.isPresent) return holderCheck.get()

		entryRegistry.frozen = false
		val entry = entry()
		val holder = Registry.registerForHolder(
			entryRegistry,
			ResourceKey.create(registry, location),
			entry
		)
		entryRegistry.frozen = true

		if (PlatformServices.NETWORK.side == ApplicationSide.CLIENT) return holder
		else if (registry != Registries.LEVEL_STEM) PlatformServices.NETWORK.sendToAllPlayers(
			when (registry) {
				Registries.DIMENSION_TYPE -> DimensionTypeRegisterPacket(location, entry as DimensionType)
				Registries.BIOME -> BiomeRegisterPacket(location, entry as Biome)
				else -> throw NotImplementedError("Client synch register for $entry / $registry / $location")
			}
		)

		return holder
	}

	fun RegistryAccess.registerBiome(biome: Biome, location: ResourceLocation) = this.register(
		Registries.BIOME, { biome }, location
	)

	fun RegistryAccess.registerDimensionType(type: DimensionType, location: ResourceLocation) = this.register(
		Registries.DIMENSION_TYPE, { type }, location
	)

	fun RegistryAccess.registerLevelStem(stem: LevelStem, location: ResourceLocation) = this.register(
		Registries.LEVEL_STEM, { stem }, location
	)

	fun RegistryAccess.registerDimension(level: Level, location: ResourceLocation) = this.register(
		Registries.DIMENSION, { level }, location
	)
}