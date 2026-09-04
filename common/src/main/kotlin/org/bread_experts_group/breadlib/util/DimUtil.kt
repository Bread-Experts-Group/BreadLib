package org.bread_experts_group.breadlib.util

import net.minecraft.core.MappedRegistry
import net.minecraft.core.Registry
import net.minecraft.core.RegistryAccess
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.biome.BiomeManager
import net.minecraft.world.level.border.BorderChangeListener
import net.minecraft.world.level.dimension.LevelStem
import net.minecraft.world.level.storage.DerivedLevelData
import org.bread_experts_group.breadlib.dimension.DimensionUpdatePacket
import org.bread_experts_group.breadlib.platform.PlatformServices
import org.bread_experts_group.breadlib.test.network.ServerboundPacketTest

// This code was derived from the ideas in:
// https://github.com/McJtyMods/RFToolsDimensions/blob/1.21_neo/src/main/java/mcjty/rftoolsdim/dimension/tools/DynamicDimensionManager.java

object DimUtil {
    //    final MinecraftServer server,
    //    final Map<ResourceKey<Level>, ServerLevel> map,
    //    final ResourceKey<Level> worldKey,
    //    final BiFunction<MinecraftServer, ResourceKey<LevelStem>, LevelStem> dimensionFactory
    fun createAndRegisterWorldAndDimension(
        server: MinecraftServer, worldKey: ResourceKey<Level>,
        dimension: (MinecraftServer, ResourceKey<LevelStem>) -> LevelStem
    ) {
        val overworld = server.getLevel(Level.OVERWORLD)!!

        val dimensionKey = ResourceKey.create(Registries.LEVEL_STEM, worldKey.location())
        val dimension = dimension(server, dimensionKey)

        val progressListener = server.progressListenerFactory.create(11)
        val executor = server.executor
        val storageSource = server.storageSource
        val worldData = server.worldData
        val worldGenSettings = worldData.worldGenOptions()
        val derivedLevelData = DerivedLevelData(worldData, worldData.overworldData())

        val regComposite = server.registries().compositeAccess() as RegistryAccess.ImmutableRegistryAccess

        val regMap = buildMap {
            regComposite.registries().forEach {
                put(it.key, it.value)
            }
        }.toMutableMap()
        val key = ResourceKey.create(
            ResourceKey.createRegistryKey<Registry<LevelStem>>(ResourceLocation.parse("root")),
            ResourceLocation.parse("dimension")
        )
        val oldRegistry = regMap[key]!!
        val oldLifecycle = oldRegistry.registryLifecycle()

        val newRegistry = MappedRegistry(Registries.LEVEL_STEM, oldLifecycle, false)
        oldRegistry.entrySet().forEach { (key, value) ->
            val oldLevelKey = ResourceKey.create(Registries.DIMENSION, key.location())
            if (value != null && oldLevelKey != key) {
                Registry.register(newRegistry, key as ResourceKey<LevelStem>, value as LevelStem)
            }
        }

        Registry.register(newRegistry, dimensionKey, dimension)
        regMap[key] = newRegistry
        regComposite.registries = regMap

        val newWorld = ServerLevel(
            server,
            executor,
            storageSource,
            derivedLevelData,
            worldKey,
            dimension,
            progressListener,
            false,
            BiomeManager.obfuscateSeed(worldGenSettings.seed()),
            listOf(),
            false,
            null
        )

        overworld.worldBorder.addListener(
            BorderChangeListener.DelegateBorderChangeListener(newWorld.worldBorder)
        )

        PlatformServices.PLATFORM.sendToAllPlayers(
            DimensionUpdatePacket(worldKey),
            overworld
        )

        server.playerList.players.forEach {
            it.teleportTo(newWorld, 0.0, 100.0, 0.0, 0f, 0f)
        }


        /*
        // update forge's world cache so the new level can be ticked
        server.markWorldsDirty();

        // fire world load event
        NeoForge.EVENT_BUS.post(new LevelEvent.Load(newWorld));

        // update clients' dimension lists
        PacketSyncDimensionListChanges.updateClientDimensionLists(ImmutableSet.of(worldKey), ImmutableSet.of());

        return newWorld;
         */
    }
}