package org.bread_experts_group.breadlib.config.backend.builtin.abnf

import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.toml

fun main() {
	println(ABNFReader("a.b=true").also { it.tasks.add(ABNFTask(toml, 0, 0)) }.resolve())
}