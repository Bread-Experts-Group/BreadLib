package org.bread_experts_group.breadlib.test

import com.mojang.datafixers.util.Either
import com.mojang.serialization.MapCodec
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.Holder
import net.minecraft.core.HolderOwner
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.WorldGenRegion
import net.minecraft.tags.TagKey
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelHeightAccessor
import net.minecraft.world.level.NoiseColumn
import net.minecraft.world.level.StructureManager
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.BiomeGenerationSettings
import net.minecraft.world.level.biome.BiomeManager
import net.minecraft.world.level.biome.BiomeSource
import net.minecraft.world.level.biome.BiomeSpecialEffects
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.biome.Climate
import net.minecraft.world.level.biome.MobSpawnSettings
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.chunk.ChunkAccess
import net.minecraft.world.level.chunk.ChunkGenerator
import net.minecraft.world.level.chunk.ChunkGenerators
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.dimension.LevelStem
import net.minecraft.world.level.levelgen.GenerationStep
import net.minecraft.world.level.levelgen.Heightmap
import net.minecraft.world.level.levelgen.RandomState
import net.minecraft.world.level.levelgen.blending.Blender
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.BreadLib.modLoc
import org.bread_experts_group.breadlib.extensions.block.BlockProperties
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlockWithEntity
import org.bread_experts_group.breadlib.extensions.block.ILightningStrikeAction
import org.bread_experts_group.breadlib.util.DimUtil.createAndRegisterWorldAndDimension
import java.awt.Color
import java.util.Optional
import java.util.OptionalLong
import java.util.concurrent.CompletableFuture
import java.util.function.Predicate
import java.util.stream.Stream

private val blockProperties = BlockProperties
	.prop(HorizontalDirectionalBlock.FACING, Direction.NORTH) { it.horizontalDirection.opposite }

class TestBlock : BreadLibBlockWithEntity<TestBlockEntity>(TestBlockEntity::class.java, Properties.of(), modID = BreadLib.MOD_ID), ILightningStrikeAction {
	override fun breadLibProperties(): BlockProperties = blockProperties
	override fun blockEntityRenderer(): (BlockEntityRendererProvider.Context) -> BlockEntityRenderer<TestBlockEntity> = ::TestBlockEntityRenderer
	override fun onLightningStruck(
		level: Level,
		pos: BlockPos,
		state: BlockState
	) {
		BreadLib.LOGGER.info(pos)
	}

