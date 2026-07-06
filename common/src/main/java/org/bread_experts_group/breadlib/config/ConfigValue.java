package org.bread_experts_group.breadlib.config;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public record ConfigValue<T>(@Nullable T value, Supplier<T> defaultValue, String comment) {
	public static <T> Builder<T> builder() {
		return new Builder<>();
	}

	public static class Builder<T> {
		private Supplier<T> value;
		private String comment = "";

		private Builder() {
		}

		public Builder<T> defaultValue(Supplier<T> value) {
			this.value = value;
			return this;
		}

		public Builder<T> comment(String comment) {
			this.comment = comment;
			return this;
		}

		public ConfigValue<T> build() {
			return new ConfigValue<>(null, this.value, this.comment);
		}
	}
}