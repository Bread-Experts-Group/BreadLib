package org.bread_experts_group.breadlib.config.backend.builtin.abnf.builtin

import org.bread_experts_group.breadlib.config.backend.builtin.abnf.ABNFRule.*
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.ALPHA
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.DIGIT
import org.bread_experts_group.breadlib.config.backend.builtin.abnf.HEXDIG

object ABNFToml {
	val wschar = ABNFAlternate(
		ABNFCharacter(0x20u),
		ABNFCharacter(0x09u),
		name = "wschar"
	)

	val ws = ABNFRepetition(rule = wschar, name = "ws")

	val `comment-start-symbol` = ABNFCharacter(0x23u, "comment-start-symbol")

	val `non-ascii` = ABNFAlternate(
		ABNFCharacterRange(0x80u..0xD7FFu),
		ABNFCharacterRange(0xE000u..0x10FFFFu),
		name = "non-ascii"
	)

	val `non-eol` = ABNFAlternate(
		ABNFCharacter(0x09u),
		ABNFCharacterRange(0x20u..0x7Eu),
		`non-ascii`,
		name = "non-eol"
	)

	val comment = ABNFString(
		`comment-start-symbol`,
		ABNFRepetition(rule = `non-eol`, name = "_non_eol_cmt"),
		name = "comment"
	)

	val `val` = ABNFReference()

	val `unquoted-key` = ABNFRepetition(
		1u,
		rule = ABNFAlternate(ALPHA, DIGIT, ABNFCharacter(0x2Du), ABNFCharacter(0x5Fu), name = "_key_char_rep"),
		name = "unquoted-key"
	)

	val `basic-string` = ABNFReference()

	val apostrophe = ABNFCharacter(0x27u, "apostrophe")

	val `literal-char` = ABNFAlternate(
		ABNFCharacter(0x09u),
		ABNFCharacterRange(0x20u..0x26u),
		ABNFCharacterRange(0x28u..0x7Eu),
		`non-ascii`,
		name = "literal-char"
	)

