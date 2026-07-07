package org.bread_experts_group.breadlib.config.backend.builtin.abnf

import org.bread_experts_group.breadlib.config.backend.builtin.abnf.ABNFRule.ABNFAlternate
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.ABNFRule.ABNFString

object ABNFToml {
	val wschar = ABNFAlternate(
		ABNFRule.ABNFCharacter(0x20),
		ABNFRule.ABNFCharacter(0x09),
		name = "wschar"
	)

	val ws = ABNFRule.ABNFRepetition(rule = wschar, name = "ws")

	val `comment-start-symbol` = ABNFRule.ABNFCharacter(0x23, "comment-start-symbol")

	val `non-ascii` = ABNFAlternate(
		ABNFRule.ABNFCharacterRange(0x80..0xD7FFL),
		ABNFRule.ABNFCharacterRange(0xE000..0x10FFFFL),
		name = "non-ascii"
	)

	val `non-eol` = ABNFAlternate(
		ABNFRule.ABNFCharacter(0x09),
		ABNFRule.ABNFCharacterRange(0x20..0x7EL),
		`non-ascii`,
		name = "non-eol"
	)

	val comment = ABNFString(
		`comment-start-symbol`,
		ABNFRule.ABNFRepetition(rule = `non-eol`),
		name = "comment"
	)

	val `val` = ABNFRule.ABNFReference()

	val `unquoted-key` = ABNFRule.ABNFRepetition(
		1,
		rule = ABNFAlternate(ALPHA, DIGIT, ABNFRule.ABNFCharacter(0x2D), ABNFRule.ABNFCharacter(0x5F)),
		name = "unquoted-key"
	)

	val `basic-string` = ABNFRule.ABNFReference()

	val apostrophe = ABNFRule.ABNFCharacter(0x27, "apostrophe")

	val `literal-char` = ABNFAlternate(
		ABNFRule.ABNFCharacter(0x09),
		ABNFRule.ABNFCharacterRange(0x20..0x26L),
		ABNFRule.ABNFCharacterRange(0x28..0x7EL),
		`non-ascii`,
		name = "literal-char"
	)

	val `literal-string` = ABNFString(
		apostrophe,
		ABNFRule.ABNFRepetition(rule = `literal-char`),
		apostrophe,
		name = "literal-string"
	)

	val `quoted-key` = ABNFAlternate(
		`basic-string`,
		`literal-string`,
		name = "quoted-key"
	)

	val `simple-key` = ABNFAlternate(
		`quoted-key`,
		`unquoted-key`,
		name = "simple-key"
	)

	val `dot-sep` = ABNFString(ws, ABNFRule.ABNFCharacter(0x2E), ws, name = "dot-sep")
	val `dotted-key` = ABNFString(
		`simple-key`,
		ABNFRule.ABNFRepetition(1, rule = ABNFString(`dot-sep`, `simple-key`)),
		name = "dotted-key"
	)

	val key = ABNFAlternate(`dotted-key`, `simple-key`, name = "key")

	val `keyval-sep` = ABNFString(ws, ABNFRule.ABNFCharacter(0x3D), ws, name = "keyval-sep")

	val keyval = ABNFString(key, `keyval-sep`, `val`, name = "keyval")

	val `std-table-open` = ABNFString(ABNFRule.ABNFCharacter(0x5B), ws, name = "std-table-open")
	val `std-table-close` = ABNFString(ws, ABNFRule.ABNFCharacter(0x5D), name = "std-table-close")

	val `std-table` = ABNFString(
		`std-table-open`,
		key,
		`std-table-close`,
		name = "std-table"
	)

	val table = ABNFAlternate(
		`std-table`,
//	`array-table`
		name = "table"
	)

	val expression = ABNFAlternate(
		ABNFString(ws, keyval, ws, ABNFRule.ABNFRepetition(high = 1, rule = comment)),
		ABNFString(ws, table, ws, ABNFRule.ABNFRepetition(high = 1, rule = comment)),
		ABNFString(ws, ABNFRule.ABNFRepetition(high = 1, rule = comment)),
		name = "expression"
	)

