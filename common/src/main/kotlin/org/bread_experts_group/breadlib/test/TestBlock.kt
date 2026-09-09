package org.bread_experts_group.breadlib.test

import com.mojang.datafixers.util.Pair
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.Registries
import net.minecraft.data.worldgen.SurfaceRuleData
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.util.valueproviders.ConstantInt
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Climate
import net.minecraft.world.level.biome.MultiNoiseBiomeSource
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.dimension.LevelStem
import net.minecraft.world.level.levelgen.*
import net.minecraft.world.level.material.Fluid
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.BreadLib.MOD_ID
import org.bread_experts_group.breadlib.BreadLib.modLoc
import org.bread_experts_group.breadlib.extensions.block.BlockProperties
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlockWithEntity
import org.bread_experts_group.breadlib.extensions.block.ILightningStrikeAction
import org.bread_experts_group.breadlib.platform.PlatformServices
import org.bread_experts_group.breadlib.registry.RegistryProvider.Companion.getProvider
import org.bread_experts_group.breadlib.util.DimUtil.createAndRegisterWorldAndDimension
import java.util.*
import kotlin.random.Random

private val blockProperties = BlockProperties
	.prop(HorizontalDirectionalBlock.FACING, Direction.NORTH) { it.horizontalDirection.opposite }

class TestBlock : BreadLibBlockWithEntity<TestBlockEntity>(TestBlockEntity::class.java, Properties.of(), modID = MOD_ID), ILightningStrikeAction {
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
		if (level.isClientSide) return
		val server = PlatformServices.NETWORK.server

		val registries = server.registryAccess()
		val random = "dim_${System.currentTimeMillis()}"

//		val biomes = registries.registry(Registries.BIOME).get()
//			.getProvider(MOD_ID)
//		biomes.freeze()
//		val biome = biomes.getOrRegister<Biome>(
//			"test_biome"
//		) {
//			createBiome(
//				true, 1f, 1f,
//				MobSpawnSettings.EMPTY,
//				BiomeGenerationSettings.EMPTY,
//				createBiomeSpecialFX(
//					Color.WHITE, Color.WHITE,
//					Color.WHITE, Color.WHITE
//				)
//			)
//		}

		val minY = 0
		val height = 256

		val dimensionTypes = registries.registry(Registries.DIMENSION_TYPE).get()
			.getProvider(MOD_ID)
		dimensionTypes.freeze()
		val dimensionType = dimensionTypes.getOrRegister<DimensionType>(
			random
		) {
			DimensionType(
				OptionalLong.empty(),
				Random.nextBoolean(),
				false,
				Random.nextBoolean(),
				Random.nextBoolean(),
				1.0,
				Random.nextBoolean(),
				Random.nextBoolean(),
				minY * 16, height, height,
				BlockTags.ANVIL,
				ResourceLocation.withDefaultNamespace("dirt"),
				Random.nextFloat(),
				DimensionType.MonsterSettings(
					Random.nextBoolean(),
					false,
					ConstantInt.of(0), 0
				)
			)
		}

		val random2 = XoroshiroRandomSource(Random.nextLong())
		val selectedBlock: Block = registries.registry(Registries.BLOCK).get().getRandom(random2)
			.get().value()
		val selectedFluid: Fluid = registries.registry(Registries.FLUID).get().getRandom(random2)
			.get().value()

		val noiseSettings = registries.registry(Registries.NOISE_SETTINGS).get()
			.getProvider(MOD_ID)
		noiseSettings.freeze()
		val noiseSetting = noiseSettings.getOrRegister<NoiseGeneratorSettings>(
			"noise_$random"
		) {
			NoiseGeneratorSettings(
				NoiseSettings(minY * 16, height, 1, 2),
				selectedBlock.defaultBlockState(),
				selectedFluid.defaultFluidState().createLegacyBlock(),
				NoiseRouterData.overworld(
					registries.lookup(Registries.DENSITY_FUNCTION).get(),
					registries.lookup(Registries.NOISE).get(),
					false,
					Random.nextBoolean()
				),
				SurfaceRuleData.overworldLike(true, false, true),
				listOf(),
				50,
				Random.nextBoolean(),
				Random.nextBoolean(),
				Random.nextBoolean(),
				false
			)
		}

		val levelStems = registries.registry(Registries.LEVEL_STEM).get()
			.getProvider(MOD_ID)
		levelStems.freeze()

		var ctr = 0
		val biomeRegistry = registries.registry(Registries.BIOME).get()
		val stem = levelStems.getOrRegister<LevelStem>(
			random
		) {
			LevelStem(
				dimensionType.holder(),
//				FlatLevelSource(
//					FlatLevelGeneratorSettings(
//						Optional.empty(),
//						biome.holder(),
//						emptyList()
//					).withBiomeAndLayers(
//						mutableListOf(
//							FlatLayerInfo(5, Blocks.ANCIENT_DEBRIS)
//						),
//						Optional.empty(),
//						biome.holder()
//					).also { it.updateLayers() }
//				)
				NoiseBasedChunkGenerator(
					MultiNoiseBiomeSource.createFromList(
						Climate.ParameterList(
							biomeRegistry.filter {
								ctr++ % 2 == Random.nextInt(0, 2)
							}.map {
								Pair(
									Climate.ParameterPoint(
										Climate.Parameter.point(Random.nextFloat()),
										Climate.Parameter.point(Random.nextFloat()),
										Climate.Parameter.point(Random.nextFloat()),
										Climate.Parameter.point(Random.nextFloat()),
										Climate.Parameter.point(Random.nextFloat()),
										Climate.Parameter.point(Random.nextFloat()),
										0
									),
									biomeRegistry.wrapAsHolder(it)
								)
							}
						)
					),
					noiseSetting.holder()
				)
			)
		}

		val newWorld = createAndRegisterWorldAndDimension(modLoc(random), stem.get())

		server.execute {
			Thread.sleep(1000)
			server.playerList.players.forEach {
				it.teleportTo(newWorld, 0.0, 200.0, 0.0, 0f, 0f)
			}
		}
    }
}