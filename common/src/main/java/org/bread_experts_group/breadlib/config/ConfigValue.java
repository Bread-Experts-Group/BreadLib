package org.bread_experts_group.breadlib.config;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

public record ConfigValue<T>(@NotNull String name, @Nullable Supplier<T> defaultValue, @NotNull String comment) {
	public static <T> Builder<T> builder() {
		return new Builder<>();
	}

	public static class Builder<T> {
		private String name;
		private Supplier<T> value;
		private String comment = "";

		private Builder() {
		}

		public Builder<T> name(String name) {
			this.name = name;
			return this;
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
			return new ConfigValue<>(
					Objects.requireNonNull(this.name),
					this.value,
					Objects.requireNonNull(this.comment)
			);
		}
	}
}