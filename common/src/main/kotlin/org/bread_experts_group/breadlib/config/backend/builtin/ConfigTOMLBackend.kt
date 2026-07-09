package org.bread_experts_group.breadlib.config.backend.builtin

import org.bread_experts_group.breadlib.config.backend.ConfigBackend
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.ABNFReader
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.ABNFResolved
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.`basic-string`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.`basic-unescaped`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.`dec-int`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.`dotted-key`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.escaped
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.expEm
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.expKv
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.`literal-string`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.`non-ascii`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.`quoted-key`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.string
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.toml
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.`unquoted-key`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.wschar
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText

object ConfigTOMLBackend : ConfigBackend {
	override val extension: String = "toml"
	override fun decode(path: Path): Map<String, Any> = buildMap {
		fun decodeString(a: ABNFResolved): String = when (val rule = a.rule) {
			`basic-string`.rule -> {
				var str = ""
				((a as ABNFResolved.ABNFString).concatenated[1] as ABNFResolved.ABNFRepetition).selected.forEach {
					val char = it
					when (char.rule) {
						`basic-unescaped` -> {
							val char = char
							str += Char(
								(char as? ABNFResolved.ABNFCharacter ?: when (char.rule) {
									wschar, `non-ascii` -> char as ABNFResolved.ABNFCharacter
									else -> throw IllegalStateException("${char.rule?.name} - ${char.rule} - $char")
								}).character.toInt()
							)
						}

						escaped -> TODO("B")
						else -> throw IllegalStateException()
					}
				}
				str
			}

			`literal-string` -> {
				var str = ""
				((a as ABNFResolved.ABNFString).concatenated[1] as ABNFResolved.ABNFRepetition).selected.forEach {
					val char = it
					str += Char(
						((char as? ABNFResolved.ABNFCharacter) ?: char as ABNFResolved.ABNFCharacter)
							.character.toInt()
					)
				}
				str
			}

			else -> throw IllegalStateException("${rule?.name} - $rule - $a")
		}

		fun decodeSimpleKey(a: ABNFResolved): String {
			return when (val rule = a.rule) {
				`quoted-key` -> decodeString(a)
				`unquoted-key` -> {
					var str = ""
					(a as ABNFResolved.ABNFRepetition).selected.forEach {
						str += Char(
							when (val sel = it) {
								is ABNFResolved.ABNFCharacter -> sel.character
								else -> throw IllegalStateException()
							}.toInt()
						)
					}
					str
				}
				else -> throw IllegalStateException("${rule?.name} - $rule - $a")
			}
		}

		fun decodeKey(a: ABNFResolved): List<String> = when (val rule = a.rule) {
			`unquoted-key` -> listOf(decodeSimpleKey(a))
			`dotted-key` -> buildList {
				a as ABNFResolved.ABNFString
				add(decodeSimpleKey(a.concatenated[0]))
				(a.concatenated[1] as ABNFResolved.ABNFRepetition).selected.forEach {
					add(decodeSimpleKey((it as ABNFResolved.ABNFString).concatenated[1]))
				}
			}

			else -> throw IllegalStateException("${rule?.name} - $rule - $a")
		}

		fun decodeVal(a: ABNFResolved): Any = when (val rule = a.rule) {
			string -> decodeString(a)
			`dec-int` -> {
				val negative = ((a as ABNFResolved.ABNFString).concatenated[0] as ABNFResolved.ABNFRepetition).selected.firstOrNull()?.let {
					(it as ABNFResolved.ABNFCharacter).character == '-'.code.toUInt()
				} ?: false
				val n = when (val c = a.concatenated[1]) {
					is ABNFResolved.ABNFCharacter -> Char(c.character.toInt()).toString()
					is ABNFResolved.ABNFString -> TODO("STR")
					else -> throw IllegalStateException()
				}
				BigDecimal("${if (negative) "-" else ""}$n")
			}

			else -> throw IllegalStateException("${rule?.name} - $rule - $a")
		}

		fun decodeToml(a: ABNFResolved): Any? = when (a.rule) {
			toml -> buildMap {
				@Suppress("UNCHECKED_CAST")
				buildList {
					add(decodeToml((a as ABNFResolved.ABNFString).concatenated[0]))
					(a.concatenated[1] as ABNFResolved.ABNFRepetition).selected.forEach {
						add(decodeToml((it as ABNFResolved.ABNFString).concatenated[1]))
					}
				}.filterNotNull().forEach { kv ->
					kv as Pair<List<String>, Any>
					var element = this
					for (i in 0..<kv.first.lastIndex) {
						val localElement = element.getOrPut(kv.first[i]) { mutableMapOf<String, Any>() }
						if (localElement !is Map<*, *>) throw IllegalArgumentException("${kv.first} cannot exist: at element $i, was defined as $localElement")
						element = localElement as MutableMap<String, Any>
					}
					element[kv.first.last()] = kv.second
				}
			}

			expKv -> {
				val keyval = (a as ABNFResolved.ABNFString).concatenated[1] as ABNFResolved.ABNFString
				val key = decodeKey(keyval.concatenated[0])
				val value = decodeVal(keyval.concatenated[2])
				key to value
			}

			expEm -> null
			else -> throw IllegalStateException("${a.rule?.name} - ${a.rule} - $a")
		}

		@Suppress("UNCHECKED_CAST")
		return decodeToml(ABNFReader(path.readText(Charsets.UTF_8)).also { it.tasks.add(toml) }.resolve().first) as Map<String, Any>
	}

	override fun encode(path: Path, config: Map<String, Any?>) {
		var toWrite = ""
		fun write(map: Map<String, Any?>) {
			next@for ((name, value) in map.entries.sortedBy { it.value is Map<*, *> }) {
				if (value == null) continue
				@Suppress("UNCHECKED_CAST")
				if (value is Map<*, *>) {
					var name = name
					var table = value as Map<String, Any>
					while (true) {
						if (table.isEmpty()) continue@next
						else if (table.size == 1) {
							val (key, value) = table.entries.first()
							if (value is Map<*, *>) {
								name += ".$key"
								table = value as Map<String, Any>
							} else break
						} else break
					}
					toWrite += "\n[$name]\n"
					write(table)
					continue
				}

				val valueStr = when (value) {
					is BigDecimal -> "$value"
					is String -> "\"$value\""
					else -> throw IllegalArgumentException("Cannot encode ${value::class}: $value")
				}

				toWrite += (if (
					name.isNotEmpty() && name.all {
						it in 'A'..'Z' ||
						it in 'a'..'z' ||
						it in '0'..'9' ||
						it == '_' || it == '-'
					}
				) "$name = $valueStr" else if (
					name.all {
						it.code == 0x09 ||
						it.code in 0x20..0x26 ||
						it.code in 0x28..0x7E ||
						it.code in 0x80..0xD7FF ||
						it.code in 0xE000..0x10FFFF
					}
				) "'$name' = $valueStr"
				else "\"$name\" = $valueStr") + '\n'
			}
		}

		write(config)
		Files.writeString(path, toWrite, Charsets.UTF_8)
	}
}