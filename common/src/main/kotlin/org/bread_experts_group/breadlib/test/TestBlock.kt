package org.bread_experts_group.breadlib.test

import net.minecraft.client.renderer.blockentity.BlockEntityRenderer
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceLocation
import net.minecraft.tags.BlockTags
import net.minecraft.util.valueproviders.ConstantInt
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Biome
import net.minecraft.world.level.biome.BiomeGenerationSettings
import net.minecraft.world.level.biome.MobSpawnSettings
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.dimension.LevelStem
import net.minecraft.world.level.levelgen.FlatLevelSource
import net.minecraft.world.level.levelgen.flat.FlatLevelGeneratorSettings
import net.minecraft.world.level.portal.DimensionTransition
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.BreadLib.MOD_ID
import org.bread_experts_group.breadlib.BreadLib.modLoc
import org.bread_experts_group.breadlib.extensions.block.BlockProperties
import org.bread_experts_group.breadlib.extensions.block.BreadLibBlockWithEntity
import org.bread_experts_group.breadlib.extensions.block.ILightningStrikeAction
import org.bread_experts_group.breadlib.platform.PlatformServices
import org.bread_experts_group.breadlib.registry.RegistryProvider.Companion.getProvider
import org.bread_experts_group.breadlib.util.DimUtil.createAndRegisterWorldAndDimension
import org.bread_experts_group.breadlib.util.DimUtil.createBiome
import org.bread_experts_group.breadlib.util.DimUtil.createBiomeSpecialFX
import java.awt.Color
import java.util.*

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

	companion object {
		val TODO_TYPE = DimensionType(
			OptionalLong.empty(), true, false, false, true,
			1.0, true, true,
			0, 256, 256,
			BlockTags.ANVIL,
			ResourceLocation.withDefaultNamespace("dirt"),
			0f,
			DimensionType.MonsterSettings(
				true, false,
				ConstantInt.of(0), 0
			)
		)
	}

	override fun onPlace(state: BlockState, level: Level, pos: BlockPos, oldState: BlockState, movedByPiston: Boolean) {
		if (level.isClientSide) return
		val server = PlatformServices.NETWORK.server

		val biomes = server.registryAccess().registry(Registries.BIOME).get()
			.getProvider(MOD_ID)
		biomes.freeze()
		val biome = biomes.getOrRegister<Biome>(
			"test_biome"
		) {
			createBiome(
				true, 1f, 1f,
				MobSpawnSettings.EMPTY,
				BiomeGenerationSettings.EMPTY,
				createBiomeSpecialFX(
					Color.WHITE, Color.WHITE,
					Color.WHITE, Color.WHITE
				)
			)
		}

		val dimensionTypes = server.registryAccess().registry(Registries.DIMENSION_TYPE).get()
			.getProvider(MOD_ID)
		dimensionTypes.freeze()
		val dimensionType = dimensionTypes.getOrRegister<DimensionType>(
			"test_dim"
		) { TODO_TYPE }

		val levelStems = server.registryAccess().registry(Registries.LEVEL_STEM).get()
			.getProvider(MOD_ID)
		levelStems.freeze()
		val stem = levelStems.getOrRegister<LevelStem>(
			"test_dim"
		) {
			LevelStem(
				dimensionType.holder(),
				FlatLevelSource(
					FlatLevelGeneratorSettings(
						Optional.empty(),
						biome.holder(),
						emptyList()
					)
				)
			)
		}

		val newWorld = createAndRegisterWorldAndDimension(modLoc("test_dim"), stem.get())

		server.execute {
			Thread.sleep(1000)
			server.playerList.players.forEach {
				it.changeDimension(
					DimensionTransition.missingRespawnBlock(
						newWorld, it
                    ) { }
                )
			}
		}
    }
}