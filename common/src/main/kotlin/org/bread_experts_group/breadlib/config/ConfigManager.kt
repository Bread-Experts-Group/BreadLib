package org.bread_experts_group.breadlib.config

import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.config.backend.ConfigBackend
import org.bread_experts_group.breadlib.config.backend.ConfigBackends
import org.bread_experts_group.breadlib.platform.PlatformServices
import java.io.IOException
import kotlin.io.path.createDirectories
import kotlin.io.path.createFile

class ConfigManager(private val modID: String) {
	operator fun get(name: String, backend: ConfigBackend): ConfigFile = ConfigFile(
		PlatformServices.PLATFORM.configDir.resolve(modID).createDirectories()
			.resolve("$name.${backend.extension}").also {
			it.parent.createDirectories()
			try {
				it.createFile()
			} catch (_: IOException) {
			}
		},
		backend
	)

	operator fun get(name: String, extension: String, modID: String = BreadLib.MOD_ID): ConfigFile {
		return this[name, ConfigBackends.getBackend(extension, modID)]
	}
}