	override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, movedByPiston: Boolean) {
		createAndRegisterWorldAndDimension(
			(level as? ServerLevel ?: return).server,
			ResourceKey.create(Registries.DIMENSION, modLoc("kappa${System.currentTimeMillis()}"))
        ) { _, _ -> LevelStem(object : Holder<DimensionType?> {
			override fun value(): DimensionType = DimensionType(
				OptionalLong.empty(),
				true,
				false,
				false,
				true,
				1.0,
				true,
				true,
				0,
				256,
				256,
				null,
				null,
				0f,
				DimensionType.MonsterSettings(
					true,
					false,
					null,
					0
				)
			)

			override fun isBound(): Boolean {
				TODO("Not yet implemented")
			}

			override fun `is`(p0: ResourceLocation): Boolean {
				println("* $p0")
				return false
			}

			override fun `is`(p0: ResourceKey<DimensionType?>): Boolean {
				println("* $p0")
				return false
			}

			override fun `is`(p0: Predicate<ResourceKey<DimensionType?>?>): Boolean {
				println("* $p0")
				return false
			}

			override fun `is`(p0: TagKey<DimensionType?>): Boolean {
				println("* $p0")
				return false
			}

			override fun `is`(p0: Holder<DimensionType?>): Boolean {
				println("* $p0")
				return false
			}

			override fun tags(): Stream<TagKey<DimensionType?>?> {
				TODO("Not yet implemented")
			}

			override fun unwrap(): Either<ResourceKey<DimensionType?>?, DimensionType?> {
				TODO("Not yet implemented")
			}

			override fun unwrapKey(): Optional<ResourceKey<DimensionType?>?> {
				TODO("Not yet implemented")
			}

			override fun kind(): Holder.Kind {
				TODO("Not yet implemented")
			}

			override fun canSerializeIn(p0: HolderOwner<DimensionType?>): Boolean {
				TODO("Not yet implemented")
			}
		}, object : ChunkGenerator(
			object : BiomeSource() {
				override fun codec(): MapCodec<out BiomeSource?> {
					TODO("Not yet implemented")
				}

				override fun collectPossibleBiomes(): Stream<Holder<Biome>> = Stream.empty()

				override fun getNoiseBiome(
					p0: Int,
					p1: Int,
					p2: Int,
					p3: Climate.Sampler
				): Holder<Biome?> = object : Holder<Biome?> {
					override fun value(): Biome = Biome.BiomeBuilder()
						.hasPrecipitation(true)
						.temperature(1f)
						.downfall(1f)
						.specialEffects(
							BiomeSpecialEffects.Builder()
								.fogColor(Color.YELLOW.rgb)
								.waterColor(Color.ORANGE.rgb)
								.waterFogColor(Color.RED.rgb)
								.skyColor(Color.RED.rgb)
								.build()
						)
						.mobSpawnSettings(MobSpawnSettings.EMPTY)
						.generationSettings(BiomeGenerationSettings.EMPTY)
						.build()

					override fun isBound(): Boolean {
						TODO("Not yet implemented")
					}

					override fun `is`(p0: ResourceLocation): Boolean {
						TODO("Not yet implemented")
					}

					override fun `is`(p0: ResourceKey<Biome?>): Boolean {
						TODO("Not yet implemented")
					}

					override fun `is`(p0: Predicate<ResourceKey<Biome?>?>): Boolean {
						TODO("Not yet implemented")
					}

					override fun `is`(p0: TagKey<Biome?>): Boolean {
						TODO("Not yet implemented")
					}

					override fun `is`(p0: Holder<Biome?>): Boolean {
						TODO("Not yet implemented")
					}

					override fun tags(): Stream<TagKey<Biome?>?> {
						TODO("Not yet implemented")
					}

					override fun unwrap(): Either<ResourceKey<Biome?>?, Biome?> {
						TODO("Not yet implemented")
					}

					override fun unwrapKey(): Optional<ResourceKey<Biome?>?> {
						TODO("Not yet implemented")
					}

					override fun kind(): Holder.Kind {
						TODO("Not yet implemented")
					}

					override fun canSerializeIn(p0: HolderOwner<Biome?>): Boolean {
						TODO("Not yet implemented")
					}
				}

			}
		) {
			override fun codec(): MapCodec<out ChunkGenerator?> {
				TODO("Not yet implemented")
			}

			override fun applyCarvers(
				p0: WorldGenRegion,
				p1: Long,
				p2: RandomState,
				p3: BiomeManager,
				p4: StructureManager,
				p5: ChunkAccess,
				p6: GenerationStep.Carving
			) {
				TODO("Not yet implemented")
			}

			override fun buildSurface(p0: WorldGenRegion, p1: StructureManager, p2: RandomState, p3: ChunkAccess) {
				TODO("Not yet implemented")
			}

			override fun spawnOriginalMobs(p0: WorldGenRegion) {
				TODO("Not yet implemented")
			}

			override fun getGenDepth(): Int {
				TODO("Not yet implemented")
			}

			override fun fillFromNoise(
				p0: Blender,
				p1: RandomState,
				p2: StructureManager,
				p3: ChunkAccess
			): CompletableFuture<ChunkAccess?> = CompletableFuture.supplyAsync {
				Thread.sleep(999999999)
				null
			}

			override fun getSeaLevel(): Int {
				TODO("Not yet implemented")
			}

			override fun getMinY(): Int {
				TODO("Not yet implemented")
			}

			override fun getBaseHeight(
				p0: Int,
				p1: Int,
				p2: Heightmap.Types,
				p3: LevelHeightAccessor,
				p4: RandomState
			): Int {
				TODO("Not yet implemented")
			}

			override fun getBaseColumn(p0: Int, p1: Int, p2: LevelHeightAccessor, p3: RandomState): NoiseColumn {
				TODO("Not yet implemented")
			}

			override fun addDebugScreenInfo(p0: List<String?>, p1: RandomState, p2: BlockPos) {
				TODO("Not yet implemented")
			}
		}) }
    }
}