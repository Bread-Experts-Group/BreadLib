package org.bread_experts_group.breadlib.config.backend.builtin

import org.bread_experts_group.breadlib.config.backend.ConfigBackend
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.ABNFReader
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.ABNFToml
import org.bread_experts_group.breadlib.util.info
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText

object ConfigTOMLBackend : ConfigBackend {
	override val extension: String = "toml"
	override fun decode(path: Path): Map<String, Any?> = buildMap {
		info(ABNFReader().resolve(path.readText(Charsets.UTF_8), ABNFToml.toml).rule?.name)
	}

	override fun encode(path: Path, config: Map<String, Any?>) {
		var toWrite = ""
		config.forEach { (name, value) ->
			val valueStr = when (value) {
				is Int -> "$value"
				else -> throw IllegalArgumentException("Cannot encode ${value!!::class}: $value")
			}

			if (
				name.isNotEmpty() && name.all {
					it in 'A'..'Z' ||
					it in 'a'..'z' ||
					it in '0'..'9' ||
					it == '_' || it == '-'
				}
			) toWrite += "$name = $valueStr"
			else toWrite += "\"$name\" = $valueStr"
		}
		Files.writeString(path, toWrite, Charsets.UTF_8)
	}
}