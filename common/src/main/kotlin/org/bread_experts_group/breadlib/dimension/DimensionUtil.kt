package org.bread_experts_group.breadlib.dimension

import com.mojang.datafixers.util.Pair
import com.mojang.serialization.Lifecycle
import net.minecraft.core.MappedRegistry
import net.minecraft.core.RegistrationInfo
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.biome.Climate
import net.minecraft.world.level.biome.MultiNoiseBiomeSource
import net.minecraft.world.level.border.BorderChangeListener
import net.minecraft.world.level.dimension.LevelStem
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings
import net.minecraft.world.level.storage.DerivedLevelData
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.extensions.IRegistryExtension
import org.bread_experts_group.breadlib.platform.PlatformServices
import java.util.*

// Referenced some code from Infiniverse
// https://github.com/Commoble/infiniverse/blob/1.21.1/src/main/java/net/commoble/infiniverse/internal/DimensionManager.java
object DimensionUtil {
	private val registrationInfo: RegistrationInfo = RegistrationInfo(Optional.empty(), Lifecycle.stable())

	private fun <T> registry(level: Level, key: ResourceKey<Registry<T>>): Registry<T> =
		level.registryAccess().registryOrThrow(key)

	private fun <T> writeToRegistry(level: Level, key: ResourceKey<T>, value: T, info: RegistrationInfo) {
		val registry = registry(level, key.registryKey())
		if (registry is MappedRegistry<T>) {
			(registry as IRegistryExtension).unfreeze()
			registry.register(key, value!!, info)
			registry.freeze()
		} else throw IllegalStateException("$registry is not writable.")
	}

	// todo the game throws a fit with custom dim types, for some reason..
//	private fun typeHolder(level: Level, location: ResourceLocation): Holder<DimensionType> {
//		val type = DimensionType(
//			OptionalLong.of(1000),
//			true,
//			false,
//			false,
//			true,
//			32.0,
//			true,
//			true,
//			-64,
//			256,
//			128,
//			BlockTags.INFINIBURN_OVERWORLD,
//			BuiltinDimensionTypes.END_EFFECTS,
//			0f,
//			DimensionType.MonsterSettings(true, true, UniformInt.of(0, 7), 0)
//		)
//
//		val key = ResourceKey.create(Registries.DIMENSION_TYPE, location)
//		this.writeToRegistry(level, key, type, this.registrationInfo)
//		return Holder.direct(type)
//	}

	var serverCount: Int = 0
	var clientCount: Int = 0
	fun createDimension(level: ServerLevel) {
		val count = if (level.isClientSide) clientCount++ else serverCount++
		val location = BreadLib.modLoc("custom_dim_$count")
		val levelKey = ResourceKey.create(Registries.DIMENSION, location)

		// Level Stem
		val dimTypeHolder = level.dimensionTypeRegistration()
//		val dimTypeHolder = this.typeHolder(level, location)
		val server = level.server
//		val chunkGenerator = level.chunkSource.generator
		val biomeReg = this.registry(level, Registries.BIOME)
		val source = MultiNoiseBiomeSource.createFromList(
			Climate.ParameterList(listOf(
				Pair(Climate.ParameterPoint(
					Climate.Parameter(0, 100),
					Climate.Parameter(0, 1),
					Climate.Parameter(0, 1),
					Climate.Parameter(0, 10),
					Climate.Parameter(0, 1),
					Climate.Parameter(0, 25),
					0
				), biomeReg.getHolderOrThrow(Biomes.PLAINS))
			))
		)
		val noiseReg = this.registry(level, Registries.NOISE_SETTINGS)
		val stem = LevelStem(
			dimTypeHolder,
			NoiseBasedChunkGenerator(
				source,
				noiseReg.getHolderOrThrow(NoiseGeneratorSettings.FLOATING_ISLANDS)
			)
		)
		val stemKey = ResourceKey.create(Registries.LEVEL_STEM, location)

		// Reflection stuff for making a new level
		val chunkProgressListener = server.progressListenerFactory.create(11)
		val executor = server.executor
		val storageAccess = server.storageSource
		val worldData = server.worldData
		val levelData = DerivedLevelData(worldData, worldData.overworldData())

		// Registering the dimension
		this.writeToRegistry(level, stemKey, stem, this.registrationInfo)

		val newLevel = ServerLevel(
			server,
			executor,
			storageAccess,
			levelData,
			levelKey,
			stem,
			chunkProgressListener,
			worldData.isDebugWorld,
			level.seed,
			listOf(),
			false,
			null
		)

		level.worldBorder.addListener(BorderChangeListener.DelegateBorderChangeListener(level.worldBorder))
		server.levels[levelKey] = newLevel
		PlatformServices.PLATFORM.sendToAllPlayers(DimensionUpdatePacket(levelKey), level)
	}
}