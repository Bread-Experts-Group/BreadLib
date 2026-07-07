package org.bread_experts_group.breadlib.config.backend

import java.nio.file.Path

interface ConfigBackend {
	val extension: String
	fun decode(path: Path): Map<String, Any?>
	fun encode(path: Path, config: Map<String, Any?>)
}