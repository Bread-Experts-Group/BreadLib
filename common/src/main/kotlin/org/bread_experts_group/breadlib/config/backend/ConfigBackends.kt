package org.bread_experts_group.breadlib.config.backend

import org.bread_experts_group.breadlib.BreadLib
import org.bread_experts_group.breadlib.config.backend.builtin.ConfigJSONBackend
import org.bread_experts_group.breadlib.config.backend.builtin.ConfigTOMLBackend

object ConfigBackends {
	private val backends = mutableMapOf<String, MutableMap<String, ConfigBackend>>()

	init {
		this.registerBackend(BreadLib.MOD_ID, ConfigTOMLBackend)
		this.registerBackend(BreadLib.MOD_ID, ConfigJSONBackend)
	}

	@JvmStatic
	fun registerBackend(modID: String, backend: ConfigBackend) {
		val modBackends = backends.getOrPut(backend.extension.lowercase()) { mutableMapOf() }
		require(!modBackends.containsKey(modID)) { "$modID already registered a backend for ${backend.extension}" }
		modBackends[modID] = backend
	}

	@JvmStatic
	fun getBackend(extension: String, modID: String = BreadLib.MOD_ID): ConfigBackend {
		val throwing = {
			throw IllegalArgumentException("$modID doesn't define a backend for $extension")
		}
		return backends.getOrElse(extension.lowercase(), throwing).getOrElse(modID, throwing)
	}
}