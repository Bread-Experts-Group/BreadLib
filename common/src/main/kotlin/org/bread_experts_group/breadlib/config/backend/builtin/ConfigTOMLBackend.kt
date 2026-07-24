package org.bread_experts_group.breadlib.config.backend.builtin

import org.bread_experts_group.breadlib.config.backend.ConfigBackend
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.ABNFReader
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.ABNFResolved
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.ABNFTask
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml._float
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.`basic-string`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.`bin-int`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.`dec-int`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.`dotted-key`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.escaped
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.exp
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.expEm
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.expKv
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.`false`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.`hex-int`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.inf
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.`literal-string`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.nan
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.`oct-int`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.`quoted-key`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.`special-float`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.toml
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.`true`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFToml.`unquoted-key`
import java.math.BigDecimal
import java.math.BigInteger
import java.nio.file.Files
import java.nio.file.Path
import kotlin.io.path.readText

object ConfigTOMLBackend : ConfigBackend {
	override val extension: String = "toml"
	override fun decode(path: Path): Map<String, Any> = buildMap {
		fun decodeBasicString(a: ABNFResolved): String {
			var str = ""
			((a as ABNFResolved.ABNFString).concatenated[1] as ABNFResolved.ABNFRepetition).selected.forEach { char ->
				if (char is ABNFResolved.ABNFCharacter) str += Char(char.character.toInt())
				else if (char.rule == escaped) TODO("B")
			}
			return str
		}

		fun decodeLiteralString(a: ABNFResolved): String {
			var str = ""
			((a as ABNFResolved.ABNFString).concatenated[1] as ABNFResolved.ABNFRepetition).selected.forEach {
				val char = it
				str += Char(
					((char as? ABNFResolved.ABNFCharacter) ?: char as ABNFResolved.ABNFCharacter)
						.character.toInt()
				)
			}
			return str
		}

		fun decodeString(a: ABNFResolved): String = when (val rule = a.rule) {
			`basic-string`.rule -> decodeBasicString(a)
			`literal-string` -> decodeLiteralString(a)
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
			`basic-string`.rule, `literal-string` -> listOf(decodeString(a))
			`dotted-key` -> buildList {
				a as ABNFResolved.ABNFString
				add(decodeSimpleKey(a.concatenated[0]))
				(a.concatenated[1] as ABNFResolved.ABNFRepetition).selected.forEach {
					add(decodeSimpleKey((it as ABNFResolved.ABNFString).concatenated[1]))
				}
			}

			else -> throw IllegalStateException("${rule?.name} - $rule - $a")
		}

		fun decodeUInt(a: ABNFResolved): String {
			val negative =
				((a as ABNFResolved.ABNFString).concatenated[0] as ABNFResolved.ABNFRepetition).selected.firstOrNull()
					?.let {
						(it as ABNFResolved.ABNFCharacter).character == '-'.code.toUInt()
					} ?: false
			val n = when (val c = a.concatenated[1]) {
				is ABNFResolved.ABNFCharacter -> Char(c.character.toInt()).toString()
				is ABNFResolved.ABNFString -> {
					var str = ""
					str += Char((c.concatenated[0] as ABNFResolved.ABNFCharacter).character.toInt())
					(c.concatenated[1] as ABNFResolved.ABNFRepetition).selected.forEach {
						val (_, character) = it as? ABNFResolved.ABNFCharacter
							?: (it as ABNFResolved.ABNFString).concatenated[1] as ABNFResolved.ABNFCharacter
						str += Char(character.toInt())
					}
					str
				}

				else -> throw IllegalStateException()
			}
			return "${if (negative) "-" else ""}$n"
		}

		fun decodeVal(a: ABNFResolved): Any = when (val rule = a.rule) {
			`true` -> true
			`false` -> false
			`basic-string`.rule -> decodeBasicString(a)
			`literal-string` -> decodeLiteralString(a)

			`hex-int` -> {
				a as ABNFResolved.ABNFString
				var str = ""
				str += Char((a.concatenated[1] as ABNFResolved.ABNFCharacter).character.toInt())
				(a.concatenated[2] as ABNFResolved.ABNFRepetition).selected.forEach {
					val (_, character) = it as? ABNFResolved.ABNFCharacter
						?: (it as ABNFResolved.ABNFString).concatenated[1] as ABNFResolved.ABNFCharacter
					str += Char(character.toInt())
				}
				BigInteger(str, 16).toBigDecimal()
			}

			`oct-int` -> {
				a as ABNFResolved.ABNFString
				var str = ""
				str += Char((a.concatenated[1] as ABNFResolved.ABNFCharacter).character.toInt())
				(a.concatenated[2] as ABNFResolved.ABNFRepetition).selected.forEach {
					val (_, character) = it as? ABNFResolved.ABNFCharacter
						?: (it as ABNFResolved.ABNFString).concatenated[1] as ABNFResolved.ABNFCharacter
					str += Char(character.toInt())
				}
				BigInteger(str, 8).toBigDecimal()
			}

			`bin-int` -> {
				a as ABNFResolved.ABNFString
				var str = ""
				str += Char((a.concatenated[1] as ABNFResolved.ABNFCharacter).character.toInt())
				(a.concatenated[2] as ABNFResolved.ABNFRepetition).selected.forEach {
					val (_, character) = it as? ABNFResolved.ABNFCharacter
						?: (it as ABNFResolved.ABNFString).concatenated[1] as ABNFResolved.ABNFCharacter
					str += Char(character.toInt())
				}
				BigInteger(str, 2).toBigDecimal()
			}

			`dec-int` -> decodeUInt(a).toBigDecimal()
			_float -> {
				fun readZPI(a: ABNFResolved): String {
					a as ABNFResolved.ABNFString
					var str = Char((a.concatenated[0] as ABNFResolved.ABNFCharacter).character.toInt()).toString()
					((a.concatenated[1]) as ABNFResolved.ABNFRepetition).selected.forEach { zpiD ->
						str += Char(
							((zpiD as? ABNFResolved.ABNFCharacter)
								?: ((zpiD as ABNFResolved.ABNFString).concatenated[1] as ABNFResolved.ABNFCharacter)).character.toInt()
						)
					}
					return str
				}

				fun readExp(a: ABNFResolved): String {
					val (_, concatenated) = (a as ABNFResolved.ABNFString).concatenated[1] as ABNFResolved.ABNFString
					val str = "e" + ((concatenated[0] as ABNFResolved.ABNFRepetition).selected.firstOrNull()?.let {
						if (it.rule == ABNFToml.minus) "-" else ""
					} ?: "")
					return str + readZPI(concatenated[1])
				}

				val intPart = decodeUInt((a as ABNFResolved.ABNFString).concatenated[0])
				val frac = a.concatenated[1].let {
					if (it.rule == exp) readExp(it)
					else {
						val zpi =
							((it as ABNFResolved.ABNFString).concatenated[0] as ABNFResolved.ABNFString).concatenated[1]
						val exp = (it.concatenated[1] as ABNFResolved.ABNFRepetition).selected.firstOrNull()
							?.let { e -> readExp(e) } ?: ""
						".${readZPI(zpi)}${exp}"
					}
				}
				BigDecimal("$intPart$frac")
			}

			`special-float` -> {
				val negative =
					((a as ABNFResolved.ABNFString).concatenated[0] as ABNFResolved.ABNFRepetition).selected.firstOrNull()
						?.let {
							(it as ABNFResolved.ABNFCharacter).character == '-'.code.toUInt()
						} ?: false
				when (a.concatenated[1].rule) {
					inf -> if (negative) Double.NEGATIVE_INFINITY else Double.POSITIVE_INFINITY
					nan -> if (negative) java.lang.Double.longBitsToDouble((0xfff8000000000000u).toLong()) else
						java.lang.Double.longBitsToDouble(0x7ff8000000000000)

					else -> throw IllegalStateException()
				}
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
						require(localElement is Map<*, *>) { "${kv.first} cannot exist: at element $i, was defined as $localElement" }
						element = localElement as MutableMap<String, Any>
					}
					element[kv.first.last()] = kv.second
				}
			}

			expKv -> {
				val (_, concatenated) = (a as ABNFResolved.ABNFString).concatenated[1] as ABNFResolved.ABNFString
				val key = decodeKey(concatenated[0])
				val value = decodeVal(concatenated[2])
				key to value
			}

			expEm -> null
			else -> throw IllegalStateException("${a.rule?.name} - ${a.rule} - $a")
		}

		@Suppress("UNCHECKED_CAST")
		return decodeToml(ABNFReader(path.readText(Charsets.UTF_8)).also { it.tasks.add(ABNFTask(toml, 0, 0)) }
			.resolve().first) as Map<String, Any>
	}

	override fun encode(path: Path, config: Map<String, Any?>) {
		var toWrite = ""
		fun write(map: Map<String, Any?>) {
			next@ for ((name, value) in map.entries.sortedBy { (_, value) -> value is Map<*, *> }) {
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
					is Double if value == Double.POSITIVE_INFINITY -> "+inf"
					is Double if value == Double.NEGATIVE_INFINITY -> "-inf"
					is Double if java.lang.Double.isNaN(value) -> {
						if (java.lang.Double.doubleToRawLongBits(value) ushr 63 != 0L) "-nan"
						else "+nan"
					}

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