	val `literal-string` = ABNFString(
		apostrophe,
		ABNFRepetition(rule = `literal-char`, name = "_char_rep"),
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

	val `dot-sep` = ABNFString(ws, ABNFCharacter(0x2Eu), ws, name = "dot-sep")
	val `dotted-key` = ABNFString(
		`simple-key`,
		ABNFRepetition(1u, rule = ABNFString(`dot-sep`, `simple-key`, name = "_dotted_key_continuation")),
		name = "dotted-key"
	)

	val key = ABNFAlternate(`dotted-key`, `simple-key`, name = "key")

	val `keyval-sep` = ABNFString(ws, ABNFCharacter(0x3Du), ws, name = "keyval-sep")

	val keyval = ABNFString(key, `keyval-sep`, `val`, name = "keyval")

	val `std-table-open` = ABNFString(ABNFCharacter(0x5Bu), ws, name = "std-table-open")
	val `std-table-close` = ABNFString(ws, ABNFCharacter(0x5Du), name = "std-table-close")

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

	val expKv = ABNFString(ws, keyval, ws, ABNFRepetition(high = 1u, rule = comment, name = "_com_opt"), name = "_exp_kv")
	val expTb = ABNFString(ws, table, ws, ABNFRepetition(high = 1u, rule = comment, name = "_com_opt"), name = "_exp_tb")
	val expEm = ABNFString(ws, ABNFRepetition(high = 1u, rule = comment, name = "_com_opt"), name = "_exp_em")
	val expression = ABNFAlternate(
		expKv,
		expTb,
		expEm,
		name = "expression"
	)

	val newline = ABNFAlternate(
		ABNFCharacter(0x0Au),
		ABNFString(ABNFCharacter(0x0Du), ABNFCharacter(0x0Au), name = "_cr_lf"),
		name = "newline"
	)

	val `quotation-mark` = ABNFCharacter(0x22u, "quotation-mark")

	val `basic-unescaped` = ABNFAlternate(
		wschar,
		ABNFCharacter(0x21u),
		ABNFCharacterRange(0x23u..0x5Bu),
		ABNFCharacterRange(0x5Du..0x7Eu),
		`non-ascii`,
		name = "basic-unescaped"
	)

	val escape = ABNFCharacter(0x5Cu, "escape")

	val `escape-seq-char` = ABNFAlternate(
		ABNFCharacter(0x22u),
		ABNFCharacter(0x5Cu),
		ABNFCharacter(0x62u),
		ABNFCharacter(0x65u),
		ABNFCharacter(0x66u),
		ABNFCharacter(0x6Eu),
		ABNFCharacter(0x72u),
		ABNFCharacter(0x74u),
		ABNFString(ABNFCharacter(0x78u), ABNFRepetition(2u, 2u, HEXDIG, name = "_2hex"), name = "_hex2"),
		ABNFString(ABNFCharacter(0x75u), ABNFRepetition(4u, 4u, HEXDIG, name = "_4hex"), name = "_hex4"),
		ABNFString(ABNFCharacter(0x55u), ABNFRepetition(8u, 8u, HEXDIG, name = "_8hex"), name = "_hex8"),
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

	val `mlb-quotes` = ABNFRepetition(1u, 2u, `quotation-mark`, name = "mlb-quotes")

	val `mlb-escaped-nl` = ABNFString(
		escape,
		ws,
		newline,
		ABNFRepetition(rule = ABNFAlternate(wschar, newline), name = "_ws_nl_mlb"),
		name = "mlb-escaped-nl"
	)

	val `mlb-content` = ABNFAlternate(
		`basic-char`,
		newline,
		`mlb-escaped-nl`,
		name = "mlb-content"
	)

	val `ml-basic-body` = ABNFString(
		ABNFRepetition(rule = `mlb-content`, name = "_mlb_content_rep"),
		ABNFRepetition(rule = ABNFString(`mlb-quotes`, ABNFRepetition(1u, rule = `mlb-content`), name = "_mlb_quotes_then_content")),
		ABNFRepetition(high = 1u, rule = `mlb-quotes`, name = "_mlb_quotes_rep"),
		name = "ml-basic-body"
	)

	val `ml-basic-string-delim` = ABNFRepetition(3u, 3u, `quotation-mark`, name = "ml-basic-string-delim")
	val `ml-basic-string` = ABNFString(
		`ml-basic-string-delim`, ABNFRepetition(high = 1u, rule = newline, name = "_newline_opt"), `ml-basic-body`,
		`ml-basic-string-delim`,
		name = "ml-basic-string"
	)

// string = ml-literal-string

	val string = ABNFAlternate(
		`basic-string`,
//		`ml-basic-string`,
		`literal-string`,
		name = "string"
	)

	val `date-fullyear` = ABNFRepetition(4u, 4u, DIGIT, name = "date-fullyear")
	val `date-month` = ABNFRepetition(2u, 2u, DIGIT, name = "date-month")
	val `date-mday` = ABNFRepetition(2u, 2u, DIGIT, name = "date-mday")

	val `time-hour` = ABNFRepetition(2u, 2u, DIGIT, name = "time-hour")
	val `time-minute` = ABNFRepetition(2u, 2u, DIGIT, name = "time-minute")
	val `time-second` = ABNFRepetition(2u, 2u, DIGIT, name = "time-second")
	val `time-secfrac` = ABNFString(
		ABNFAlternate.char('.'),
		ABNFRepetition(1u, rule = DIGIT, name = "_frac_digits"),
		name = "time-secfrac"
	)

	val `full-date` = ABNFString(
		`date-fullyear`,
		ABNFAlternate.char('-'),
		`date-month`,
		ABNFAlternate.char('-'),
		`date-mday`,
		name = "full-date"
	)

	val `time-delim` = ABNFAlternate(
		ABNFAlternate.char('T'),
		ABNFCharacter(0x20u),
		name = "time-delim"
	)

	val `partial-time` = ABNFString(
		`time-hour`,
		ABNFAlternate.char(':'),
		`time-minute`,
		ABNFRepetition(
			high = 1u,
			rule = ABNFString(
				ABNFAlternate.char(':'),
				`time-second`,
				ABNFRepetition(
					high = 1u,
					rule = `time-secfrac`
				),
				name = "_seconds"
			),
			name = "_sec_opt"
		),
		name = "partial-time"
	)

	val `time-numoffset` = ABNFString(
		ABNFAlternate(
			ABNFAlternate.char('+'),
			ABNFAlternate.char('-'),
		),
		`time-hour`,
		ABNFAlternate.char(':'),
		`time-minute`,
		name = "time-numoffset"
	)

	val `time-offset` = ABNFAlternate(
		ABNFAlternate(
			ABNFAlternate.char('Z'),
			ABNFAlternate.char('z'),
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
		ABNFCharacter(0x74u),
		ABNFCharacter(0x72u),
		ABNFCharacter(0x75u),
		ABNFCharacter(0x65u),
		name = "true"
	)

	val `false` = ABNFString(
		ABNFCharacter(0x66u),
		ABNFCharacter(0x61u),
		ABNFCharacter(0x6Cu),
		ABNFCharacter(0x73u),
		ABNFCharacter(0x65u),
		name = "false"
	)

	val boolean = ABNFAlternate(
		`true`,
		`false`,
		name = "boolean"
	)

	val underscore = ABNFCharacter(0x5Fu, name = "underscore")

	val `hex-prefix` = ABNFString(
		ABNFCharacter(0x30u),
		ABNFCharacter(0x78u),
		name = "hex-prefix"
	)

	val `hex-int` = ABNFString(
		`hex-prefix`,
		HEXDIG,
		ABNFRepetition(
			rule = ABNFAlternate(HEXDIG, ABNFString(underscore, HEXDIG, name = "_hex_continuation")),
			name = "_hex_rep"
		),
		name = "hex-int"
	)

	val `digit1-9` = ABNFCharacterRange(0x31u..0x39u, name = "digit1-9")
	val `digit0-7` = ABNFCharacterRange(0x30u..0x37u, name = "digit0-7")
	val `digit0-1` = ABNFCharacterRange(0x30u..0x31u, name = "digit0-1")

	val `oct-prefix` = ABNFString(ABNFCharacter(0x30u), ABNFCharacter(0x6Fu), name = "oct-prefix")
	val `oct-int` = ABNFString(
		`oct-prefix`,
		`digit0-7`,
		ABNFRepetition(
			rule = ABNFAlternate(`digit0-7`, ABNFString(underscore, `digit0-7`, name = "_oct_continuation")),
			name = "_oct_rep"
		),
		name = "oct-int"
	)

	val `bin-prefix` = ABNFString(ABNFCharacter(0x30u), ABNFCharacter(0x62u), name = "bin-prefix")
	val `bin-int` = ABNFString(
		`bin-prefix`,
		`digit0-1`,
		ABNFRepetition(
			rule = ABNFAlternate(`digit0-1`, ABNFString(underscore, `digit0-1`, name = "_bin_continuatio")),
			name = "_bin_rep"
		),
		name = "bin-int"
	)

	val `unsigned-dec-int` = ABNFAlternate(
		DIGIT,
		ABNFString(`digit1-9`, ABNFRepetition(1u, rule = ABNFAlternate(DIGIT, ABNFString(underscore, DIGIT, name = "_dec_continuation")))),
		name = "unsigned-dec-int"
	)

	val minus = ABNFCharacter(0x2Du, name = "minus")
	val plus = ABNFCharacter(0x2Bu, name = "plus")
	val `dec-int` = ABNFString(
		ABNFRepetition(
			high = 1u,
			rule = ABNFAlternate(minus, plus),
			name = "_sign_opt"
		),
		`unsigned-dec-int`,
		name = "dec-int"
	)

	val integer = ABNFAlternate(
		`dec-int`,
		`hex-int`,
		`oct-int`,
		`bin-int`,
		name = "integer"
	)

	val `float-int-part` = `dec-int`
	val `zero-prefixable-int` = ABNFString(
		DIGIT,
		ABNFRepetition(rule = ABNFAlternate(DIGIT, ABNFString(underscore, DIGIT, name = "_int_continuation")), name = "_digit_rep"),
		name = "zero-prefixable-int"
	)

	val `float-exp-part` = ABNFString(
		ABNFRepetition(high = 1u, rule = ABNFAlternate(minus, plus), name = "_float_sign_opt"),
		`zero-prefixable-int`,
		name = "float-exp-part"
	)

	val exp = ABNFString(
		ABNFAlternate(ABNFAlternate.char('e'), ABNFAlternate.char('E')),
		`float-exp-part`,
		name = "exp"
	)

	val `decimal-point` = ABNFCharacter(0x2Eu, name = "decimal-point")
	val frac = ABNFString(
		`decimal-point`,
		`zero-prefixable-int`,
		name = "frac"
	)

	val inf = ABNFString(
		ABNFCharacter(0x69u),
		ABNFCharacter(0x6Eu),
		ABNFCharacter(0x66u),
		name = "inf"
	)

	val nan = ABNFString(
		ABNFCharacter(0x6Eu),
		ABNFCharacter(0x61u),
		ABNFCharacter(0x6Eu),
		name = "nan"
	)

	val `special-float` = ABNFString(
		ABNFRepetition(high = 1u, rule = ABNFAlternate(minus, plus), name = "_special_float_sign"),
		ABNFAlternate(inf, nan),
		name = "special-float"
	)

	val _float = ABNFString(`float-int-part`, ABNFAlternate(exp, ABNFString(frac, ABNFRepetition(high = 1u, rule = exp, name = "_exp_opt"), name = "_float_frac")), name = "_float")
	val float = ABNFAlternate(
		_float,
		`special-float`,
		name = "float"
	)

	val toml = ABNFString(expression, ABNFRepetition(rule = ABNFString(newline, expression, name = "_nl_expression"), name = "_nl_exp_rep"), name = "toml").also {
		`val`.rule = ABNFAlternate(string, boolean, `date-time`, integer, float, name = "val")
		`basic-string`.rule = ABNFString(
			`quotation-mark`,
			ABNFRepetition(rule = `basic-char`, name = "_char_rep"),
			`quotation-mark`,
			name = "basic-string"
		)
	}
}