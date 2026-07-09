package org.bread_experts_group.breadlib.config.backend.builtin.abnf

class ABNFReader(
	val input: String,
	var offset: Int = 0,
	var tasks: ArrayDeque<ABNFRule> = ArrayDeque(),
	var results: ArrayDeque<ABNFResolved> = ArrayDeque(),
	val retries: ArrayDeque<Triple<Int, ArrayDeque<ABNFRule>, ArrayDeque<ABNFResolved>>> = ArrayDeque()
) {
	fun resolve(): Pair<ABNFResolved, Int> {
		var sv = offset
		task@while (tasks.isNotEmpty()) when (val task = tasks.removeLast()) {
			is ABNFRule.ABNFString -> {
				val alpha = mutableListOf<ABNFResolved>()
				val sv = offset
				for (rule in task.concatenated) {
					val (alp, ha) = ABNFReader(
						input,
						offset,
						ArrayDeque<ABNFRule>().also { it.add(rule) },
						ArrayDeque(),
					).resolve()
					if (alp == ABNFResolved.ABNFNone) {
						offset = sv
						results.add(ABNFResolved.ABNFNone)
						continue@task
					} else {
						offset = ha
						alpha.add(alp)
					}
				}
				results.add(ABNFResolved.ABNFString(task, alpha))
			}

			is ABNFRule.ABNFAlternate -> {
				task.alternates.forEach { r ->
					retries.add(
						Triple(
							offset,
							ArrayDeque<ABNFRule>().also { it.addAll(tasks); it.add(r) },
							ArrayDeque<ABNFResolved>().also { it.addAll(results) }
						)
					)
				}
				val (offs, nT, nR) = retries.removeLast()
				offset = offs
				tasks = nT
				results = nR
			}

			is ABNFRule.ABNFRepetition -> {
				var i = 0u
				val m = mutableListOf<ABNFResolved>()
				val sv = offset
				while (task.high == null || i < task.high) {
					val rsv = ABNFReader(
						input,
						offset,
						ArrayDeque<ABNFRule>().also { it.add(task.rule) },
						ArrayDeque(),
					).resolve()
					if (rsv.first != ABNFResolved.ABNFNone) {
						offset = rsv.second
						m.add(rsv.first)
					} else {
						break
					}
					i++
				}
				if (i >= task.low) {
					results.add(ABNFResolved.ABNFRepetition(task, m))
				} else {
					results.add(ABNFResolved.ABNFNone)
					offset = sv
				}
			}

			is ABNFRule.ABNFCharacter -> {
				if (offset !in input.indices) {
					results.add(ABNFResolved.ABNFNone)
					continue
				}

				if (input[offset++].code.toUInt() == task.character) {
					results.add(ABNFResolved.ABNFCharacter(task, task.character))
				} else {
					results.add(ABNFResolved.ABNFNone)
				}
			}

			is ABNFRule.ABNFCharacterRange -> {
				if (offset !in input.indices) {
					results.add(ABNFResolved.ABNFNone)
					continue
				}

				val o = input[offset++]
				if (o.code.toUInt() in task.characters) {
					results.add(ABNFResolved.ABNFCharacter(task, o.code.toUInt()))
				} else {
					results.add(ABNFResolved.ABNFNone)
				}
			}

			is ABNFRule.ABNFReference -> {
				tasks.add(task.rule!!)
			}
		}
		var l = results.removeLast()
		while ((l == ABNFResolved.ABNFNone || sv == offset) && retries.isNotEmpty()) {
			val (offs, nT, nR) = retries.removeLast()
			offset = offs
			tasks = nT
			results = nR
			val (a, b) = this.resolve()
			l = a
			offset = b
		}
		return l to offset
	}
}