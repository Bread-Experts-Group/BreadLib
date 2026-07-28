package org.bread_experts_group.breadlib.rendering.shader

import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.FilePackResources
import net.minecraft.server.packs.PackLocationInfo
import net.minecraft.server.packs.PackResources
import net.minecraft.server.packs.repository.KnownPack
import net.minecraft.server.packs.repository.PackSource
import net.minecraft.server.packs.resources.Resource
import net.minecraft.server.packs.resources.ResourceManager
import org.bread_experts_group.breadlib.platform.PlatformServices
import org.bread_experts_group.breadlib.util.minecraft
import org.bread_experts_group.breadlib.util.optional
import org.bread_experts_group.breadlib.util.resolve
import java.io.IOException
import java.nio.file.FileSystems
import java.nio.file.Path
import java.util.*
import java.util.function.Predicate
import java.util.stream.Stream
import kotlin.io.path.inputStream
import kotlin.io.path.isDirectory

class ShaderResourceManager : ResourceManager {
	companion object {
		private val PACK_INFO = PackLocationInfo(
			"breadlib_shaders", Component.literal("Breadlib Shaders"), PackSource.BUILT_IN, Optional.empty<KnownPack?>()
		)
		private val filePackResources =
			FilePackResources.FileResourcesSupplier(PlatformServices.PLATFORM.gameDir.resolve("shaders"))
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

	private fun createResource(input: Path): Resource {
		val resources: PackResources = filePackResources.openPrimary(PACK_INFO)
		return Resource(resources) { input.inputStream().buffered() }
	}

	override fun getResource(location: ResourceLocation): Optional<Resource> {
//		BreadLib.LOGGER.info("{}, {}", PlatformServices.PLATFORM.getPlatformName(), location)
		val isBuiltin = location.namespace == ResourceLocation.DEFAULT_NAMESPACE
		if (isBuiltin) return minecraft!!.resourceManager.getResource(location)

		if (PlatformServices.PLATFORM.isModLoaded(location.namespace)) {
			val info = PlatformServices.PLATFORM.getModInfo(location.namespace)
			val resolution = arrayOf("bl_shaders", location.namespace, location.path.substringAfter("shaders/"))
			val dynamicShaders = PlatformServices.PLATFORM.gameDir.resolve(*resolution)
			try {
				return createResource(dynamicShaders).optional()
			} catch (_: IOException) {
			}

			val path = info.path.let {
				if (it.isDirectory()) it
				else FileSystems.newFileSystem(info.path).getPath("/")
			}.resolve(*resolution)
			return createResource(path).optional()
		}
		return Optional.empty()
//		BreadLib.LOGGER.info("resolved: {}", resolved)
	}
}