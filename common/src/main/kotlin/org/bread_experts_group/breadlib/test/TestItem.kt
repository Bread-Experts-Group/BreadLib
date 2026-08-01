package org.bread_experts_group.breadlib.test

import com.mojang.blaze3d.platform.InputConstants
import com.mojang.datafixers.util.Pair
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.core.Holder
import net.minecraft.core.Registry
import net.minecraft.core.RegistrySynchronization
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.NbtOps
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.common.ClientboundUpdateTagsPacket
import net.minecraft.network.protocol.configuration.ClientboundRegistryDataPacket
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagNetworkSerialization
import net.minecraft.util.valueproviders.ConstantInt
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.Biomes
import net.minecraft.world.level.biome.Climate
import net.minecraft.world.level.biome.MultiNoiseBiomeSource
import net.minecraft.world.level.dimension.BuiltinDimensionTypes
import net.minecraft.world.level.dimension.DimensionType
import net.minecraft.world.level.dimension.LevelStem
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings
import net.minecraft.world.level.storage.DerivedLevelData
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.extensions.IRegistryExtension
import org.bread_experts_group.breadlib.extensions.item.IKeyboardItem
import org.bread_experts_group.breadlib.extensions.item.IMouseItem
import java.util.*

class TestItem : Item(Properties()), IMouseItem, IKeyboardItem {
//	override fun onMouseScroll(heldStack: ItemStack, level: ClientLevel, player: Player): Boolean {
//		if (player.isShiftKeyDown) {
//			level.playLocalSound(player, SoundEvents.NOTE_BLOCK_PLING.value(), SoundSource.AMBIENT, 1f, 1f)
//			player.displayClientMessage(Component.literal("pling!"), false)
//			// todo packets are broken rn for some reason
//			PlatformServices.PLATFORM.sendToServer(ServerboundPacketTest(10, "test"))
//			return true
//		}
//		return false
//	}

	// todo sending updated dims to client,
	override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
		if (level.isClientSide) return super.use(level, player, usedHand)
		val serverLevel = level as ServerLevel
		val server = serverLevel.server
		val dimTypeRegistry = serverLevel.registryAccess().registryOrThrow(Registries.DIMENSION_TYPE)
		val dimRegistry = serverLevel.registryAccess().registryOrThrow(Registries.DIMENSION)
		val noiseRegistry = serverLevel.registryAccess().registryOrThrow(Registries.NOISE_SETTINGS)
		val biomeRegistry = serverLevel.registryAccess().registryOrThrow(Registries.BIOME)
		val location = BreadLib.modLoc("custom_dim")
		val stemKey = ResourceKey.create(Registries.LEVEL_STEM, location)
		val stemRegistry = level.registryAccess().registryOrThrow(Registries.LEVEL_STEM)
		val dimensionType = DimensionType(
			OptionalLong.of(18000L),
			true,
			false,
			true,
			false,
			8.0,
			true,
			true,
			0,
			512,
			128,
			BlockTags.INFINIBURN_NETHER,
			BuiltinDimensionTypes.OVERWORLD_EFFECTS,
			0.1F,
			DimensionType.MonsterSettings(true, false, ConstantInt.of(7), 15)
		)
		(dimTypeRegistry as IRegistryExtension).unfreeze()
		Registry.register(dimTypeRegistry, location, dimensionType)
		dimTypeRegistry.freeze()
		val stem = LevelStem(
			Holder.direct(dimensionType),
			NoiseBasedChunkGenerator(
				MultiNoiseBiomeSource.createFromList(
					Climate.ParameterList(listOf(
						Pair.of(Climate.ParameterPoint(
							Climate.Parameter(0, 100),
							Climate.Parameter(0, 100),
							Climate.Parameter(0, 100),
							Climate.Parameter(0, 100),
							Climate.Parameter(0, 100),
							Climate.Parameter(0, 100),
							1,
						), biomeRegistry.getHolderOrThrow(Biomes.JUNGLE))
					))
				),
				noiseRegistry.getHolderOrThrow(NoiseGeneratorSettings.END)
			)
		)
		(stemRegistry as IRegistryExtension).unfreeze()
		Registry.register(stemRegistry, location, stem)
		stemRegistry.freeze()
		val levelKey = ResourceKey.create(Registries.DIMENSION, stemKey.location())
		val newLevel = ServerLevel(
			server,
			server.executor,
			server.storageSource,
			DerivedLevelData(server.worldData, server.worldData.overworldData()),
			levelKey,
			stem,
			server.progressListenerFactory.create(server.worldData.gameRules.getInt(GameRules.RULE_SPAWN_CHUNK_RADIUS)),
			server.worldData.isDebugWorld,
			0,
			listOf(),
			true,
			null
		)
		server.levels[levelKey] = newLevel
//		(dimRegistry as IRegistryExtension).unfreeze() // todo maybe?
//		Registry.register(dimRegistry, levelKey, newLevel)
//		dimRegistry.freeze()
//		val access = LayeredRegistryAccess(listOf(RegistryLayer.DIMENSIONS))
		server.playerList.players.map { it.connection }.forEach { connection ->
			RegistrySynchronization.packRegistries(
				server.registries().compositeAccess().createSerializationContext(NbtOps.INSTANCE),
				server.registries().compositeAccess(),
				setOf()
			) { key, list -> connection.send(ClientboundRegistryDataPacket(key, list)) }
			connection.send(
				ClientboundUpdateTagsPacket(
					TagNetworkSerialization.serializeTagsToNetwork(server.registries())
				)
			)
		}
		return super.use(level, player, usedHand)
	}

	override fun onKeyPress(
		button: Int,
		scanCode: Int,
		action: Int,
		modifiers: Int,
		heldStack: ItemStack,
		level: ClientLevel,
		player: Player
	) {
		val key = InputConstants.getKey(button, scanCode).name
		player.displayClientMessage(Component.literal(key), true)
	}
}