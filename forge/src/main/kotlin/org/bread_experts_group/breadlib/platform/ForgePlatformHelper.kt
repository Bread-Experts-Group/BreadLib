package org.bread_experts_group.breadlib.platform

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.fml.ModList
import net.minecraftforge.fml.loading.FMLLoader
import net.minecraftforge.fml.loading.FMLPaths
import java.nio.file.Path

class ForgePlatformHelper : IPlatformHelper {
	override val platformName: String = "Forge"
	override val configDir: Path
		get() = FMLPaths.CONFIGDIR.get()
	override val gameDir: Path
		get() = FMLPaths.GAMEDIR.get()
	override val environmentKind: EnvironmentKind
		get() = if (FMLLoader.isProduction()) EnvironmentKind.RELEASE else EnvironmentKind.DEVELOPMENT
	override val side: ApplicationSide
		get() = if (FMLLoader.getDist() == Dist.CLIENT) ApplicationSide.CLIENT else ApplicationSide.SERVER

	override fun isModLoaded(modId: String): Boolean = ModList.get().isLoaded(modId)

	override fun getModInfo(modId: String): ModInfo {
		val container = ModList.get().getModContainerById(modId).get()
		val info = container.modInfo
		val dependencies = info.dependencies.map { it.modId }
		val path = info.owningFile.file.filePath
		val version = info.owningFile.versionString()

		return ModInfo(modId, info.description, version, dependencies, path)
	}
}