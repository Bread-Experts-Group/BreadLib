package org.bread_experts_group.breadlib.config.backend.builtin.abnf

class ABNFReader(
	val input: String,
	var offset: Int = 0,
	var tasks: ArrayDeque<ABNFTask> = ArrayDeque(),
	var results: ArrayDeque<ABNFResolved> = ArrayDeque(),
	val retries: ArrayDeque<Triple<Int, ArrayDeque<ABNFTask>, ArrayDeque<ABNFResolved>>> = ArrayDeque(),
) {
	fun resolve(): Pair<ABNFResolved, Int> {
		iter@while (true) {
			task@while (tasks.isNotEmpty()) {
				val task = tasks.last()
				when (val rule = task.rule) {
					is ABNFRule.ABNFString -> {
						if (task.position > 0) {
							if (results.last() == ABNFResolved.ABNFNone) {
								tasks.removeLast()
								repeat(task.position) { results.removeLast() }
								results.add(ABNFResolved.ABNFNone)
								offset = task.savedOffset
								continue@task
							}
						}
						if (task.position < rule.concatenated.size) {
							tasks.add(ABNFTask(rule.concatenated[task.position++], 0, offset))
							continue@task
						}
						tasks.removeLast()
						results.add(ABNFResolved.ABNFString(rule, List(task.position) { results.removeLast() }.reversed()))
					}

					is ABNFRule.ABNFAlternate -> {
						tasks.removeLast()
						rule.alternates.reversed().forEach { r ->
							retries.add(
								Triple(
									offset,
									ArrayDeque<ABNFTask>().also { it.addAll(tasks.map { ABNFTask(it.rule, it.position, it.savedOffset) }); it.add(ABNFTask(r, 0, offset)) },
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
						if (task.position > 0) {
							val hitsLow = task.position.toUInt() >= rule.low
							if (results.last() == ABNFResolved.ABNFNone) {
								offset = task.savedOffset
								tasks.removeLast()
								results.removeLast()
								if (hitsLow) {
									results.add(ABNFResolved.ABNFRepetition(rule, List(task.position - 1) { results.removeLast() }.reversed()))
								} else {
									repeat(task.position - 1) { results.removeLast() }
									results.add(ABNFResolved.ABNFNone)
								}
								continue@task
							}
							if (rule.high != null && task.position.toUInt() == rule.high) {
								tasks.removeLast()
								results.add(ABNFResolved.ABNFRepetition(rule, List(task.position) { results.removeLast() }.reversed()))
								continue@task
							}
							if (hitsLow) task.savedOffset = offset
						}
						tasks.add(ABNFTask(rule.rule, 0, offset))
						task.position++
					}

					is ABNFRule.ABNFCharacter -> {
						tasks.removeLast()
						if (offset !in input.indices) {
							results.add(ABNFResolved.ABNFNone)
							continue
						}

						if (input[offset++].code.toUInt() == rule.character) {
							results.add(ABNFResolved.ABNFCharacter(rule, rule.character))
						} else {
							results.add(ABNFResolved.ABNFNone)
						}
					}

					is ABNFRule.ABNFCharacterRange -> {
						tasks.removeLast()
						if (offset !in input.indices) {
							results.add(ABNFResolved.ABNFNone)
							continue
						}

						val o = input[offset++]
						if (o.code.toUInt() in rule.characters) {
							results.add(ABNFResolved.ABNFCharacter(rule, o.code.toUInt()))
						} else {
							results.add(ABNFResolved.ABNFNone)
						}
					}

					is ABNFRule.ABNFReference -> {
						tasks.removeLast()
						tasks.add(ABNFTask(rule.rule!!, task.position, task.savedOffset))
					}
				}
			}

			val last = results.removeLast()
			if (retries.isNotEmpty() && (last == ABNFResolved.ABNFNone)) {
				val (offs, nT, nR) = retries.removeLast()
				offset = offs
				tasks = nT
				results = nR
				continue@iter
			}
			if (retries.isNotEmpty() && offset < input.length) {
				val (offs, nT, nR) = retries.removeLast()
				offset = offs
				tasks = nT
				results = nR
				continue@iter
			}
			return last to offset
		}
	}
}