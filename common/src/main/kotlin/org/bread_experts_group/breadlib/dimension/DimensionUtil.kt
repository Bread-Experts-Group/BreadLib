package org.bread_experts_group.breadlib.dimension

import com.mojang.datafixers.util.Pair
import net.minecraft.core.Holder
import net.minecraft.core.MappedRegistry
import net.minecraft.core.RegistrationInfo
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.Climate
import net.minecraft.world.level.biome.MultiNoiseBiomeSource
import net.minecraft.world.level.border.BorderChangeListener
import net.minecraft.world.level.dimension.LevelStem
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator
import net.minecraft.world.level.storage.DerivedLevelData
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.extensions.IRegistryExtension
import org.bread_experts_group.breadlib.platform.PlatformServices
import kotlin.random.Random

// Referenced some code from Infiniverse
// https://github.com/Commoble/infiniverse/blob/1.21.1/src/main/java/net/commoble/infiniverse/internal/DimensionManager.java
object DimensionUtil {
	private fun <T> registry(level: Level, key: ResourceKey<Registry<T>>): Registry<T> =
		level.registryAccess().registryOrThrow(key)

	private fun <T> writeToRegistry(level: Level, key: ResourceKey<T>, value: T) {
		val registry = this.registry(level, key.registryKey())
		if (registry is MappedRegistry<T>) {
			(registry as IRegistryExtension).unfreeze()
			registry.register(key, value ?: return, RegistrationInfo.BUILT_IN)
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

	fun parameterPoint(
		temperature: kotlin.Pair<Long, Long>,
		humidity: kotlin.Pair<Long, Long>,
		continentalness: kotlin.Pair<Long, Long>,
		erosion: kotlin.Pair<Long, Long>,
		depth: kotlin.Pair<Long, Long>,
		weirdness: kotlin.Pair<Long, Long>,
		offset: Long
	): Climate.ParameterPoint = Climate.ParameterPoint(
		Climate.Parameter(temperature.first, temperature.second),
		Climate.Parameter(humidity.first, humidity.second),
		Climate.Parameter(continentalness.first, continentalness.second),
		Climate.Parameter(erosion.first, erosion.second),
		Climate.Parameter(depth.first, depth.second),
		Climate.Parameter(weirdness.first, weirdness.second),
		offset
	)

	fun randomLong(): Long = Random.nextLong(0, 25)
	fun randomSizedBiomeSource(level: Level): MultiNoiseBiomeSource {
		val biomeReg = this.registry(level, Registries.BIOME)
		val list = buildList {
			repeat(Random.nextInt(1, 10)) {
				this.add(
					parameterPoint(
						randomLong() to randomLong(),
						randomLong() to randomLong(),
						randomLong() to randomLong(),
						randomLong() to randomLong(),
						randomLong() to randomLong(),
						randomLong() to randomLong(),
						Random.nextLong(0, 10)
					)
				)
			}
		}.map { it to biomeReg.getRandom(level.random).get() }
		return this.multiNoiseBiomeSource(*list.toTypedArray())
	}

	fun multiNoiseBiomeSource(vararg pair: kotlin.Pair<Climate.ParameterPoint, Holder<Biome>>): MultiNoiseBiomeSource =
		MultiNoiseBiomeSource.createFromList(
			Climate.ParameterList(pair.map { (first, second) -> Pair(first, second) })
		)

	var serverCount: Int = 0
	var clientCount: Int = 0
	fun createDimension(level: ServerLevel) {
		val count = if (level.isClientSide) clientCount++ else serverCount++
		val location = BreadLib.modLoc("custom_dim_$count")
		val levelKey = ResourceKey.create(Registries.DIMENSION, location)
		val server = level.server

		// Level Stem
		val dimTypeHolder = level.dimensionTypeRegistration()
//		val dimTypeHolder = this.typeHolder(level, location)
//		val chunkGenerator = level.chunkSource.generator
//		val biomeReg = this.registry(level, Registries.BIOME)
		val source = this.randomSizedBiomeSource(level)
		val noiseReg = this.registry(level, Registries.NOISE_SETTINGS)
		val stem = LevelStem(
			dimTypeHolder,
			NoiseBasedChunkGenerator(source, noiseReg.getRandom(level.random).get())
		)
		val stemKey = ResourceKey.create(Registries.LEVEL_STEM, location)

		// Reflection stuff for making a new level
		val chunkProgressListener = server.progressListenerFactory.create(11)
		val executor = server.executor
		val storageAccess = server.storageSource
		val worldData = server.worldData
		val levelData = DerivedLevelData(worldData, worldData.overworldData())

		// Registering the dimension
		this.writeToRegistry(level, stemKey, stem)

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
		PlatformServices.NETWORK.sendToAllPlayers(DimensionUpdatePacket(levelKey), level)
	}
}