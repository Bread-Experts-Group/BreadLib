package org.bread_experts_group.breadlib.platform

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import java.util.concurrent.CompletableFuture

class BreadLibWorldGenProvider(
	output: FabricDataOutput,
	futures: CompletableFuture<HolderLookup.Provider>
): FabricDynamicRegistryProvider(output, futures) {
	override fun configure(registries: HolderLookup.Provider, entries: Entries) {
		entries.addAll(registries.lookupOrThrow(Registries.DIMENSION_TYPE))
		entries.addAll(registries.lookupOrThrow(Registries.LEVEL_STEM))
		entries.addAll(registries.lookupOrThrow(Registries.BIOME))
		entries.addAll(registries.lookupOrThrow(Registries.NOISE_SETTINGS))
	}

	override fun getName(): String = "BreadLibWorldgenProvider"
}