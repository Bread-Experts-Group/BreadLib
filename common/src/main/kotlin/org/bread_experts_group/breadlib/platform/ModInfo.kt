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
	val path: Path
) {
	companion object {
		private val DIGEST = MessageDigest.getInstance("MD5")
		private val HASH_BUFFER = ByteBuffer.allocate(8192)
	}

	fun dependsOn(modId: String): Boolean = modId in this.dependencies

	val hash: String? by lazy {
		if (this.path.isDirectory()) return@lazy null
		synchronized(DIGEST) {
			DIGEST.reset()
			Files.newByteChannel(this.path).use { channel ->
				synchronized(HASH_BUFFER) {
					while (channel.read(HASH_BUFFER.clear()) != -1) DIGEST.update(HASH_BUFFER.flip())
					DIGEST.digest().toHexString()
				}
			}
		}
	}
}