	val newline = ABNFAlternate(
		ABNFRule.ABNFCharacter(0x0A),
		ABNFString(ABNFRule.ABNFCharacter(0x0D), ABNFRule.ABNFCharacter(0x0A)),
		name = "newline"
	)

	val `quotation-mark` = ABNFRule.ABNFCharacter(0x22, "quotation-mark")

	val `basic-unescaped` = ABNFAlternate(
		wschar,
		ABNFRule.ABNFCharacter(0x21),
		ABNFRule.ABNFCharacterRange(0x23..0x5BL),
		ABNFRule.ABNFCharacterRange(0x5D..0x7EL),
		`non-ascii`,
		name = "basic-unescaped"
	)

	val escape = ABNFRule.ABNFCharacter(0x5C, "escape")

	val `escape-seq-char` = ABNFAlternate(
		ABNFRule.ABNFCharacter(0x22),
		ABNFRule.ABNFCharacter(0x5C),
		ABNFRule.ABNFCharacter(0x62),
		ABNFRule.ABNFCharacter(0x65),
		ABNFRule.ABNFCharacter(0x66),
		ABNFRule.ABNFCharacter(0x6E),
		ABNFRule.ABNFCharacter(0x72),
		ABNFRule.ABNFCharacter(0x74),
		ABNFString(ABNFRule.ABNFCharacter(0x78), ABNFRule.ABNFRepetition(2, 2, HEXDIG)),
		ABNFString(ABNFRule.ABNFCharacter(0x75), ABNFRule.ABNFRepetition(4, 4, HEXDIG)),
		ABNFString(ABNFRule.ABNFCharacter(0x55), ABNFRule.ABNFRepetition(8, 8, HEXDIG)),
		name = "escape-seq-char"
	)

	val escaped = ABNFString(
		escape,
		`escape-seq-char`,
		name = "escaped"
	)

	val `basic-char` = ABNFAlternate(
		`basic-unescaped`,
		escaped,
		name = "basic-char"
	)

	val `mlb-quotes` = ABNFRule.ABNFRepetition(1, 2, `quotation-mark`, name = "mlb-quotes")

	val `mlb-escaped-nl` = ABNFString(
		escape,
		ws,
		newline,
		ABNFRule.ABNFRepetition(rule = ABNFAlternate(wschar, newline)),
		name = "mlb-escaped-nl"
	)

	val `mlb-content` = ABNFAlternate(
		`mlb-escaped-nl`,
		`basic-char`,
		newline,
		name = "mlb-content"
	)

	val `ml-basic-body` = ABNFString(
		ABNFRule.ABNFRepetition(rule = `mlb-content`),
		ABNFRule.ABNFRepetition(rule = ABNFString(`mlb-quotes`, ABNFRule.ABNFRepetition(1, rule = `mlb-content`))),
		//ABNFRule.ABNFRepetition(high = 1, rule = `mlb-quotes`) // TODO: The ABNF reader can't understand this yet
		name = "ml-basic-body"
	)

	val `ml-basic-string-delim` = ABNFRule.ABNFRepetition(3, 3, `quotation-mark`, name = "ml-basic-string-delim")
	val `ml-basic-string` = ABNFString(
		`ml-basic-string-delim`, ABNFRule.ABNFRepetition(high = 1, rule = newline), `ml-basic-body`,
		`ml-basic-string-delim`,
		name = "ml-basic-string"
	)

// string = ml-literal-string

	val string = ABNFAlternate(
		`basic-string`,
		`literal-string`,
		name = "string"
	)

	val `date-fullyear` = ABNFRule.ABNFRepetition(4, 4, DIGIT, name = "date-fullyear")
	val `date-month` = ABNFRule.ABNFRepetition(2, 2, DIGIT, name = "date-month")
	val `date-mday` = ABNFRule.ABNFRepetition(2, 2, DIGIT, name = "date-mday")

