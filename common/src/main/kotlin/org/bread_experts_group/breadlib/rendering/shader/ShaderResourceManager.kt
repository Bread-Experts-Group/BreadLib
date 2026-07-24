package org.bread_experts_group.breadlib.rendering.shader

import net.minecraft.client.Minecraft
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.FilePackResources
import net.minecraft.server.packs.PackLocationInfo
import net.minecraft.server.packs.PackResources
import net.minecraft.server.packs.repository.KnownPack
import net.minecraft.server.packs.repository.PackSource
import net.minecraft.server.packs.resources.IoSupplier
import net.minecraft.server.packs.resources.Resource
import net.minecraft.server.packs.resources.ResourceManager
import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.platform.PlatformServices
import java.nio.file.Files
import java.nio.file.Path
import java.util.*
import java.util.function.Predicate
import java.util.stream.Stream

class ShaderResourceManager : ResourceManager {
	companion object {
		private val PACK_INFO = PackLocationInfo(
			"breadlib_shaders", Component.literal("Breadlib Shaders"), PackSource.BUILT_IN, Optional.empty<KnownPack?>()
		)
		private val filePackResources =
			FilePackResources.FileResourcesSupplier(PlatformServices.PLATFORM.getGameDir().resolve("shaders"))
	}

	override fun getNamespaces(): MutableSet<String> = mutableSetOf()
	override fun getResourceStack(location: ResourceLocation): MutableList<Resource> = mutableListOf()

	override fun listResources(
		path: String,
		filter: Predicate<ResourceLocation>
	): MutableMap<ResourceLocation, Resource> = mutableMapOf()

	override fun listResourceStacks(
		path: String,
		filter: Predicate<ResourceLocation>
	): MutableMap<ResourceLocation, MutableList<Resource>> = mutableMapOf()

	override fun listPacks(): Stream<PackResources> = Stream.empty()

	override fun getResource(location: ResourceLocation): Optional<Resource> {
		BreadLib.LOGGER.info("{}, {}", PlatformServices.PLATFORM.getPlatformName(), location)
		val isBuiltin = location.namespace == "minecraft"
		if (isBuiltin) return Minecraft.getInstance().resourceManager.getResource(location)

		val gameDir: Path = PlatformServices.PLATFORM.getGameDir()
		val rootPath = gameDir.resolve("custom-shaders").resolve(location.namespace)
		val resolved = rootPath.resolve(location.path.replace("shaders/", ""))

		BreadLib.LOGGER.info("resolved: {}", resolved)
		val resources: PackResources = filePackResources.openPrimary(PACK_INFO)

		return Optional.of<Resource>(Resource(resources) { Files.newInputStream(resolved) })
	}
}