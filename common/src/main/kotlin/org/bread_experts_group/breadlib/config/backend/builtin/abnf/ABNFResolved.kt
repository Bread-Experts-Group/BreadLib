package org.bread_experts_group.breadlib.config.backend.builtin.abnf

sealed class ABNFResolved {
	abstract val rule: ABNFRule?
	data class ABNFString(override val rule: ABNFRule, val concatenated: List<ABNFResolved>) : ABNFResolved()
	data class ABNFAlternate(override val rule: ABNFRule, val selected: ABNFResolved) : ABNFResolved()
	data class ABNFRepetition(override val rule: ABNFRule, val selected: List<ABNFResolved>) : ABNFResolved()
	data class ABNFCharacter(override val rule: ABNFRule, val character: Long) : ABNFResolved()

	object ABNFNone : ABNFResolved() {
		override val rule: ABNFRule? = null
	}
}