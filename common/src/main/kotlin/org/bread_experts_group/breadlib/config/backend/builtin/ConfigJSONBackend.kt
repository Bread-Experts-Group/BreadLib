package org.bread_experts_group.breadlib.config.backend.builtin

import org.bread_experts_group.breadlib.config.backend.ConfigBackend
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.ABNFResolved
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFJson
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFJson.`JSON-text`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFJson.array
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFJson.`false`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFJson.`null`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFJson.number
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFJson.`object`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFJson.string
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFJson.`true`
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFJson.unescaped
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin.ABNFJson.zero
import java.math.BigDecimal
import java.nio.file.Files
import java.nio.file.Path

object ConfigJSONBackend : ConfigBackend {
	override val extension: String = "json"
	override fun decode(path: Path): Map<String, Any?> {
		fun decodeStr(rep: ABNFResolved.ABNFString): String {
			var str = ""
			(rep.concatenated[1] as ABNFResolved.ABNFRepetition).selected.forEach {
				val selected = (it as ABNFResolved.ABNFAlternate).selected
				if (selected.rule == unescaped) {
					val sel = (selected as ABNFResolved.ABNFAlternate).selected
					str += Char((sel as ABNFResolved.ABNFCharacter).character.toInt())
				} else {
					val sel = ((selected as ABNFResolved.ABNFString).concatenated[1] as ABNFResolved.ABNFAlternate).selected
					if (sel is ABNFResolved.ABNFCharacter) str += when (val char = Char(sel.character.toInt())) {
						'b' -> '\b'
						'f' -> '\u000C'
						'n' -> '\n'
						'r' -> '\r'
						't' -> '\t'
						else -> char
					} else {
						val hexDig = (sel as ABNFResolved.ABNFString).concatenated[1] as ABNFResolved.ABNFRepetition
						var hexStr = ""
						hexDig.selected.forEach { hexAlt ->
							val hexChar = (hexAlt as ABNFResolved.ABNFAlternate).selected
							hexStr += if (hexChar is ABNFResolved.ABNFCharacter) Char(hexChar.character.toInt())
							else {
								val char = (hexChar as ABNFResolved.ABNFAlternate).selected as ABNFResolved.ABNFCharacter
								Char(char.character.toInt())
							}
						}
						str += Char(hexStr.toInt(16))
					}
				}
			}
			return str
		}

		fun decodeJson(a: ABNFResolved): Any? = when (a.rule) {
			`JSON-text` -> decodeJson(((a as ABNFResolved.ABNFString).concatenated[1] as ABNFResolved.ABNFAlternate).selected)
			`object` -> {
				val members = mutableListOf<ABNFResolved.ABNFString>()
				val initial = ((a as ABNFResolved.ABNFString).concatenated[1] as ABNFResolved.ABNFRepetition).selected
				if (initial.isNotEmpty()) {
					val initialMember = initial[0] as ABNFResolved.ABNFString
					members.add(initialMember.concatenated[0] as ABNFResolved.ABNFString)
					for (resolved in (initialMember.concatenated[1] as ABNFResolved.ABNFRepetition).selected) {
						members.add((resolved as ABNFResolved.ABNFString).concatenated[1] as ABNFResolved.ABNFString)
					}
				}
				members.associate {
					decodeStr(it.concatenated[0] as ABNFResolved.ABNFString) to
							decodeJson((it.concatenated[2] as ABNFResolved.ABNFAlternate).selected)
				}
			}

			array -> {
				val members = mutableListOf<ABNFResolved>()
				val initial = ((a as ABNFResolved.ABNFString).concatenated[1] as ABNFResolved.ABNFRepetition).selected
				if (initial.isNotEmpty()) {
					val initialMember = initial[0] as ABNFResolved.ABNFString
					members.add((initialMember.concatenated[0] as ABNFResolved.ABNFAlternate).selected)
					for (resolved in (initialMember.concatenated[1] as ABNFResolved.ABNFRepetition).selected) {
						members.add(((resolved as ABNFResolved.ABNFString).concatenated[1] as ABNFResolved.ABNFAlternate).selected)
					}
				}
				members.map { decodeJson(it) }
			}

			`null` -> null
			`true` -> true
			`false` -> false
			string -> decodeStr(a as ABNFResolved.ABNFString)

			number -> {
				a as ABNFResolved.ABNFString
				val negative = (a.concatenated[0] as ABNFResolved.ABNFRepetition).selected.isNotEmpty()
				val int = (a.concatenated[1] as ABNFResolved.ABNFAlternate).selected.let {
					if (it.rule == zero) return@let BigDecimal.ZERO

					val int = (it as ABNFResolved.ABNFString).concatenated
					var str = Char((int[0] as ABNFResolved.ABNFCharacter).character.toInt()).toString()
					(int[1] as ABNFResolved.ABNFRepetition).selected.forEach { char ->
						str += Char((char as ABNFResolved.ABNFCharacter).character.toInt())
					}
					str
				}
				val frac = (a.concatenated[2] as ABNFResolved.ABNFRepetition).selected.getOrNull(0)?.let {
					val frac = (it as ABNFResolved.ABNFString).concatenated
					var str = ""
					(frac[1] as ABNFResolved.ABNFRepetition).selected.forEach { char ->
						str += Char((char as ABNFResolved.ABNFCharacter).character.toInt())
					}
					str
				} ?: "0"
				val exp = (a.concatenated[3] as ABNFResolved.ABNFRepetition).selected.getOrNull(0)?.let {
					val frac = (it as ABNFResolved.ABNFString).concatenated
					var str = (frac[1] as ABNFResolved.ABNFRepetition).selected.getOrNull(0)?.let { expSign ->
						if (
							((expSign as ABNFResolved.ABNFAlternate).selected as ABNFResolved.ABNFCharacter).character == ABNFJson.minus.character
						) "-" else ""
					} ?: ""
					(frac[2] as ABNFResolved.ABNFRepetition).selected.forEach { char ->
						str += Char((char as ABNFResolved.ABNFCharacter).character.toInt())
					}
					str
				} ?: "0"
				BigDecimal("${if (negative) '-' else ""}$int.${frac}e$exp")
			}

			else -> throw IllegalStateException("${a.rule?.name} - ${a.rule} - $a")
		}

		TODO("JSON")
//		@Suppress("UNCHECKED_CAST")
//		return decodeJson(ABNFReader().resolve(path.readText(Charsets.UTF_8).ifEmpty { "{}" }, `JSON-text`).first) as Map<String, Any?>
	}

	override fun encode(path: Path, config: Map<String, Any?>) = Files.newBufferedWriter(path, Charsets.UTF_8).use {
		fun write(type: Any?): Unit = when (type) {
			is Map<*, *> -> {
				it.write('{'.code)
				var next = false
				type.forEach { (key, value) ->
					it.write("${if (next) ", " else ""}\"$key\": ")
					write(value)
					next = true
				}
				it.write('}'.code)
			}

			false, true, null, is Number -> it.write(type.toString())
			else -> TODO("${type::class}")
		}

		write(config)
	}
}