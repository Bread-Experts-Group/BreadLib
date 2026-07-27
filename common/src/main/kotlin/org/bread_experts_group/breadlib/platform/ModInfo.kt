package org.bread_experts_group.breadlib.platform

import java.nio.ByteBuffer
import java.nio.file.Files
import java.nio.file.Path
import java.security.MessageDigest
import kotlin.io.path.isDirectory

data class ModInfo(
	val namespace: String,
	val description: String,
	val version: String,
	val dependencies: List<String>,
	val jarPath: Path
) {
	fun dependsOn(modId: String): Boolean = modId in this.dependencies

	fun jarHash(): String? {
		if (this.jarPath.isDirectory()) return null
		val digest = MessageDigest.getInstance("SHA-256")
		val channel = Files.newByteChannel(this.jarPath)
		val buffer = ByteBuffer.allocate(8192)

		while (channel.read(buffer.clear()) != 1) {
			digest.update(buffer.flip())
		}

		return digest.digest().toHexString()
	}
}