	val `time-hour` = ABNFRule.ABNFRepetition(2, 2, DIGIT, name = "time-hour")
	val `time-minute` = ABNFRule.ABNFRepetition(2, 2, DIGIT, name = "time-minute")
	val `time-second` = ABNFRule.ABNFRepetition(2, 2, DIGIT, name = "time-second")
	val `time-secfrac` = ABNFString(
		ABNFRule.ABNFCharacter('.'.code.toLong()),
		ABNFRule.ABNFRepetition(1, rule = DIGIT),
		name = "time-secfrac"
	)

	val `full-date` = ABNFString(
		`date-fullyear`,
		ABNFRule.ABNFCharacter('-'.code.toLong()),
		`date-month`,
		ABNFRule.ABNFCharacter('-'.code.toLong()),
		`date-mday`,
		name = "full-date"
	)

	val `time-delim` = ABNFAlternate(
		ABNFAlternate(
			ABNFRule.ABNFCharacter('T'.code.toLong()),
			ABNFRule.ABNFCharacter('t'.code.toLong()),
		),
		ABNFRule.ABNFCharacter(0x20),
		name = "time-delim"
	)

	val `partial-time` = ABNFString(
		`time-hour`,
		ABNFRule.ABNFCharacter(':'.code.toLong()),
		`time-minute`,
		ABNFRule.ABNFRepetition(
			high = 1,
			rule = ABNFString(
				ABNFRule.ABNFCharacter(':'.code.toLong()),
				`time-second`,
				ABNFRule.ABNFRepetition(
					high = 1,
					rule = `time-secfrac`
				)
			)
		),
		name = "partial-time"
	)

	val `time-numoffset` = ABNFString(
		ABNFAlternate(
			ABNFRule.ABNFCharacter('+'.code.toLong()),
			ABNFRule.ABNFCharacter('-'.code.toLong()),
		),
		`time-hour`,
		ABNFRule.ABNFCharacter(':'.code.toLong()),
		`time-minute`,
		name = "time-numoffset"
	)

	val `time-offset` = ABNFAlternate(
		ABNFAlternate(
			ABNFRule.ABNFCharacter('Z'.code.toLong()),
			ABNFRule.ABNFCharacter('z'.code.toLong()),
		),
		`time-numoffset`,
		name = "time-offset"
	)

	val `full-time` = ABNFString(
		`partial-time`,
		`time-offset`,
		name = "full-time"
	)

	val `offset-date-time` = ABNFString(
		`full-date`,
		`time-delim`,
		`full-time`,
		name = "offset-date-time"
	)

	val `date-time` = ABNFAlternate(
		`offset-date-time`,
//	`local-date-time`,
//	`local-date`,
//	`local-time`
		name = "date-time"
	)

	val `true` = ABNFString(
		ABNFRule.ABNFCharacter(0x74),
		ABNFRule.ABNFCharacter(0x72),
		ABNFRule.ABNFCharacter(0x75),
		ABNFRule.ABNFCharacter(0x65),
		name = "true"
	)

	val `false` = ABNFString(
		ABNFRule.ABNFCharacter(0x66),
		ABNFRule.ABNFCharacter(0x61),
		ABNFRule.ABNFCharacter(0x6C),
		ABNFRule.ABNFCharacter(0x73),
		ABNFRule.ABNFCharacter(0x65),
		name = "false"
	)

	val boolean = ABNFAlternate(
		`true`,
		`false`,
		name = "boolean"
	)

	val toml = ABNFString(expression, ABNFRule.ABNFRepetition(rule = ABNFString(newline, expression)), name = "toml").also {
		`val`.rule = ABNFAlternate(string, boolean, `date-time`, name = "val")
		`basic-string`.rule = ABNFString(
			`quotation-mark`,
			ABNFRule.ABNFRepetition(rule = `basic-char`),
			`quotation-mark`,
			name = "basic-string"
		)
	}
}