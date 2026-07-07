package org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin

import org.bread_experts_group.breadlib.config.backend.builtin.abnf.ABNFRule
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.DIGIT
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.HEXDIG

// as defined in IETF RFC 8259
// TODO: Probably a good idea to put this as a separate library when the maven exists again

object ABNFJson {
	val value = ABNFRule.ABNFReference()

	val ws = ABNFRule.ABNFRepetition(
		rule = ABNFRule.ABNFAlternate(
			ABNFRule.ABNFCharacter(0x20),
			ABNFRule.ABNFCharacter(0x09),
			ABNFRule.ABNFCharacter(0x0A),
			ABNFRule.ABNFCharacter(0x0D)
		),
		name = "ws"
	)

	val `quotation-mark` = ABNFRule.ABNFCharacter(0x22, "quotation-mark")
	val unescaped = ABNFRule.ABNFAlternate(
		ABNFRule.ABNFCharacterRange(0x20..0x21L),
		ABNFRule.ABNFCharacterRange(0x23..0x5BL),
		ABNFRule.ABNFCharacterRange(0x5D..0x10FFFFL),
		name = "unescaped"
	)

	val escape = ABNFRule.ABNFCharacter(0x5C, "escape")

	val char = ABNFRule.ABNFAlternate(
		unescaped,
		ABNFRule.ABNFString(
			escape,
			ABNFRule.ABNFAlternate(
				ABNFRule.ABNFCharacter(0x22),
				ABNFRule.ABNFCharacter(0x5C),
				ABNFRule.ABNFCharacter(0x2F),
				ABNFRule.ABNFCharacter(0x62),
				ABNFRule.ABNFCharacter(0x66),
				ABNFRule.ABNFCharacter(0x6E),
				ABNFRule.ABNFCharacter(0x72),
				ABNFRule.ABNFCharacter(0x74),
				ABNFRule.ABNFString(
					ABNFRule.ABNFCharacter(0x75),
					ABNFRule.ABNFRepetition(4, 4, HEXDIG)
				),
			)
		),
		name = "char"
	)

	val string: ABNFRule.ABNFString = ABNFRule.ABNFString(
		`quotation-mark`,
		ABNFRule.ABNFRepetition(rule = char),
		`quotation-mark`,
		name = "string"
	)

	val `false` = ABNFRule.ABNFString(
		ABNFRule.ABNFCharacter(0x66),
		ABNFRule.ABNFCharacter(0x61),
		ABNFRule.ABNFCharacter(0x6C),
		ABNFRule.ABNFCharacter(0x73),
		ABNFRule.ABNFCharacter(0x65),
		name = "false"
	)

	val `null` = ABNFRule.ABNFString(
		ABNFRule.ABNFCharacter(0x6E),
		ABNFRule.ABNFCharacter(0x75),
		ABNFRule.ABNFCharacter(0x6C),
		ABNFRule.ABNFCharacter(0x6C),
		name = "null"
	)

	val `true` = ABNFRule.ABNFString(
		ABNFRule.ABNFCharacter(0x74),
		ABNFRule.ABNFCharacter(0x72),
		ABNFRule.ABNFCharacter(0x75),
		ABNFRule.ABNFCharacter(0x65),
		name = "true"
	)

	val `begin-object` = ABNFRule.ABNFString(ws, ABNFRule.ABNFCharacter(0x7B), ws, name = "begin-object")
	val `end-object` = ABNFRule.ABNFString(ws, ABNFRule.ABNFCharacter(0x7D), ws, name = "end-object")

	val `begin-array` = ABNFRule.ABNFString(ws, ABNFRule.ABNFCharacter(0x5B), ws, name = "begin-array")
	val `end-array` = ABNFRule.ABNFString(ws, ABNFRule.ABNFCharacter(0x5D), ws, name = "end-array")

	val `name-separator` = ABNFRule.ABNFString(ws, ABNFRule.ABNFCharacter(0x3A), ws, name = "name-separator")
	val `value-separator` = ABNFRule.ABNFString(ws, ABNFRule.ABNFCharacter(0x2C), ws, name = "value-separator")

	val member = ABNFRule.ABNFString(
		string,
		`name-separator`,
		value,
		name = "member"
	)

	val `object` = ABNFRule.ABNFString(
		`begin-object`,
		ABNFRule.ABNFRepetition(
			high = 1,
			rule = ABNFRule.ABNFString(
				member,
				ABNFRule.ABNFRepetition(
					rule = ABNFRule.ABNFString(`value-separator`, member)
				)
			)
		),
		`end-object`,
		name = "object"
	)

	val array: ABNFRule.ABNFString = ABNFRule.ABNFString(
		`begin-array`,
		ABNFRule.ABNFRepetition(
			high = 1,
			rule = ABNFRule.ABNFString(
				value,
				ABNFRule.ABNFRepetition(
					rule = ABNFRule.ABNFString(`value-separator`, value)
				)
			)
		),
		`end-array`,
		name = "array"
	)

	val zero = ABNFRule.ABNFCharacter(0x30, "zero")
	val `digit1-9` = ABNFRule.ABNFCharacterRange(0x31..0x39L, "digit1-9")
	val int = ABNFRule.ABNFAlternate(zero, ABNFRule.ABNFString(`digit1-9`, ABNFRule.ABNFRepetition(rule = DIGIT)), name = "int")
	val minus = ABNFRule.ABNFCharacter(0x2D, name = "minux")
	val plus = ABNFRule.ABNFCharacter(0x2B, name = "plus")
	val e = ABNFRule.ABNFAlternate(ABNFRule.ABNFCharacter(0x65), ABNFRule.ABNFCharacter(0x45), name = "e")
	val `decimal-point` = ABNFRule.ABNFCharacter(0x2E, "decimal-point")
	val frac = ABNFRule.ABNFString(`decimal-point`, ABNFRule.ABNFRepetition(1, rule = DIGIT), name = "frac")
	val exp = ABNFRule.ABNFString(
		e,
		ABNFRule.ABNFRepetition(
			high = 1,
			rule = ABNFRule.ABNFAlternate(minus, plus)
		),
		ABNFRule.ABNFRepetition(1, rule = DIGIT),
		name = "exp"
	)

	val number: ABNFRule.ABNFString = ABNFRule.ABNFString(
		ABNFRule.ABNFRepetition(high = 1, rule = minus),
		int,
		ABNFRule.ABNFRepetition(high = 1, rule = frac),
		ABNFRule.ABNFRepetition(high = 1, rule = exp),
		name = "number"
	)

	val `JSON-text` = ABNFRule.ABNFString(ws, value, ws, name = "JSON-text").also {
		value.rule = ABNFRule.ABNFAlternate(`false`, `null`, `true`, `object`, array, number, string, name = "value")
	}
}