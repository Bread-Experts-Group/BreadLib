package org.bread_experts_group.breadlib.util

class KV1Values<V1>(private val map: Map<*, Pair<V1, *>>) : Collection<V1> {
	override val size: Int
		get() = map.size

	override fun isEmpty(): Boolean = map.isEmpty()
	override fun contains(element: V1): Boolean = map.contains(element)
	override fun iterator(): Iterator<V1> = object : Iterator<V1> {
		val kIterator = map.keys.iterator()
		override fun next(): V1 = map[kIterator.next()]?.first ?: throw NoSuchElementException()
		override fun hasNext(): Boolean = kIterator.hasNext()
	}

	override fun containsAll(elements: Collection<V1>): Boolean {
		var status = true
		for (element in iterator()) {
			if (!elements.contains(element)) {
				status = false
				break
			}
		}
		return status
	}
}

class KV2Values<V2>(private val map: Map<*, Pair<*, V2>>) : Collection<V2> {
	override val size: Int
		get() = map.size

	override fun isEmpty(): Boolean = map.isEmpty()
	override fun contains(element: V2): Boolean = map.contains(element)
	override fun iterator(): Iterator<V2> = object : Iterator<V2> {
		val kIterator = map.keys.iterator()
		override fun next(): V2 = map[kIterator.next()]?.second ?: throw NoSuchElementException()
		override fun hasNext(): Boolean = kIterator.hasNext()
	}

	override fun containsAll(elements: Collection<V2>): Boolean {
		var status = true
		for (element in iterator()) {
			if (!elements.contains(element)) {
				status = false
				break
			}
		}
		return status
	}
}

fun <K, V1, V2> Map<K, Pair<V1, V2>>.toMapKV1(): Map<K, V1> = object : Map<K, V1> {
	override val size: Int
		get() = this@toMapKV1.size
	override val keys: Set<K>
		get() = this@toMapKV1.keys
	override val values: Collection<V1> = KV1Values(this@toMapKV1)
	override val entries: Set<Map.Entry<K, V1>> = object : Set<Map.Entry<K, V1>> {
		override val size: Int
			get() = this@toMapKV1.size

		override fun isEmpty(): Boolean = this@toMapKV1.isEmpty()
		override fun contains(element: Map.Entry<K, V1>): Boolean = this@toMapKV1[element.key] == element.value
		override fun iterator(): Iterator<Map.Entry<K, V1>> = object : Iterator<Map.Entry<K, V1>> {
			val kIterator = this@toMapKV1.keys.iterator()
			override fun next(): Map.Entry<K, V1> {
				val key = kIterator.next()
				return object : Map.Entry<K, V1> {
					override val key: K = key
					override val value: V1 = this@toMapKV1[key]?.first ?: throw NoSuchElementException()
				}
			}

			override fun hasNext(): Boolean = kIterator.hasNext()
		}

		override fun containsAll(elements: Collection<Map.Entry<K, V1>>): Boolean {
			var status = true
			for (element in iterator()) {
				if (!elements.contains(element)) {
					status = false
					break
				}
			}
			return status
		}
	}

	override fun isEmpty(): Boolean = this@toMapKV1.isEmpty()
	override fun containsKey(key: K): Boolean = this@toMapKV1.containsKey(key)
	override fun containsValue(value: V1): Boolean = this@toMapKV1.any { (_, value1) -> value1.first == value }
	override fun get(key: K): V1? = this@toMapKV1[key]?.first
}

fun <K, V1, V2> Map<K, Pair<V1, V2>>.toMapKV2(): Map<K, V2> = object : Map<K, V2> {
	override val size: Int
		get() = this@toMapKV2.size
	override val keys: Set<K>
		get() = this@toMapKV2.keys
	override val values: Collection<V2> = KV2Values(this@toMapKV2)
	override val entries: Set<Map.Entry<K, V2>> = object : Set<Map.Entry<K, V2>> {
		override val size: Int
			get() = this@toMapKV2.size

		override fun isEmpty(): Boolean = this@toMapKV2.isEmpty()
		override fun contains(element: Map.Entry<K, V2>): Boolean = this@toMapKV2[element.key] == element.value
		override fun iterator(): Iterator<Map.Entry<K, V2>> = object : Iterator<Map.Entry<K, V2>> {
			val kIterator = this@toMapKV2.keys.iterator()
			override fun next(): Map.Entry<K, V2> {
				val key = kIterator.next()
				return object : Map.Entry<K, V2> {
					override val key: K = key
					override val value: V2 = this@toMapKV2[key]?.second ?: throw NoSuchElementException()
				}
			}

			override fun hasNext(): Boolean = kIterator.hasNext()
		}

		override fun containsAll(elements: Collection<Map.Entry<K, V2>>): Boolean {
			var status = true
			for (element in iterator()) {
				if (!elements.contains(element)) {
					status = false
					break
				}
			}
			return status
		}
	}

	override fun isEmpty(): Boolean = this@toMapKV2.isEmpty()
	override fun containsKey(key: K): Boolean = this@toMapKV2.containsKey(key)
	override fun containsValue(value: V2): Boolean = this@toMapKV2.any { (_, value1) -> value1.second == value }
	override fun get(key: K): V2? = this@toMapKV2[key]?.second
}