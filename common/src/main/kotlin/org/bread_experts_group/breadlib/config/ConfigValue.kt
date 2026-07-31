package org.bread_experts_group.breadlib.config

import java.util.function.Supplier

data class ConfigValue<T>(val name: String, val defaultValue: Supplier<T>, val comment: String) {
	companion object {
		fun <T> builder(): Builder<T> = Builder()
	}

	class Builder<T> {
		private lateinit var name: String
		private lateinit var value: Supplier<T>
		private var comment: String = ""

		fun name(name: String): Builder<T> = this.also { this.name = name }
		fun defaultValue(value: Supplier<T>): Builder<T> = this.also { this.value = value }
		fun comment(comment: String): Builder<T> = this.also { this.comment = comment }

		fun build(): ConfigValue<T> = ConfigValue(this.name, this.value, this.comment)
	}
}