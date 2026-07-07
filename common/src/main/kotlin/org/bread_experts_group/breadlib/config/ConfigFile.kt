package org.bread_experts_group.breadlib.config

import org.bread_experts_group.breadlib.config.backend.ConfigBackend
import java.nio.file.Path
import java.nio.file.StandardWatchEventKinds
import kotlin.io.path.name

class ConfigFile(private val path: Path, private val backend: ConfigBackend) {
	private var currentState = backend.decode(path).toMutableMap()

	init {
		try {
			val watcher = path.fileSystem.newWatchService()
			Thread.ofVirtual().start {
				path.parent.register(watcher, StandardWatchEventKinds.ENTRY_MODIFY)
				while (true) {
					val watchKey = watcher.take()
					for (event in watchKey.pollEvents()) when (event.kind()) {
						StandardWatchEventKinds.ENTRY_MODIFY if ((event.context() as Path).name == path.name) -> currentState = backend.decode(path).toMutableMap()
					}
					if (!watchKey.reset()) {
						watcher.close()
						break
					}
				}
			}
		} catch (_: UnsupportedOperationException) {
		}
	}

	@Suppress("UNCHECKED_CAST")
	fun <T> getOrNull(key: ConfigValue<T>): T? = currentState.getOrElse(key.name) {
		key.defaultValue?.get()
	} as? T

	operator fun <T> get(key: ConfigValue<T>): T = getOrNull(key)!!

	operator fun <T> set(key: ConfigValue<T>, value: T?) {
		val previous = if (value == null) this.currentState.remove(key.name)
		else this.currentState[key.name] = value
		if (previous != value) this.backend.encode(path, currentState.toMap())
	}
}