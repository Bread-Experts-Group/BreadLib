package org.bread_experts_group.breadlib.config.backend.builtin.abnf

val DIGIT = ABNFRule.ABNFCharacterRange(0x30u..0x39u)

val HEXDIG = ABNFRule.ABNFAlternate(
	DIGIT,
	ABNFRule.ABNFAlternate.char('A'),
	ABNFRule.ABNFAlternate.char('B'),
	ABNFRule.ABNFAlternate.char('C'),
	ABNFRule.ABNFAlternate.char('D'),
	ABNFRule.ABNFAlternate.char('E'),
	ABNFRule.ABNFAlternate.char('F')
)

val ALPHA = ABNFRule.ABNFAlternate(
	ABNFRule.ABNFCharacterRange(0x41u..0x5Au),
	ABNFRule.ABNFCharacterRange(0x61u..0x7Au)
)

sealed class ABNFRule {
	abstract val name: String?
	class ABNFString(vararg val concatenated: ABNFRule, override val name: String? = null) : ABNFRule()
	class ABNFAlternate(vararg val alternates: ABNFRule, override val name: String? = null) : ABNFRule() {
		companion object {
			fun char(c: Char): ABNFAlternate {
				val u = c.uppercaseChar()
				val l = c.lowercaseChar()
				return if (u == l) ABNFAlternate(ABNFCharacter(c.code.toUInt()))
				else ABNFAlternate(ABNFCharacter(u.code.toUInt()), ABNFCharacter(l.code.toUInt()))
			}
		}
	}

	class ABNFRepetition(val low: UInt = 0u, val high: UInt? = null, val rule: ABNFRule, override val name: String? = null) : ABNFRule()

	class ABNFCharacter(val character: UInt, override val name: String? = null) : ABNFRule()
	class ABNFCharacterRange(val characters: UIntRange, override val name: String? = null) : ABNFRule()

	class ABNFReference(var rule: ABNFRule? = null) : ABNFRule() {
		override val name: String? = rule?.name
	}
}