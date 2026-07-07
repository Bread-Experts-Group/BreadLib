package org.bread_experts_group.breadlib.config.backend.builtin.abnf

val DIGIT = ABNFRule.ABNFCharacterRange(0x30..0x39L)

val HEXDIG = ABNFRule.ABNFAlternate(
	DIGIT,
	ABNFRule.ABNFAlternate(ABNFRule.ABNFCharacter('A'.code.toLong()), ABNFRule.ABNFCharacter('a'.code.toLong())),
	ABNFRule.ABNFAlternate(ABNFRule.ABNFCharacter('B'.code.toLong()), ABNFRule.ABNFCharacter('b'.code.toLong())),
	ABNFRule.ABNFAlternate(ABNFRule.ABNFCharacter('C'.code.toLong()), ABNFRule.ABNFCharacter('c'.code.toLong())),
	ABNFRule.ABNFAlternate(ABNFRule.ABNFCharacter('D'.code.toLong()), ABNFRule.ABNFCharacter('d'.code.toLong())),
	ABNFRule.ABNFAlternate(ABNFRule.ABNFCharacter('E'.code.toLong()), ABNFRule.ABNFCharacter('e'.code.toLong())),
	ABNFRule.ABNFAlternate(ABNFRule.ABNFCharacter('F'.code.toLong()), ABNFRule.ABNFCharacter('f'.code.toLong()))
)

val ALPHA = ABNFRule.ABNFAlternate(
	ABNFRule.ABNFCharacterRange(0x41..0x5AL),
	ABNFRule.ABNFCharacterRange(0x61..0x7AL)
)

sealed class ABNFRule {
	abstract val name: String?
	class ABNFString(vararg val concatenated: ABNFRule, override val name: String? = null) : ABNFRule()
	class ABNFAlternate(vararg val alternates: ABNFRule, override val name: String? = null) : ABNFRule()
	class ABNFRepetition(val low: Long = 0, val high: Long = -1, val rule: ABNFRule, override val name: String? = null) : ABNFRule()

	class ABNFCharacter(val character: Long, override val name: String? = null) : ABNFRule()
	class ABNFCharacterRange(val characters: LongRange, override val name: String? = null) : ABNFRule()

	class ABNFReference(var rule: ABNFRule? = null) : ABNFRule() {
		override val name: String? = rule?.name
	}
}