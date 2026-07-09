package org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin

import org.bread_experts_group.breadlib.config.backend.builtin.abnf.ABNFRule.*
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.DIGIT
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.HEXDIG

// as defined in IETF RFC 8259
// TODO: Probably a good idea to put this as a separate library when the maven exists again

object ABNFJson {
	val value = ABNFReference()

	val ws = ABNFRepetition(
		rule = ABNFAlternate(
			ABNFCharacter(0x20u),
			ABNFCharacter(0x09u),
			ABNFCharacter(0x0Au),
			ABNFCharacter(0x0Du)
		),
		name = "ws"
	)

	val `quotation-mark` = ABNFCharacter(0x22u, "quotation-mark")
	val unescaped = ABNFAlternate(
		ABNFCharacterRange(0x20u..0x21u),
		ABNFCharacterRange(0x23u..0x5Bu),
		ABNFCharacterRange(0x5Du..0x10FFFFu),
		name = "unescaped"
	)

	val escape = ABNFCharacter(0x5Cu, "escape")

	val char = ABNFAlternate(
		unescaped,
		ABNFString(
			escape,
			ABNFAlternate(
				ABNFCharacter(0x22u),
				ABNFCharacter(0x5Cu),
				ABNFCharacter(0x2Fu),
				ABNFCharacter(0x62u),
				ABNFCharacter(0x66u),
				ABNFCharacter(0x6Eu),
				ABNFCharacter(0x72u),
				ABNFCharacter(0x74u),
				ABNFString(
					ABNFCharacter(0x75u),
					ABNFRepetition(4u, 4u, HEXDIG)
				),
			)
		),
		name = "char"
	)

	val string: ABNFString = ABNFString(
		`quotation-mark`,
		ABNFRepetition(rule = char),
		`quotation-mark`,
		name = "string"
	)

	val `false` = ABNFString(
		ABNFCharacter(0x66u),
		ABNFCharacter(0x61u),
		ABNFCharacter(0x6Cu),
		ABNFCharacter(0x73u),
		ABNFCharacter(0x65u),
		name = "false"
	)

	val `null` = ABNFString(
		ABNFCharacter(0x6Eu),
		ABNFCharacter(0x75u),
		ABNFCharacter(0x6Cu),
		ABNFCharacter(0x6Cu),
		name = "null"
	)

	val `true` = ABNFString(
		ABNFCharacter(0x74u),
		ABNFCharacter(0x72u),
		ABNFCharacter(0x75u),
		ABNFCharacter(0x65u),
		name = "true"
	)

	val `begin-object` = ABNFString(ws, ABNFCharacter(0x7Bu), ws, name = "begin-object")
	val `end-object` = ABNFString(ws, ABNFCharacter(0x7Du), ws, name = "end-object")

	val `begin-array` = ABNFString(ws, ABNFCharacter(0x5Bu), ws, name = "begin-array")
	val `end-array` = ABNFString(ws, ABNFCharacter(0x5Du), ws, name = "end-array")

	val `name-separator` = ABNFString(ws, ABNFCharacter(0x3Au), ws, name = "name-separator")
	val `value-separator` = ABNFString(ws, ABNFCharacter(0x2Cu), ws, name = "value-separator")

	val member = ABNFString(
		string,
		`name-separator`,
		value,
		name = "member"
	)

	val `object` = ABNFString(
		`begin-object`,
		ABNFRepetition(
			high = 1u,
			rule = ABNFString(
				member,
				ABNFRepetition(
					rule = ABNFString(`value-separator`, member)
				)
			)
		),
		`end-object`,
		name = "object"
	)

	val array: ABNFString = ABNFString(
		`begin-array`,
		ABNFRepetition(
			high = 1u,
			rule = ABNFString(
				value,
				ABNFRepetition(
					rule = ABNFString(`value-separator`, value)
				)
			)
		),
		`end-array`,
		name = "array"
	)

	val zero = ABNFCharacter(0x30u, "zero")
	val `digit1-9` = ABNFCharacterRange(0x31u..0x39u, "digit1-9")
	val int = ABNFAlternate(zero, ABNFString(`digit1-9`, ABNFRepetition(rule = DIGIT)), name = "int")
	val minus = ABNFCharacter(0x2Du, name = "minux")
	val plus = ABNFCharacter(0x2Bu, name = "plus")
	val e = ABNFAlternate(ABNFCharacter(0x65u), ABNFCharacter(0x45u), name = "e")
	val `decimal-point` = ABNFCharacter(0x2Eu, "decimal-point")
	val frac = ABNFString(`decimal-point`, ABNFRepetition(1u, rule = DIGIT), name = "frac")
	val exp = ABNFString(
		e,
		ABNFRepetition(
			high = 1u,
			rule = ABNFAlternate(minus, plus)
		),
		ABNFRepetition(1u, rule = DIGIT),
		name = "exp"
	)

	val number: ABNFString = ABNFString(
		ABNFRepetition(high = 1u, rule = minus),
		int,
		ABNFRepetition(high = 1u, rule = frac),
		ABNFRepetition(high = 1u, rule = exp),
		name = "number"
	)

	val `JSON-text` = ABNFString(ws, value, ws, name = "JSON-text").also {
		value.rule = ABNFAlternate(`false`, `null`, `true`, `object`, array, number, string, name = "value")
	}
}