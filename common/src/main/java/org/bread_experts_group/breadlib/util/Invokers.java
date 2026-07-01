package org.bread_experts_group.breadlib.util;

public interface Invokers {
	@FunctionalInterface
	interface One<F, R> {
		R invoke(F first);
	}

	@FunctionalInterface
	interface Two<F, S, R> {
		R invoke(F first, S second);
	}

	@FunctionalInterface
	interface Three<F, S, T, R> {
		R invoke(F first, S second, T third);
	}
}
