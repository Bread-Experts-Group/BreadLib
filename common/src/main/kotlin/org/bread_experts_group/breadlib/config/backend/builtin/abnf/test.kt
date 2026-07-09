package org.bread_experts_group.breadlib.config.backend.builtin.abnf

import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.toml

fun main() {
	try {
		println(ABNFReader("a = 1234").also { it.tasks.add(toml) } .resolve())
	} catch (e: Throwable) {
		e.printStackTrace()
	}
}