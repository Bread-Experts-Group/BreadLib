package org.bread_experts_group.breadlib.platform

import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.ModList
import net.neoforged.fml.loading.FMLLoader
import net.neoforged.fml.loading.FMLPaths
import java.nio.file.Path

class NeoForgePlatformHelper : IPlatformHelper {
	override val platformName: String = "NeoForge"
	override val configDir: Path
		get() = FMLPaths.CONFIGDIR.get()
	override val gameDir: Path
		get() = FMLPaths.GAMEDIR.get()
	override val environmentKind: EnvironmentKind
		get() = if (FMLLoader.isProduction()) EnvironmentKind.RELEASE else EnvironmentKind.DEVELOPMENT
	override val side: ApplicationSide
		get() = if (FMLLoader.getDist() == Dist.CLIENT) ApplicationSide.CLIENT else ApplicationSide.SERVER

	override fun isModLoaded(modId: String): Boolean {
		return ModList.get().isLoaded(modId)
	}

	override fun getModInfo(modId: String): ModInfo {
		val container = ModList.get().getModContainerById(modId).get()
		val info = container.modInfo
		val dependencies = info.dependencies.map { it.modId }
		val path = info.owningFile.file.findResource("/")
		val version = info.owningFile.versionString()

		return ModInfo(modId, info.description, version, dependencies, path)
	}
}