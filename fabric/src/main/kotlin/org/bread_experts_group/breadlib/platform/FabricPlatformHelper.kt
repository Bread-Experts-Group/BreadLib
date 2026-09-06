package org.bread_experts_group.breadlib.platform

import net.fabricmc.api.EnvType
import net.fabricmc.loader.api.FabricLoader
import net.fabricmc.loader.api.metadata.ModDependency
import java.nio.file.Path

class FabricPlatformHelper : IPlatformHelper {
	private val fabricLoader = FabricLoader.getInstance()

	override val platformName: String = "Fabric"
	override val configDir: Path
		get() = fabricLoader.configDir
	override val gameDir: Path
		get() = fabricLoader.gameDir
	override val environmentKind: EnvironmentKind
		get() = if (fabricLoader.isDevelopmentEnvironment) EnvironmentKind.DEVELOPMENT
		else EnvironmentKind.RELEASE
	override val side: ApplicationSide
		get() = if (fabricLoader.environmentType == EnvType.CLIENT) ApplicationSide.CLIENT
		else ApplicationSide.SERVER

	override fun isModLoaded(modId: String): Boolean = fabricLoader.isModLoaded(modId)

	override fun getModInfo(modId: String): ModInfo {
		check(isModLoaded(modId)) { "Mod $modId is not loaded, cannot retrieve info." }
		val container = fabricLoader.getModContainer(modId).get()
		val metadata = container.metadata
		val dependencies = metadata.dependencies.filter { it.kind == ModDependency.Kind.DEPENDS }.map { it.modId }
		val version = metadata.version.friendlyString
		val jarPath = container.origin.paths.first()

		return ModInfo(modId, metadata.description, version, dependencies, jarPath)
	}
}