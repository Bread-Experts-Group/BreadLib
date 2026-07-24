package org.bread_experts_group.breadlib.util

import net.minecraft.network.chat.Component
import net.minecraft.network.chat.MutableComponent
import kotlin.math.roundToInt

object Color {
	@JvmStatic
	val NONE: Int = this.color(a = 0)

	@JvmStatic
	val WHITE: Int = this.color(255, 255, 255)

	@JvmStatic
	val BLACK: Int = this.color()

	@JvmStatic
	val GRAY: Int = this.color(128, 128, 128)

	@JvmStatic
	val LIGHT_GRAY: Int = this.color(192, 192, 192)

	@JvmStatic
	val DARK_GRAY: Int = this.color(64, 64, 64)

	@JvmStatic
	val RED: Int = this.color(255)

	@JvmStatic
	val PINK: Int = this.color(255, 175, 175)

	@JvmStatic
	val ORANGE: Int = this.color(255, 200)

	@JvmStatic
	val YELLOW: Int = this.color(255, 255)

	@JvmStatic
	val GREEN: Int = this.color(g = 255)

	@JvmStatic
	val MAGENTA: Int = this.color(r = 255, b = 255)

	@JvmStatic
	val CYAN: Int = this.color(g = 255, b = 255)

	@JvmStatic
	val BLUE: Int = this.color(b = 255)

	@JvmStatic
	val CORNFLOWER_BLUE: Int = this.color(97, 149, 237)

	@JvmStatic
	val SAFFRON: Int = this.color(243, 179, 48)

	@JvmStatic
	val LAVENDER_MAGENTA: Int = this.color(236, 91, 211)

	@JvmStatic
	val ALL_COLORS: List<Int> = listOf(
		this.WHITE, this.BLACK, this.GRAY, this.LIGHT_GRAY,
		this.DARK_GRAY, this.RED, this.PINK, this.ORANGE,
		this.YELLOW, this.GREEN, this.MAGENTA, this.CYAN,
		this.BLUE, this.CORNFLOWER_BLUE, this.SAFFRON, this.LAVENDER_MAGENTA
	)

	@JvmStatic
	fun randomColor(): Int = this.ALL_COLORS.random()

	fun Int.component(literal: String): MutableComponent = Component.literal(literal).withColor(this)
	fun Char.component(color: Int): MutableComponent = Component.literal(this.toString()).withColor(color)
	fun String.component(color: Int): MutableComponent = Component.literal(this).withColor(color)
	fun mix(vararg colors: Int): Int {
		val mixed = FloatArray(4)
		colors.forEach {
			val array = it.argbArray()
			mixed[0] += array[0]
			mixed[1] += array[1]
			mixed[2] += array[2]
			mixed[3] += array[3]
		}
		mixed.forEachIndexed { index, f -> mixed[index] = f / colors.size }
		return this.color(mixed)
	}

	fun Int.argbArray(): FloatArray = floatArrayOf(
		((this and 0xFF000000.toInt()) ushr 24) / 255f,
		((this and 0x00FF0000) ushr 16) / 255f,
		((this and 0x0000FF00) ushr 8) / 255f,
		(this and 0x000000FF) / 255f,
	)

	/**
	 * ARGB
	 */
	fun color(array: FloatArray): Int = this.color(
		(array[1] * 255).roundToInt(),
		(array[2] * 255).roundToInt(),
		(array[3] * 255).roundToInt(),
		(array[0] * 255).roundToInt(),
	)

	/**
	 * ARGB32
	 */
	@JvmStatic
	fun color(r: Int = 0, g: Int = 0, b: Int = 0, a: Int = 255): Int =
		(((a and 0xFF) shl 24) or ((r and 0xFF) shl 16) or ((g and 0xFF) shl 8) or (b and 0xFF))
}