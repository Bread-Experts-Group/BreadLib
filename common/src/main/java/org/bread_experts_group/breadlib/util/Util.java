package org.bread_experts_group.breadlib.util;

import java.util.Map;
import java.util.function.Supplier;

public class Util {
	public static <T, V> V getOrPut(Map<T, V> map, T key, Supplier<V> supplier) {
		V value = map.get(key);
		if (value == null) map.put(key, supplier.get());
		return map.get(key);
	}
}